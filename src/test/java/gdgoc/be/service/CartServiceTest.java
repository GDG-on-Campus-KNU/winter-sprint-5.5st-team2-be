package gdgoc.be.service;

import gdgoc.be.Repository.CartItemRepository;
import gdgoc.be.Repository.ProductRepository;
import gdgoc.be.Repository.UserRepository;
import gdgoc.be.domain.CartItem;
import gdgoc.be.domain.Product;
import gdgoc.be.domain.User;
import gdgoc.be.dto.cart.CartRequest;
import gdgoc.be.dto.cart.CartSummaryResponse;
import gdgoc.be.exception.BusinessErrorCode;
import gdgoc.be.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductRepository productRepository;

    private final String TEST_EMAIL = "user@example.com";

    @BeforeEach
    void setUp() {
        // SecurityContext에 테스트 유저 설정
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(TEST_EMAIL, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("장바구니 담기 실패 - 재고 부족")
    void addCart_fail_outOfStock() {
        // given
        CartRequest request = new CartRequest(1L, 100, "L");
        User user = User.builder().email(TEST_EMAIL).build();
        Product product = Product.builder()
                .stock(10)
                .sizesOptions(List.of("L"))
                .build();

        given(userRepository.findByEmail(TEST_EMAIL)).willReturn(Optional.of(user));
        given(productRepository.findById(1L)).willReturn(Optional.of(product));

        // when & then
        assertThatThrownBy(() -> cartService.addProductToCart(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("재고가 부족합니다.");
    }

    @Test
    @DisplayName("무료 배송 조건 확인 (30,000원 이상)")
    void getMyCart_freeShipping() {
        // given
        Product product = Product.builder().price(35000).build();
        User user = User.builder().email(TEST_EMAIL).build();
        CartItem item = CartItem.builder().user(user).product(product).quantity(1).build();

        given(cartItemRepository.findByUserEmail(TEST_EMAIL)).willReturn(List.of(item));

        // when
        CartSummaryResponse response = cartService.getMyCart();

        // then
        assertThat(response.subtotal()).isEqualTo(35000);
        assertThat(response.shippingFee()).isZero(); // 3만원 이상 무료 배송
        assertThat(response.total()).isEqualTo(35000);
    }

    @Test
    @DisplayName("장바구니 삭제 실패 - 소유권 없음")
    void deleteCartItem_fail_unauthorized() {
        // given
        User otherUser = User.builder().email("other@example.com").build();
        CartItem cartItem = CartItem.builder().user(otherUser).build();

        given(cartItemRepository.findById(1L)).willReturn(Optional.of(cartItem));

        // when & then
        assertThatThrownBy(() -> cartService.deleteCartItem(1L))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", BusinessErrorCode.UNAUTHORIZED_CART_ACCESS);
    }
}