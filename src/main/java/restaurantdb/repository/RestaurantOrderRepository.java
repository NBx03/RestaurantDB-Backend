package restaurantdb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import restaurantdb.model.RestaurantOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import restaurantdb.model.Supplier;

import java.util.List;

@Repository
public interface RestaurantOrderRepository extends JpaRepository<RestaurantOrder, Long> {
    // Запрос с GROUP BY:
    // Cгруппировать заказы по статусу и посчитать количество заказов в каждом статусе.
    @Query("SELECT r.status, COUNT(r) FROM RestaurantOrder r GROUP BY r.status")
    List<Object[]> countOrdersByStatus();

    Page<RestaurantOrder> findAll(Pageable pageable);
}
