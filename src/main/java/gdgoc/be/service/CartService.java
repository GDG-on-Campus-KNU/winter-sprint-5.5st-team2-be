package gdgoc.be.service;

import gdgoc.be.Repository.CartItemRepository;
import gdgoc.be.Repository.ProductRepository;
import gdgoc.be.Repository.UserRepository;
import gdgoc.be.common.util.SecurityUtil;
import gdgoc.be.domain.CartItem;
import gdgoc.be.domain.Product;
import gdgoc.be.domain.User;
import gdgoc.be.dto.cart.CartRequest;
import gdgoc.be.dto.cart.CartResponse;
import gdgoc.be.dto.cart.CartSummaryResponse;
import gdgoc.be.dto.cart.CartUpdateRequest;
import gdgoc.be.exception.BusinessErrorCode;
import gdgoc.be.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public void addProductToCart(CartRequest request) {
        String email = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_FOUND));

        Product product = findProductById(request.productId());

        if (!product.getSizesOptions().contains(request.selectedSize())) {
            throw new BusinessException(BusinessErrorCode.INVALID_CART);
        }

        CartItem cartItem = cartItemRepository.findByUserEmailAndProductIdAndSelectedSize(
                        email, request.productId(), request.selectedSize())
                .orElseGet(() -> CartItem.createEmptyCartItem(user,product,request.selectedSize()));

        cartItem.addQuantity(request.quantity());
        cartItem.validateStock(cartItem.getQuantity());

        cartItemRepository.save(cartItem);
    }

    @Transactional(readOnly = true)
    public CartSummaryResponse getMyCart() {
        String email = SecurityUtil.getCurrentUserEmail();
        List<CartResponse> items = cartItemRepository.findByUserEmail(email).stream()
                .map(CartResponse::from)
                .toList();

        int subTotal = items.stream().mapToInt(item -> item.price() * item.quantity())
                .sum();

        int shippingFee = (subTotal >= 30000 || subTotal == 0) ? 0 : 3000;
        int total = subTotal + shippingFee;

        return new CartSummaryResponse(items,subTotal,shippingFee,total);
    }

    public void deleteCartItem(Long cartId) {
        String email = SecurityUtil.getCurrentUserEmail();
        CartItem cartItem = cartItemRepository.findById(cartId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.CART_ITEM_NOT_FOUND));

        if (!cartItem.getUser().getEmail().equals(email)) {
            throw new BusinessException(BusinessErrorCode.UNAUTHORIZED_CART_ACCESS);
        }

        cartItemRepository.delete(cartItem);
    }

    public void updateCartItem(Long cartItemId, CartUpdateRequest request) {

        CartItem cartItem = findCartItemById(cartItemId);
        validateCartItemOwnership(cartItem);

        updateSizeIfRequested(cartItem, request.selectedSize());
        updateQuantityIfRequested(cartItem, request.quantity());
    }

    private CartItem findCartItemById(Long cartItemId) {
        return cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.CART_ITEM_NOT_FOUND));
    }

    private void validateCartItemOwnership(CartItem cartItem) {
        String currentEmail = SecurityUtil.getCurrentUserEmail();
        if (!cartItem.getUser().getEmail().equals(currentEmail)) {
            throw new BusinessException(BusinessErrorCode.UNAUTHORIZED_CART_ACCESS);
        }
    }

    private void updateSizeIfRequested(CartItem cartItem, String newSize) {
        if (newSize == null) return;

        if (!cartItem.getProduct().getSizesOptions().contains(newSize)) {
            throw new BusinessException(BusinessErrorCode.INVALID_CART);
        }
        cartItem.updateSelectedSize(newSize);
    }

    private void updateQuantityIfRequested(CartItem cartItem, Integer newQuantity) {
        if (newQuantity == null) return; // Guard Clause

        cartItem.validateStock(newQuantity);
        cartItem.updateQuantity(newQuantity);
    }


    private Product findProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.PRODUCT_NOT_FOUND));
    }



    public void deleteCartItems(List<Long> cartItemIds) {

        String email = SecurityUtil.getCurrentUserEmail();

        if (cartItemIds == null || cartItemIds.isEmpty()) {
            List<CartItem> userItems = cartItemRepository.findByUserEmail(email);
            cartItemRepository.deleteAllInBatch(userItems);
            return;
        }
        List<CartItem> targetItems = cartItemRepository.findAllById(cartItemIds);

        for (CartItem item : targetItems) {
            if (!item.getUser().getEmail().equals(email)) {
                throw new BusinessException(BusinessErrorCode.UNAUTHORIZED_CART_ACCESS);
            }
        }
        cartItemRepository.deleteAllInBatch(targetItems);
    }
}
