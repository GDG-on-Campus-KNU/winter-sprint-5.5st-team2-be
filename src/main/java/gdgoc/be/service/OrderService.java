package gdgoc.be.service;

import gdgoc.be.Repository.*;
import gdgoc.be.common.util.SecurityUtil;
import gdgoc.be.domain.Order;
import gdgoc.be.domain.OrderItem;
import gdgoc.be.domain.Product;
import gdgoc.be.domain.User;
import gdgoc.be.domain.UserCoupon;
import gdgoc.be.dto.CalculationResult;
import gdgoc.be.dto.order.OrderItemRequest;
import gdgoc.be.dto.order.OrderRequest;
import gdgoc.be.dto.order.OrderResponse;
import gdgoc.be.dto.user.UserCouponResponse;
import gdgoc.be.exception.BusinessErrorCode;
import gdgoc.be.exception.BusinessException;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;
    private final OrderCalculator orderCalculator;
    private final CartItemRepository cartItemRepository;

    @Transactional(readOnly = true)
    public Page<OrderResponse> getMyOrders(Pageable pageable) {
        String email = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_FOUND));

        return orderRepository.findByUserIdOrderByOrderDateDesc(user.getId(), pageable)
                .map(OrderResponse::from);
    }

    public OrderResponse createOrder(OrderRequest request) {

        String email = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_FOUND));

        List<OrderItem> orderItems = createOrderItems(request.orderItems(), user.getId());

        CalculationResult calculation = orderCalculator.calculate(orderItems);

        Order order = Order.createOrder(
                user,
                calculation,
                null, // 주문 레벨 쿠폰은 더 이상 사용하지 않음
                request.shippingAddress()
        );
        orderItems.forEach(order::addOrderItem);

        // 개별 적용된 모든 쿠폰 사용 처리
        orderItems.stream()
                .map(OrderItem::getAppliedCoupon)
                .filter(java.util.Objects::nonNull)
                .forEach(UserCoupon::use);

        Order savedOrder = orderRepository.save(order);

        // 주문 성공 시 장바구니 비우기 (해당 상품들만)
        clearOrderedItemsFromCart(user.getId(), request.orderItems());

        return OrderResponse.from(savedOrder);
    }

    private List<OrderItem> createOrderItems(List<OrderItemRequest> itemRequests, Long userId) {
        return itemRequests.stream()
                .map(itemRequest -> {
                    Product product = productRepository.findById(itemRequest.menuId())
                            .orElseThrow(() -> new BusinessException(BusinessErrorCode.PRODUCT_NOT_FOUND));

                    UserCoupon userCoupon = null;
                    if (itemRequest.appliedCouponId() != null) {
                        userCoupon = userCouponRepository.findByIdAndUserId(itemRequest.appliedCouponId(), userId)
                                .orElseThrow(() -> new BusinessException(BusinessErrorCode.COUPON_NOT_FOUND));
                    }

                    product.reduceStock(itemRequest.quantity());
                    return OrderItem.createOrderItem(product, itemRequest.quantity(), itemRequest.selectedSize(), userCoupon);
                })
                .collect(Collectors.toList());
    }

    private void clearOrderedItemsFromCart(Long userId, List<OrderItemRequest> items) {
        for (OrderItemRequest item : items) {
            cartItemRepository.findByUserIdAndProductId(userId, item.menuId())
                    .ifPresent(cartItemRepository::delete);
        }
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderDetails(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.ORDER_NOT_FOUND));

        String email = SecurityUtil.getCurrentUserEmail();
        if (!order.getUser().getEmail().equals(email)) {
            throw new BusinessException(BusinessErrorCode.FORBIDDEN);
        }

        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public List<UserCouponResponse> getMyCoupons() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userCouponRepository.findByUserEmailAndStatus(email, UserCoupon.CouponStatus.ACTIVE).stream()
                .map(UserCouponResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public gdgoc.be.dto.order.CouponApplyResponse applyCoupon(Long couponId, gdgoc.be.dto.order.CouponApplyRequest request) {
        String email = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_FOUND));

        // 1. 임시로 주문 아이템들 생성 (DB 저장 X)
        List<OrderItem> orderItems = request.orderItems().stream()
                .map(itemRequest -> {
                    Product product = productRepository.findById(itemRequest.menuId())
                            .orElseThrow(() -> new BusinessException(BusinessErrorCode.PRODUCT_NOT_FOUND));
                    
                    UserCoupon userCoupon = null;
                    if (itemRequest.appliedCouponId() != null) {
                        userCoupon = userCouponRepository.findByIdAndUserId(itemRequest.appliedCouponId(), user.getId())
                                .orElseThrow(() -> new BusinessException(BusinessErrorCode.COUPON_NOT_FOUND));
                    }
                    
                    return OrderItem.createOrderItem(product, itemRequest.quantity(), itemRequest.selectedSize(), userCoupon);
                })
                .collect(Collectors.toList());

        // 2. 계산 모듈 사용
        CalculationResult calculation = orderCalculator.calculate(orderItems);

        return new gdgoc.be.dto.order.CouponApplyResponse(
                calculation.totalAmount(),
                calculation.discountAmount(),
                calculation.shippingFee(),
                calculation.finalAmount(),
                "복수 쿠폰 적용됨"
        );
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.ORDER_NOT_FOUND));

        String email = SecurityUtil.getCurrentUserEmail();
        if (!order.getUser().getEmail().equals(email)) {
            throw new BusinessException(BusinessErrorCode.FORBIDDEN);
        }

        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new BusinessException(BusinessErrorCode.FORBIDDEN);
        }

        for (OrderItem item : order.getOrderItems()) {
            Product product = item.getProduct();
            product.addStock(item.getQuantity());
        }
        order.cancel();
    }
    @Transactional
    public boolean confirmPayment(String paymentKey, Long orderId, Integer amount) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.ORDER_NOT_FOUND));


        if (order.getFinalAmount().intValue() != amount) {
            throw new BusinessException(BusinessErrorCode.BAD_REQUEST);
        }

        order.completePayment();
        return true;
    }

}
