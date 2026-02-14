package gdgoc.be.service;


import gdgoc.be.Repository.CartItemRepository;
import gdgoc.be.Repository.MenuRepository;
import gdgoc.be.domain.CartItem;
import gdgoc.be.domain.Menu;
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

    public void addMenuToCart(Long userId, long menuId, int quantity) {

        Menu menu = findMenuById(menuId);

        CartItem cartItem = cartItemRepository.findByUserIdAndMenuId(userId,menuId)
                .orElseGet(() -> CartItem.createEmptyCartItem(userId, menu));

        cartItem.addQuantityWithStockCheck(quantity);
        cartItemRepository.save(cartItem);

    }

    public void updateCartItemQuantity(Long itemId, int quantity) {

        CartItem cartItem = findCartItemById(itemId);

        if(quantity <= 0 ) {
            cartItemRepository.delete(cartItem);
            return;
        }

        cartItem.updateQuantityWithStockCheck(quantity);
    }

    private Menu findMenuById(Long menuId) {
        return menuRepository.findById(menuId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.MENU_NOT_FOUND));
    }

    private CartItem findCartItemById(Long itemId) {
        return cartItemRepository.findById(itemId)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.INVALID_CART));
    }

    public void deleteSelectedItems(List<Long> itemIds) {
        cartItemRepository.deleteAllById(itemIds);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<CartResponse> getCartItems(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);

        return items.stream()
                .map(CartResponse::from)
                .collect(Collectors.toList());
    }
}