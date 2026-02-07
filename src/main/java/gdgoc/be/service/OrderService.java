package gdgoc.be.service;

import gdgoc.be.Repository.MenuRepository;
import gdgoc.be.Repository.OrderItemRepository;
import gdgoc.be.Repository.OrderRepository;
import gdgoc.be.Repository.UserRepository; // Assuming UserRepository exists or needs to be created
import gdgoc.be.domain.Menu;
import gdgoc.be.domain.Order;
import gdgoc.be.domain.OrderItem;
import gdgoc.be.domain.User; // Assuming User domain exists
import gdgoc.be.dto.OrderRequest;
import gdgoc.be.dto.OrderResponse;
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
    private final UserRepository userRepository; // Need to verify if this repository exists

    public OrderResponse createOrder(Long userId, OrderRequest request) {
        // 1. Fetch User (Assuming UserRepository exists and User entity is used)
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        // 2. Validate items and check stock
        List<OrderItem> orderItems = request.getOrderItems().stream().map(itemRequest -> {
            Menu menu = menuRepository.findById(itemRequest.getMenuId())
                    .orElseThrow(() -> new RuntimeException("MENU_NOT_FOUND: " + itemRequest.getMenuId()));

            if (menu.getStock() < itemRequest.getQuantity()) {
                throw new RuntimeException("OUT_OF_STOCK for menu: " + menu.getName());
            }

            // Temporarily create OrderItem to calculate price, will be saved later
            return OrderItem.builder()
                    .menu(menu)
                    .quantity(itemRequest.getQuantity())
                    .orderPrice(BigDecimal.valueOf(menu.getPrice()).multiply(BigDecimal.valueOf(itemRequest.getQuantity())))
                    .build();
        }).collect(Collectors.toList());

        // 3. Calculate total amounts
        BigDecimal totalAmount = orderItems.stream()
                .map(OrderItem::getOrderPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Placeholder for discount and delivery fee calculation
        BigDecimal discountAmount = BigDecimal.ZERO;
        BigDecimal deliveryFee = BigDecimal.ZERO;
        BigDecimal finalAmount = totalAmount.subtract(discountAmount).add(deliveryFee);

        // 4. Create and save Order
        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .discountAmount(discountAmount)
                .deliveryFee(deliveryFee)
                .finalAmount(finalAmount)
                .status(Order.OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                // Address is missing in OrderRequest, assuming it will be added later or default
                .address("Default Address") // Placeholder
                .build();
        orderRepository.save(order);

        // 5. Save OrderItems and deduct stock
        orderItems.forEach(orderItem -> {
            order.addOrderItem(orderItem); // Associate order with orderItem
            orderItemRepository.save(orderItem);

            // Deduct stock
            Menu menu = orderItem.getMenu();
            menu.setStock(menu.getStock() - orderItem.getQuantity());
            menuRepository.save(menu);
        });

        // Ensure order has updated orderItems list for response mapping
        order.setOrderItems(orderItems);


        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUser(Long userId) {
        // User validation for existence, though findByUser_Id will likely throw if user doesn't exist linked to orders
        userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("USER_NOT_FOUND"));

        List<Order> orders = orderRepository.findByUser_Id(userId); // Use findByUser_Id as per domain model

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

        // [Authorization - Skipped for now as per user instruction]
        // if (!order.getUser().getId().equals(userId)) {
        //     throw new RuntimeException("UNAUTHORIZED_ACCESS: Order does not belong to user");
        // }

        return OrderResponse.from(order);
    }
}
