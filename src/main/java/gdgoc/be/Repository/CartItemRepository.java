package gdgoc.be.Repository;

import gdgoc.be.domain.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByUserId(Long userId);

    List<CartItem> findByUserEmail(String email);

    Optional<CartItem> findByUserEmailAndProductId(String email, Long productId);

    Optional<CartItem> findByUserIdAndProductId(Long userId, Long productId);
}
