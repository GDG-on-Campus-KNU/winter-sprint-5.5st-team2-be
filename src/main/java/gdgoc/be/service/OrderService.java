package gdgoc.be.service;

import gdgoc.be.Repository.*;
import gdgoc.be.common.util.SecurityUtil;
import gdgoc.be.domain.*;
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
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CouponRepository couponRepository;
    private final OrderCalculator orderCalculator;
    private final CartItemRepository cartItemRepository;

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
                orderItems,
                calculation.totalAmount(),
                calculation.discountAmount(),
                calculation.finalAmount(),
                calculation.coupon()
        );

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
                    return OrderItem.createOrderItem(product, itemRequest.quantity());
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
    public List<OrderResponse> getMyOrders() {
        String email = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_FOUND));

        return orderRepository.findByUserIdOrderByOrderDateDesc(user.getId()).stream()
                .map(OrderResponse::from)
                .collect(Collectors.toList());
    }
}
