package gdgoc.be.service;

import gdgoc.be.Repository.CartItemRepository;
import gdgoc.be.Repository.ProductRepository;
import gdgoc.be.Repository.UserRepository;
import gdgoc.be.common.util.SecurityUtil;
import gdgoc.be.domain.CartItem;
import gdgoc.be.domain.Product;
import gdgoc.be.domain.User;
import gdgoc.be.dto.CartDeleteRequest;
import gdgoc.be.dto.CartRequest;
import gdgoc.be.dto.CartResponse;
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
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public void addProductToCart(long productId, int quantity) {
        String email = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_FOUND));

        Product product = findProductById(productId);

        CartItem cartItem = cartItemRepository.findByUserEmailAndProductId(email, productId)
                        .orElseGet(() -> CartItem.createEmptyCartItem(user, product));

        cartItem.addQuantity(quantity);
        cartItem.validateStock(cartItem.getQuantity());

        cartItemRepository.save(cartItem);
    }

    @Transactional(readOnly = true)
    public List<CartResponse> getMyCart() {
        String email = SecurityUtil.getCurrentUserEmail();
        return cartItemRepository.findByUserEmail(email).stream()
                .map(CartResponse::from)
                .collect(Collectors.toList());
    }

    public void deleteCartItem(CartDeleteRequest request) {
        String email = SecurityUtil.getCurrentUserEmail();
        CartItem cartItem = cartItemRepository.findById(request.cartId())
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.CART_ITEM_NOT_FOUND));

        if (!cartItem.getUser().getEmail().equals(email)) {
            throw new BusinessException(BusinessErrorCode.UNAUTHORIZED_CART_ACCESS);
        }

        cartItemRepository.delete(cartItem);
    }

    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.PRODUCT_NOT_FOUND));
    }
}
