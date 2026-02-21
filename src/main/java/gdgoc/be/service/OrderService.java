package gdgoc.be.service;

import gdgoc.be.Repository.*;
import gdgoc.be.domain.*;
import gdgoc.be.dto.CalculationResult;
import gdgoc.be.dto.OrderItemRequest;
import gdgoc.be.dto.OrderRequest;
import gdgoc.be.dto.OrderResponse;
import gdgoc.be.exception.BusinessErrorCode;
import gdgoc.be.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final UserCouponRepository userCouponRepository;

    public OrderResponse createOrder(String email, OrderRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_FOUND));

        UserCoupon userCoupon = findAndValidateCoupon(email, request.couponId());
        Coupon coupon = (userCoupon != null) ? userCoupon.getCoupon() : null;

        List<OrderItem> orderItems = createOrderItems(request.orderItems());

        CalculationResult result = OrderCalculator.calculateTotal(orderItems, coupon);

        Order order = Order.createOrder(user, result, request.couponId(), "Default Address");

        orderItems.forEach(order::addOrderItem);
        if (userCoupon != null) {
            userCoupon.use();
        }

        return OrderResponse.from(orderRepository.save(order));

    }

    private UserCoupon findAndValidateCoupon(String email, Long couponId) {
        if (couponId == null) return null;

        UserCoupon userCoupon = userCouponRepository.findByIdAndUserEmail(couponId,email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.COUPON_NOT_FOUND));

        userCoupon.validate();

        return userCoupon;
    }

    private List<OrderItem> createOrderItems(List<OrderItemRequest> itemRequests) {

        return itemRequests.stream().map(itemRequest -> {
            Menu menu = menuRepository.findById(itemRequest.menuId())
                    .orElseThrow(() -> new RuntimeException("MENU_NOT_FOUND"));

            menu.reduceStock(itemRequest.quantity());
            return OrderItem.createOrderItem(menu, itemRequest.quantity());
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(String email) {

        List<Order> orders = orderRepository.findByUserEmail(email);

        return orders.stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderDetails(String email, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.ORDER_NOT_FOUND));

        if (!order.getUser().getEmail().equals(email)) {
            // 권한이 없는 주문에 접근할 경우 에러 처리
            throw new BusinessException(BusinessErrorCode.FORBIDDEN);
        }
        return OrderResponse.from(order);
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(BusinessErrorCode.USER_NOT_FOUND);
        }
    }
}