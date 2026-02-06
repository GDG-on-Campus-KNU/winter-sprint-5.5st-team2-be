package gdgoc.be.Repository;

import gdgoc.be.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    // 특정 유저의 장바구니에 특정 메뉴가 있는 지를 확인
    Optional<CartItem> findByUserIdAndMenuId(Long userId, Long menuId);

    // 2. 특정 유저의 장바구니 전체 목록 조회
    List<CartItem> findByUserId(Long userId);
}
