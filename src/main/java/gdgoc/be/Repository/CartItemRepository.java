package gdgoc.be.Repository;

import gdgoc.be.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserEmail(String email);

    Optional<CartItem> findByUserEmailAndMenuId(String email, Long menuId);

    Optional<CartItem> findByUserIdAndMenuId(Long userId, Long menuId);

    List<CartItem> findByUserId(Long userId);
}
