package gdgoc.be.service;

import gdgoc.be.Repository.*;
import gdgoc.be.common.util.SecurityUtil;
import gdgoc.be.domain.*;
import gdgoc.be.dto.*;
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

    public OrderResponse createOrder(OrderRequest request) {

        String email = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_FOUND));

        UserCoupon userCoupon = findAndValidateCoupon(request.couponId());
        Coupon coupon = (userCoupon != null) ? userCoupon.getCoupon() : null;

        List<OrderItem> orderItems = createOrderItems(request.orderItems());

        CalculationResult result = OrderCalculator.calculateTotal(orderItems, coupon);

        String deliveryAddress = (request.address() != null) ? request.address() : user.getAddress();
        Order order = Order.createOrder(user, result, request.couponId(), deliveryAddress);

        orderItems.forEach(order::addOrderItem);
        if (userCoupon != null) {
            userCoupon.use();
        }

        return OrderResponse.from(orderRepository.save(order));

    }

    private UserCoupon findAndValidateCoupon(Long couponId) {
        if (couponId == null) return null;

        String email = SecurityUtil.getCurrentUserEmail();
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
    public List<OrderResponse> getOrdersByUser() {

        String email = SecurityUtil.getCurrentUserEmail();
        List<Order> orders = orderRepository.findByUserEmail(email);

        return orders.stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderDetails(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.ORDER_NOT_FOUND));

        String email = SecurityUtil.getCurrentUserEmail();
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

    public List<UserCouponResponse> getMyCoupons() {
        String email = SecurityUtil.getCurrentUserEmail();
        return userCouponRepository.findByUserEmail(email).stream()
                .map(UserCouponResponse::from)
                .collect(Collectors.toList());
    }
}