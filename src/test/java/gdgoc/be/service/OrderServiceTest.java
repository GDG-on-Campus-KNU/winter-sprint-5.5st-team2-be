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
import gdgoc.be.domain.Store;
import gdgoc.be.domain.User;
import gdgoc.be.domain.Coupon;
import gdgoc.be.domain.UserCoupon;
import gdgoc.be.dto.OrderRequest;
import gdgoc.be.dto.OrderResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private MenuRepository menuRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CouponRepository couponRepository;
    @Mock
    private UserCouponRepository userCouponRepository;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Store testStore;
    private Menu testMenu1;
    private Menu testMenu2;

    @BeforeEach
    void setUp() {
        testUser = new User("testUser", "test@example.com");
        testUser.setId(1L);
        testUser.setCreatedAt(LocalDateTime.now());
        testStore = new Store(1L, "Test Store", "Test Address", "010-1234-5678");
        testMenu1 = new Menu(1L, testStore, "Menu 1", "Description 1", 10000, 10, true, null); // Category is null, but not relevant for these tests
        testMenu2 = new Menu(2L, testStore, "Menu 2", "Description 2", 15000, 5, true, null);
    }

    @Test
    @DisplayName("성공적인 주문 생성")
    void createOrder_Success() {
        // Given
        OrderRequest.OrderItemRequest itemRequest1 = new OrderRequest.OrderItemRequest();
        itemRequest1.setMenuId(testMenu1.getId());
        itemRequest1.setQuantity(2);

        OrderRequest.OrderItemRequest itemRequest2 = new OrderRequest.OrderItemRequest();
        itemRequest2.setMenuId(testMenu2.getId());
        itemRequest2.setQuantity(1);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(List.of(itemRequest1, itemRequest2));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(menuRepository.findById(testMenu1.getId())).thenReturn(Optional.of(testMenu1));
        when(menuRepository.findById(testMenu2.getId())).thenReturn(Optional.of(testMenu2));

        Order savedOrder = Order.builder()
                .id(1L)
                .user(testUser)
                .totalAmount(BigDecimal.valueOf(35000))
                .discountAmount(BigDecimal.ZERO)
                .deliveryFee(BigDecimal.ZERO)
                .finalAmount(BigDecimal.valueOf(35000))
                .status(Order.OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .address("Default Address")
                .orderItems(new java.util.ArrayList<>()) // Initialize the list
                .build();
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));


        // When
        OrderResponse response = orderService.createOrder(testUser.getId(), orderRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(savedOrder.getId());
        assertThat(response.getTotalPrice()).isEqualTo(BigDecimal.valueOf(35000));
        assertThat(response.getOrderItems()).hasSize(2);
        assertThat(testMenu1.getStock()).isEqualTo(8); // 10 - 2
        assertThat(testMenu2.getStock()).isEqualTo(4); // 5 - 1

        verify(userRepository, times(1)).findById(testUser.getId());
        verify(menuRepository, times(2)).findById(anyLong());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(2)).save(any(OrderItem.class));
        verify(menuRepository, times(2)).save(any(Menu.class)); // Stock update
    }

    @Test
    @DisplayName("주문 생성 시 사용자를 찾을 수 없음")
    void createOrder_UserNotFound() {
        // Given
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(Collections.emptyList()); // Not relevant for this test, but needed to instantiate

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.createOrder(999L, orderRequest));
        assertThat(exception.getMessage()).isEqualTo("USER_NOT_FOUND");

        verify(userRepository, times(1)).findById(anyLong());
        verifyNoInteractions(menuRepository, orderRepository, orderItemRepository);
    }

    @Test
    @DisplayName("주문 생성 시 메뉴를 찾을 수 없음")
    void createOrder_MenuNotFound() {
        // Given
        OrderRequest.OrderItemRequest itemRequest = new OrderRequest.OrderItemRequest();
        itemRequest.setMenuId(999L); // Non-existent menu
        itemRequest.setQuantity(1);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(List.of(itemRequest));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(menuRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.createOrder(testUser.getId(), orderRequest));
        assertThat(exception.getMessage()).isEqualTo("MENU_NOT_FOUND: " + itemRequest.getMenuId());

        verify(userRepository, times(1)).findById(testUser.getId());
        verify(menuRepository, times(1)).findById(itemRequest.getMenuId());
        verifyNoInteractions(orderRepository, orderItemRepository);
    }

    @Test
    @DisplayName("주문 생성 시 재고 부족")
    void createOrder_InsufficientStock() {
        // Given
        OrderRequest.OrderItemRequest itemRequest = new OrderRequest.OrderItemRequest();
        itemRequest.setMenuId(testMenu1.getId());
        itemRequest.setQuantity(11); // More than available stock (10)

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(List.of(itemRequest));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(menuRepository.findById(testMenu1.getId())).thenReturn(Optional.of(testMenu1));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.createOrder(testUser.getId(), orderRequest));
        assertThat(exception.getMessage()).isEqualTo("OUT_OF_STOCK for menu: " + testMenu1.getName());

        verify(userRepository, times(1)).findById(testUser.getId());
        verify(menuRepository, times(1)).findById(testMenu1.getId());
        verifyNoInteractions(orderRepository, orderItemRepository);
    }

    @Test
    @DisplayName("사용자별 주문 목록 성공적으로 조회")
    void getOrdersByUser_Success() {
        // Given
        Order order1 = Order.builder()
                .id(10L)
                .user(testUser)
                .totalAmount(BigDecimal.valueOf(10000))
                .finalAmount(BigDecimal.valueOf(10000))
                .status(Order.OrderStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .address("Default Address")
                .orderItems(List.of(
                        OrderItem.builder().id(100L).menu(testMenu1).quantity(1).orderPrice(BigDecimal.valueOf(10000)).build()
                ))
                .build();
        Order order2 = Order.builder()
                .id(11L)
                .user(testUser)
                .totalAmount(BigDecimal.valueOf(20000))
                .finalAmount(BigDecimal.valueOf(20000))
                .status(Order.OrderStatus.PENDING)
                .createdAt(LocalDateTime.now().minusHours(1))
                .address("Another Address")
                .orderItems(List.of(
                        OrderItem.builder().id(101L).menu(testMenu2).quantity(1).orderPrice(BigDecimal.valueOf(15000)).build(),
                        OrderItem.builder().id(102L).menu(testMenu1).quantity(0).orderPrice(BigDecimal.valueOf(5000)).build()
                ))
                .build();


        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(orderRepository.findByUser_Id(testUser.getId())).thenReturn(List.of(order1, order2));

        // When
        List<OrderResponse> responses = orderService.getOrdersByUser(testUser.getId());

        // Then
        assertThat(responses).isNotNull();
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getOrderId()).isEqualTo(order1.getId());
        assertThat(responses.get(1).getOrderId()).isEqualTo(order2.getId());

        verify(userRepository, times(1)).findById(testUser.getId());
        verify(orderRepository, times(1)).findByUser_Id(testUser.getId());
    }

    @Test
    @DisplayName("사용자별 주문 목록 조회 시 사용자를 찾을 수 없음")
    void getOrdersByUser_UserNotFound() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.getOrdersByUser(999L));
        assertThat(exception.getMessage()).isEqualTo("USER_NOT_FOUND");

        verify(userRepository, times(1)).findById(anyLong());
        verifyNoInteractions(orderRepository, orderItemRepository, menuRepository);
    }

    @Test
    @DisplayName("사용자에게 주문이 없음")
    void getOrdersByUser_NoOrders() {
        // Given
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(orderRepository.findByUser_Id(testUser.getId())).thenReturn(Collections.emptyList());

        // When
        List<OrderResponse> responses = orderService.getOrdersByUser(testUser.getId());

        // Then
        assertThat(responses).isNotNull();
        assertThat(responses).isEmpty();

        verify(userRepository, times(1)).findById(testUser.getId());
        verify(orderRepository, times(1)).findByUser_Id(testUser.getId());
    }

    @Test
    @DisplayName("주문 상세 정보 성공적으로 조회")
    void getOrderDetails_Success() {
        // Given
        Order order = Order.builder()
                .id(1L)
                .user(testUser)
                .totalAmount(BigDecimal.valueOf(10000))
                .finalAmount(BigDecimal.valueOf(10000))
                .status(Order.OrderStatus.COMPLETED)
                .createdAt(LocalDateTime.now())
                .address("Default Address")
                .build();
        // Add order items to ensure response mapping works
        OrderItem orderItem = OrderItem.builder().id(100L).order(order).menu(testMenu1).quantity(1).orderPrice(BigDecimal.valueOf(10000)).build();
        order.setOrderItems(List.of(orderItem));


        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));

        // When
        OrderResponse response = orderService.getOrderDetails(testUser.getId(), order.getId());

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(order.getId());
        assertThat(response.getTotalPrice()).isEqualTo(order.getTotalAmount());
        assertThat(response.getOrderItems()).hasSize(1);
        assertThat(response.getOrderItems().get(0).getMenuName()).isEqualTo(testMenu1.getName());


        verify(userRepository, times(1)).findById(testUser.getId());
        verify(orderRepository, times(1)).findById(order.getId());
    }

    @Test
    @DisplayName("주문 상세 조회 시 사용자를 찾을 수 없음")
    void getOrderDetails_UserNotFound() {
        // Given
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.getOrderDetails(999L, 1L));
        assertThat(exception.getMessage()).isEqualTo("USER_NOT_FOUND");

        verify(userRepository, times(1)).findById(anyLong());
        verifyNoInteractions(orderRepository, orderItemRepository, menuRepository);
    }

    @Test
    @DisplayName("주문 상세 조회 시 주문을 찾을 수 없음")
    void getOrderDetails_OrderNotFound() {
        // Given
        when(userRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
        when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.getOrderDetails(testUser.getId(), 999L));
        assertThat(exception.getMessage()).isEqualTo("ORDER_NOT_FOUND: 999");

        verify(userRepository, times(1)).findById(testUser.getId());
        verify(orderRepository, times(1)).findById(anyLong());
        verifyNoInteractions(orderItemRepository, menuRepository);
    }

    @Test
    @DisplayName("고정 금액 쿠폰으로 주문 생성 성공")
    void createOrder_WithFixedCoupon_Success() {
        // Given
        OrderRequest.OrderItemRequest itemRequest1 = new OrderRequest.OrderItemRequest();
        itemRequest1.setMenuId(testMenu1.getId());
        itemRequest1.setQuantity(2); // 20000원

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(List.of(itemRequest1));
        orderRequest.setCouponId(100L); // 가정된 쿠폰 ID

        Coupon fixedCoupon = new Coupon(1L, "고정 할인 쿠폰", Coupon.DiscountType.FIXED, BigDecimal.valueOf(5000), BigDecimal.valueOf(10000), LocalDateTime.now().plusDays(7));
        UserCoupon userCoupon = new UserCoupon(100L, testUser, fixedCoupon, false);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(menuRepository.findById(testMenu1.getId())).thenReturn(Optional.of(testMenu1));
        when(userCouponRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(userCoupon));

        Order savedOrder = Order.builder()
                .id(1L)
                .user(testUser)
                .totalAmount(BigDecimal.valueOf(20000))
                .discountAmount(BigDecimal.valueOf(5000))
                .deliveryFee(BigDecimal.valueOf(3000)) // 20000-5000 = 15000 < 30000 이므로 배송비 3000원
                .finalAmount(BigDecimal.valueOf(18000))
                .couponId(orderRequest.getCouponId())
                .status(Order.OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .address("Default Address")
                .orderItems(new java.util.ArrayList<>())
                .build();
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userCouponRepository.save(any(UserCoupon.class))).thenAnswer(invocation -> invocation.getArgument(0));


        // When
        OrderResponse response = orderService.createOrder(testUser.getId(), orderRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(savedOrder.getId());
        assertThat(response.getTotalPrice()).isEqualTo(BigDecimal.valueOf(20000));
        assertThat(response.getTotalDiscount()).isEqualTo(BigDecimal.valueOf(5000));
        assertThat(response.getShippingFee()).isEqualTo(BigDecimal.valueOf(3000));
        assertThat(response.getFinalPrice()).isEqualTo(BigDecimal.valueOf(18000));
        assertThat(response.getCouponId()).isEqualTo(orderRequest.getCouponId());
        assertThat(testMenu1.getStock()).isEqualTo(8); // 10 - 2
        assertThat(userCoupon.isUsed()).isTrue();

        verify(userRepository, times(1)).findById(testUser.getId());
        verify(menuRepository, times(1)).findById(anyLong());
        verify(userCouponRepository, times(1)).findByIdAndUserId(orderRequest.getCouponId(), testUser.getId());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
        verify(menuRepository, times(1)).save(any(Menu.class));
        verify(userCouponRepository, times(1)).save(any(UserCoupon.class));
    }

    @Test
    @DisplayName("퍼센트 쿠폰으로 주문 생성 성공")
    void createOrder_WithPercentCoupon_Success() {
        // Given
        OrderRequest.OrderItemRequest itemRequest1 = new OrderRequest.OrderItemRequest();
        itemRequest1.setMenuId(testMenu1.getId());
        itemRequest1.setQuantity(3); // 30000원

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(List.of(itemRequest1));
        orderRequest.setCouponId(101L); // 가정된 쿠폰 ID

        Coupon percentCoupon = new Coupon(2L, "퍼센트 할인 쿠폰", Coupon.DiscountType.PERCENT, BigDecimal.valueOf(20), BigDecimal.valueOf(20000), LocalDateTime.now().plusDays(7));
        UserCoupon userCoupon = new UserCoupon(101L, testUser, percentCoupon, false);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(menuRepository.findById(testMenu1.getId())).thenReturn(Optional.of(testMenu1));
        when(userCouponRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(userCoupon));

        Order savedOrder = Order.builder()
                .id(1L)
                .user(testUser)
                .totalAmount(BigDecimal.valueOf(30000))
                .discountAmount(BigDecimal.valueOf(6000)) // 30000 * 20% = 6000
                .deliveryFee(BigDecimal.ZERO) // 30000-6000 = 24000 < 30000 이지만 OrderCalculator에서 무료배송조건이 totalAmount 기준인듯
                .finalAmount(BigDecimal.valueOf(24000)) // 30000 - 6000 = 24000
                .couponId(orderRequest.getCouponId())
                .status(Order.OrderStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .address("Default Address")
                .orderItems(new java.util.ArrayList<>())
                .build();
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userCouponRepository.save(any(UserCoupon.class))).thenAnswer(invocation -> invocation.getArgument(0));


        // When
        OrderResponse response = orderService.createOrder(testUser.getId(), orderRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(savedOrder.getId());
        assertThat(response.getTotalPrice()).isEqualTo(BigDecimal.valueOf(30000));
        assertThat(response.getTotalDiscount()).isEqualTo(BigDecimal.valueOf(6000));
        assertThat(response.getShippingFee()).isEqualTo(BigDecimal.ZERO); // 총액이 3만원 이상이므로 무료배송
        assertThat(response.getFinalPrice()).isEqualTo(BigDecimal.valueOf(24000));
        assertThat(response.getCouponId()).isEqualTo(orderRequest.getCouponId());
        assertThat(testMenu1.getStock()).isEqualTo(7); // 10 - 3
        assertThat(userCoupon.isUsed()).isTrue();

        verify(userRepository, times(1)).findById(testUser.getId());
        verify(menuRepository, times(1)).findById(anyLong());
        verify(userCouponRepository, times(1)).findByIdAndUserId(orderRequest.getCouponId(), testUser.getId());
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderItemRepository, times(1)).save(any(OrderItem.class));
        verify(menuRepository, times(1)).save(any(Menu.class));
        verify(userCouponRepository, times(1)).save(any(UserCoupon.class));
    }

    @Test
    @DisplayName("주문 생성 시 쿠폰을 찾을 수 없음")
    void createOrder_CouponNotFound() {
        // Given
        OrderRequest.OrderItemRequest itemRequest = new OrderRequest.OrderItemRequest();
        itemRequest.setMenuId(testMenu1.getId());
        itemRequest.setQuantity(1);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(List.of(itemRequest));
        orderRequest.setCouponId(999L); // 존재하지 않는 쿠폰 ID

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(userCouponRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());


        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.createOrder(testUser.getId(), orderRequest));
        assertThat(exception.getMessage()).isEqualTo("USER_COUPON_NOT_FOUND_OR_NOT_OWNED: 999");

        verify(userRepository, times(1)).findById(testUser.getId());
        verify(userCouponRepository, times(1)).findByIdAndUserId(orderRequest.getCouponId(), testUser.getId());
        verifyNoInteractions(orderRepository, orderItemRepository);
    }

    @Test
    @DisplayName("주문 생성 시 사용자 쿠폰이 이미 사용됨")
    void createOrder_UserCouponAlreadyUsed() {
        // Given
        OrderRequest.OrderItemRequest itemRequest = new OrderRequest.OrderItemRequest();
        itemRequest.setMenuId(testMenu1.getId());
        itemRequest.setQuantity(1);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(List.of(itemRequest));
        orderRequest.setCouponId(100L);

        Coupon fixedCoupon = new Coupon(1L, "고정 할인 쿠폰", Coupon.DiscountType.FIXED, BigDecimal.valueOf(5000), BigDecimal.valueOf(10000), LocalDateTime.now().plusDays(7));
        UserCoupon userCoupon = new UserCoupon(100L, testUser, fixedCoupon, true); // 이미 사용됨

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(userCouponRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(userCoupon));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.createOrder(testUser.getId(), orderRequest));
        assertThat(exception.getMessage()).isEqualTo("COUPON_ALREADY_USED: 100");

        verify(userRepository, times(1)).findById(testUser.getId());
        verify(userCouponRepository, times(1)).findByIdAndUserId(orderRequest.getCouponId(), testUser.getId());
        verifyNoInteractions(orderRepository, orderItemRepository);
    }

    @Test
    @DisplayName("주문 생성 시 쿠폰 만료됨")
    void createOrder_CouponExpired() {
        // Given
        OrderRequest.OrderItemRequest itemRequest = new OrderRequest.OrderItemRequest();
        itemRequest.setMenuId(testMenu1.getId());
        itemRequest.setQuantity(1);

        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setOrderItems(List.of(itemRequest));
        orderRequest.setCouponId(100L);

        Coupon fixedCoupon = new Coupon(1L, "고정 할인 쿠폰", Coupon.DiscountType.FIXED, BigDecimal.valueOf(5000), BigDecimal.valueOf(10000), LocalDateTime.now().minusDays(1)); // 만료됨
        UserCoupon userCoupon = new UserCoupon(100L, testUser, fixedCoupon, false);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(testUser));
        when(userCouponRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.of(userCoupon));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> orderService.createOrder(testUser.getId(), orderRequest));
        assertThat(exception.getMessage()).isEqualTo("COUPON_EXPIRED: 100");

        verify(userRepository, times(1)).findById(testUser.getId());
        verify(userCouponRepository, times(1)).findByIdAndUserId(orderRequest.getCouponId(), testUser.getId());
        verifyNoInteractions(orderRepository, orderItemRepository);
    }
}

