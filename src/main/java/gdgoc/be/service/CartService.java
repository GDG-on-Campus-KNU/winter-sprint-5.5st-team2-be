package gdgoc.be.service;


import gdgoc.be.Repository.CartItemRepository;
import gdgoc.be.Repository.MenuRepository;
import gdgoc.be.Repository.UserRepository;
import gdgoc.be.common.util.SecurityUtil;
import gdgoc.be.domain.CartItem;
import gdgoc.be.domain.Menu;
import gdgoc.be.domain.User;
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

    public void addMenuToCart(long menuId, int quantity) {

        String email = SecurityUtil.getCurrentUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException(BusinessErrorCode.USER_NOT_FOUND));
        Menu menu = findMenuById(menuId);

        CartItem cartItem = cartItemRepository.findByUserEmailAndMenuId(email,menuId)
                        .orElseGet(() -> CartItem.createEmptyCartItem(user, menu));

        cartItem.addQuantityWithStockCheck(quantity);
        cartItemRepository.save(cartItem);
    }

    public void updateCartItemQuantity(Long itemId, int quantity) {

        CartItem cartItem = findCartItemById(itemId);

        String email = SecurityUtil.getCurrentUserEmail();
        if (!cartItem.getUser().getEmail().equals(email)) {
            throw new BusinessException(BusinessErrorCode.FORBIDDEN);
        }

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

        String email = SecurityUtil.getCurrentUserEmail();
        for(Long id : itemIds) {
            CartItem item = findCartItemById(id);
            if(!item.getUser().getEmail().equals((email))) {
                throw new BusinessException(BusinessErrorCode.FORBIDDEN);
            }
        }
        cartItemRepository.deleteAllById(itemIds);
    }

    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public List<CartResponse> getCartItems() {
        String email = SecurityUtil.getCurrentUserEmail();
        List<CartItem> items = cartItemRepository.findByUserEmail(email);

        return items.stream()
                .map(CartResponse::from)
                .collect(Collectors.toList());
    }
}