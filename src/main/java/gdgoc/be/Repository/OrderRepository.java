package gdgoc.be.Repository;

import gdgoc.be.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser_IdOrderByOrderDateDesc(Long userId);
    Page<Order> findByUserIdOrderByOrderDateDesc(Long userId, Pageable pageable);
    List<Order> findByUserIdAndOrderDateAfterOrderByOrderDateDesc(Long userId, LocalDateTime startDate);
}
