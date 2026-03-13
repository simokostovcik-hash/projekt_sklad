package cz.project_storage.repository;

import cz.project_storage.model.Order;
import cz.project_storage.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUser(User user);

    @Query("SELECT DISTINCT o FROM Order o " +
            "LEFT JOIN FETCH o.items i " +
            "LEFT JOIN FETCH i.coffee " +
            "WHERE o.user = :user " +
            "ORDER BY o.orderDate DESC")
    List<Order> findByUserWithItems(@Param("user") User user);
}