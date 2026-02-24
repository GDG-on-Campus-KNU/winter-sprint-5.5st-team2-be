package gdgoc.be.service;

import gdgoc.be.Repository.*;
import gdgoc.be.common.util.SecurityUtil;
import gdgoc.be.domain.*;
import gdgoc.be.dto.*;
import gdgoc.be.dto.order.OrderItemRequest;
import gdgoc.be.dto.order.OrderRequest;
import gdgoc.be.dto.order.OrderResponse;
import gdgoc.be.dto.user.UserCouponResponse;
import gdgoc.be.exception.BusinessErrorCode;
import gdgoc.be.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
    public List<OrderResponse> getMyOrders(Integer months) {
        String email = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_FOUND));

        List<Order> orders;
        if (months != null && months > 0) {
            LocalDateTime startDate = LocalDateTime.now().minusMonths(months);
            orders = orderRepository.findByUserIdAndOrderDateAfterOrderByOrderDateDesc(user.getId(), startDate);
        } else {
            orders = orderRepository.findByUserIdOrderByOrderDateDesc(user.getId());
        }
        return orders.stream().map(OrderResponse::from).toList();
    }

    public OrderResponse createOrder(OrderRequest request) {

        String email = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_FOUND));

        List<OrderItem> orderItems = createOrderItems(request.items());

        CalculationResult calculation = orderCalculator.calculate(
                orderItems,
                request.couponId(),
                user.getId()
        );

        Order order = Order.createOrder(
                user,
                calculation,
                request.couponId(),
                request.address()
        );
        orderItems.forEach(order::addOrderItem);

        Order savedOrder = orderRepository.save(order);

        // 주문 성공 시 장바구니 비우기 (해당 상품들만)
        clearOrderedItemsFromCart(user.getId(), request.items());

        return OrderResponse.from(savedOrder);
    }

    private List<OrderItem> createOrderItems(List<OrderItemRequest> itemRequests) {
        return itemRequests.stream()
                .map(itemRequest -> {
                    Product product = productRepository.findById(itemRequest.productId())
                            .orElseThrow(() -> new BusinessException(BusinessErrorCode.PRODUCT_NOT_FOUND));

                    product.reduceStock(itemRequest.quantity());
                    return OrderItem.createOrderItem(product, itemRequest.quantity(), itemRequest.selectedSize());
                })
                .collect(Collectors.toList());
    }

    private void clearOrderedItemsFromCart(Long userId, List<OrderItemRequest> items) {
        for (OrderItemRequest item : items) {
            cartItemRepository.findByUserIdAndProductId(userId, item.productId())
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
        return userCouponRepository.findByUserEmail(email).stream()
                .map(UserCouponResponse::from)
                .collect(Collectors.toList());
    }
}
