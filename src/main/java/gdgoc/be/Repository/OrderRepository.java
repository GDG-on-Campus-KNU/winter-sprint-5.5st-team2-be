package gdgoc.be.Repository;

import gdgoc.be.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser_IdOrderByOrderDateDesc(Long userId);
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);
    List<Order> findByUserIdAndOrderDateAfterOrderByOrderDateDesc(Long userId, LocalDateTime startDate);
}
