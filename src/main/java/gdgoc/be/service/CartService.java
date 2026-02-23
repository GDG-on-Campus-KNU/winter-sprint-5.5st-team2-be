package gdgoc.be.service;
import gdgoc.be.Repository.CartItemRepository;
import gdgoc.be.Repository.MenuRepository;
import gdgoc.be.Repository.UserRepository;
import gdgoc.be.common.util.SecurityUtil;
import gdgoc.be.domain.CartItem;
import gdgoc.be.domain.Menu;
import gdgoc.be.domain.User;
import gdgoc.be.dto.CartItemResponse;
import gdgoc.be.dto.CartResponse;
import gdgoc.be.exception.BusinessErrorCode;
import gdgoc.be.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final MenuRepository menuRepository;
    private final UserRepository userRepository;

    private static final int FREE_SHIPPING_THRESHOLD = 30000;
    private static final int DEFAULT_SHIPPING_FEE = 3000;

    public void addMenuToCart(long menuId, int quantity) {

        String email = SecurityUtil.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_FOUND));
        Menu menu = findMenuById(menuId);

        CartItem cartItem = cartItemRepository.findByUserEmailAndMenuId(email,menuId)
                        .orElseGet(() -> CartItem.createEmptyCartItem(user, menu));

        cartItem.addQuantityWithStockCheck(quantity);
        cartItem.updateItem(cartItem.getQuantity(), cartItem.getSelectedSize());
        cartItemRepository.save(cartItem);
    }

    public void updateCartItem(Long itemId, int quantity, String selectedSize) {

        CartItem cartItem = findCartItemById(itemId);
        validateOwnership(cartItem);

        if(quantity <= 0 ) {
            cartItemRepository.delete(cartItem);
            return;
        }

        cartItem.updateItem(quantity,selectedSize);
    }

    private Menu findMenuById(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MENU_NOT_FOUND));
    }

    private CartItem findCartItemById(Long itemId) {
        return cartItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.INVALID_CART));
    }

    public void deleteCartItem(Long itemId) {
        CartItem cartItem = findCartItemById(itemId);
        validateOwnership(cartItem);
        cartItemRepository.delete(cartItem);
    }

    public void deleteSelectedItems(List<Long> itemIds) {
        String email = SecurityUtil.getCurrentUserEmail();
        for(Long id : itemIds) {
            CartItem item = findCartItemById(id);
            validateOwnership(item);
        }
        cartItemRepository.deleteAllById(itemIds);
    }

    public void validateOwnership(CartItem item) {
        String email = SecurityUtil.getCurrentUserEmail();
        if (!item.getUser().getEmail().equals(email)) {
            throw new BusinessException(BusinessErrorCode.FORBIDDEN);
        }
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public CartResponse getCartItems() {

        String email = SecurityUtil.getCurrentUserEmail();
        List<CartItem> items = cartItemRepository.findByUserEmail(email);

        List<CartItemResponse> itemResponses = items.stream()
                .map(CartItemResponse::from)
                .collect(Collectors.toList());

        int subtotal = items.stream()
                .mapToInt(item -> item.getMenu().getPrice() * item.getQuantity())
                .sum();

        int shippingFee = (subtotal > 0 && subtotal < FREE_SHIPPING_THRESHOLD) ? DEFAULT_SHIPPING_FEE : 0;

        return CartResponse.of(itemResponses, subtotal, shippingFee);
    }
}