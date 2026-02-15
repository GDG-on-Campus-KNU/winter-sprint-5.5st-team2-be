package gdgoc.be.service;

import gdgoc.be.Repository.MenuRepository;
import gdgoc.be.Repository.OrderItemRepository;
import gdgoc.be.Repository.OrderRepository;
import gdgoc.be.Repository.UserRepository;
import gdgoc.be.Repository.CouponRepository;
import gdgoc.be.Repository.UserCouponRepository;
import gdgoc.be.domain.Menu;
import gdgoc.be.domain.Order;
import gdgoc.be.domain.OrderItem;
import gdgoc.be.domain.User;
import gdgoc.be.domain.Coupon;
import gdgoc.be.domain.UserCoupon;
import gdgoc.be.dto.CalculationResult;
import gdgoc.be.dto.OrderRequest;
import gdgoc.be.dto.OrderResponse;
import gdgoc.be.service.OrderCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    public OrderResponse createOrder(Long userId, OrderRequest request) {
        // 1. 사용자 및 쿠폰 ID 가져오기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        Long couponId = request.getCouponId();
        gdgoc.be.domain.Coupon coupon = null;
        gdgoc.be.domain.UserCoupon userCoupon = null;
        if (couponId != null) {
            userCoupon = userCouponRepository.findByIdAndUserId(couponId, userId)
                    .orElseThrow(() -> new RuntimeException("USER_COUPON_NOT_FOUND_OR_NOT_OWNED: " + couponId));

            if (userCoupon.isUsed()) {
                throw new RuntimeException("COUPON_ALREADY_USED: " + couponId);
            }

            coupon = userCoupon.getCoupon();
            if (coupon.getExpiryDate() != null && coupon.getExpiryDate().isBefore(LocalDateTime.now())) {
                throw new RuntimeException("COUPON_EXPIRED: " + couponId);
            }
        }

        // 2. 상품 유효성 검사 및 재고 확인
        List<OrderItem> orderItems = request.getOrderItems().stream().map(itemRequest -> {
            Menu menu = menuRepository.findById(itemRequest.getMenuId())
                    .orElseThrow(() -> new RuntimeException("MENU_NOT_FOUND: " + itemRequest.getMenuId()));

            if (menu.getStock() < itemRequest.getQuantity()) {
                throw new RuntimeException("OUT_OF_STOCK for menu: " + menu.getName());
            }

            // 가격 계산을 위해 임시로 OrderItem 생성, 나중에 저장됨
            return OrderItem.builder()
                    .menu(menu)
                    .quantity(itemRequest.getQuantity())
                    .orderPrice(BigDecimal.valueOf(menu.getPrice()).multiply(BigDecimal.valueOf(itemRequest.getQuantity())))
                    .build();
        }).collect(Collectors.toList());

        // 3. OrderCalculator를 사용하여 총 금액 계산
        CalculationResult calculationResult = OrderCalculator.calculateTotal(orderItems, coupon);

        // 4. 주문 생성 및 저장
        Order tempOrder = Order.builder()
                .user(user)
                .totalAmount(calculationResult.getTotalAmount())
                .discountAmount(calculationResult.getDiscountAmount())
                .deliveryFee(calculationResult.getShippingFee())
                .finalAmount(calculationResult.getFinalAmount())
                .couponId(couponId)
                .status(Order.OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                // OrderRequest에 주소가 누락됨, 나중에 추가되거나 기본값으로 가정
                .address("Default Address") // 임시 값
                .build();
        final Order savedOrder = orderRepository.save(tempOrder);

        // 5. 주문 상품 저장 및 재고 차감
        orderItems.forEach(orderItem -> {
            savedOrder.addOrderItem(orderItem); // 주문과 주문 상품 연관
            orderItemRepository.save(orderItem);

            // 재고 차감
            Menu menu = orderItem.getMenu();
            menu.setStock(menu.getStock() - orderItem.getQuantity());
            menuRepository.save(menu);
        });

        // 6. 쿠폰을 사용했으면 사용됨으로 표시
        if (userCoupon != null) {
            userCoupon.setUsed(true);
            userCouponRepository.save(userCoupon);
        }

        // 응답 매핑을 위해 주문에 업데이트된 orderItems 목록이 있는지 확인
        savedOrder.setOrderItems(orderItems);


        return OrderResponse.from(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(Long userId) {
        // 사용자가 주문에 연결되어 있지 않으면 findByUser_Id가 예외를 발생시키지만, 사용자 존재 유효성 검사
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        List<Order> orders = orderRepository.findByUser_Id(userId); // 도메인 모델에 따라 findByUser_Id 사용

        return orders.stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderDetails(Long userId, Long orderId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("ORDER_NOT_FOUND: " + orderId));

        // [권한 부여 - ]
        // if (!order.getUser().getId().equals(userId)) {
        //     throw new RuntimeException("UNAUTHORIZED_ACCESS: Order does not belong to user");
        // }

        return OrderResponse.from(order);
    }
}