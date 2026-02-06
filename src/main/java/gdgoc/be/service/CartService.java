package gdgoc.be.service;


import gdgoc.be.Repository.CartItemRepository;
import gdgoc.be.Repository.MenuRepository;
import gdgoc.be.domain.CartItem;
import gdgoc.be.domain.Menu;
import gdgoc.be.dto.CartResponse;
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
    private final MenuRepository menuRepository;

    // 1. 장바구니 담기
    public void addMenuToCart(Long userId, long menuId, int quantity) {

        // 1. menuId 를 통해 menu 를 반환
        Menu menu = menuRepository.findById(menuId)
                .orElseThrow(() -> new RuntimeException("MENU_NOT_FOUND"));

        // [요구사항1] 재고 확인: 담으려는 수량(quantity) 가 menu 의 재고보다 많을 시
        // Out_Of_Stock Exception 발생
        if(menu.getStock() < quantity) {
            throw new RuntimeException("OUT_OF_STOCK");
        }

         /*
            [요구사항2] 중복 담기 체크
            : 같은 메뉴를 담으면 수량 증가, 없으면 새로 저장
         */
        cartItemRepository.findByUserIdAndMenuId(userId, menuId)
                .ifPresentOrElse(
                        cartItem -> {
                            // 이미 담긴 상품의 총 수량이 재고를 넘지 않는지 한 번 더 확인
                            if (menu.getStock() < cartItem.getQuantity() + quantity) {
                                throw new RuntimeException("OUT_OF_STOCK");
                            }
                            cartItem.addQuantity(quantity);
                        },
                        () -> {
                            CartItem newItem = CartItem.builder()
                                    .userId(userId)
                                    .menu(menu)
                                    .quantity(quantity)
                                    .build();
                            cartItemRepository.save(newItem);
                        }
                );
    }

    // 2. 장바구니 수량 변경 (PATCH /api/cart/{itemId})
    public void updateCartItemQuantity(Long itemId, int quantity) {
        CartItem cartItem = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("INVALID_CART"));

        /*
            [요구사항]
            수량 변경, 수량(quantity)이 0 이하일 시  삭제
         */
        if (quantity <= 0) {
            cartItemRepository.delete(cartItem);
        } else {
            //재고 확인 후 업데이트, 만약 변경하려는 양(quantity) 가 재고의 수량보다 많을 경우 OUT_OF_STOCK 예외 발생
            if (cartItem.getMenu().getStock() < quantity) {
                throw new RuntimeException("OUT_OF_STOCK");
            }
            // 정상적인 경우 quantity 업데이트
            cartItem.updateQuantity(quantity);
        }
    }

    // 3. 일괄 삭제 (DELETE /api/cart)
    public void deleteSelectedItems(List<Long> itemIds) {
        cartItemRepository.deleteAllById(itemIds);
    }

    // 4. 장바구니 목록 조회 (GET /api/cart)
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public List<CartResponse> getCartItems(Long userId) {
        List<CartItem> items = cartItemRepository.findByUserId(userId);

        return items.stream()
                .map(item -> CartResponse.builder()
                        .itemId(item.getId())
                        .menuName(item.getMenu().getName())
                        .price(item.getMenu().getPrice())
                        .quantity(item.getQuantity())
                        // 현재 메뉴의 재고가 0보다 큰지 확인하여 가용 상태 판단
                        .isAvailable(item.getMenu().getStock() > 0)
                        .build())
                .collect(Collectors.toList());
    }

}
