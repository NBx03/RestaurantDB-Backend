package restaurantdb.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import restaurantdb.model.OrderDishBill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDishBillRepository extends JpaRepository<OrderDishBill, Long> {
    @Query("SELECT o FROM OrderDishBill o WHERE o.order.id = :orderId")
    List<OrderDishBill> findDishesByOrderId(@Param("orderId") Long orderId);

    @Query("SELECT o FROM OrderDishBill o WHERE o.dish.id = :dishId")
    List<OrderDishBill> findOrdersByDishId(@Param("dishId") Long dishId);

    @Query("SELECT o FROM OrderDishBill o WHERE o.bill.id = :billId")
    List<OrderDishBill> findDishesByBillId(@Param("billId") Long billId);

    @Query("SELECT o FROM OrderDishBill o WHERE o.dish.id = :dishId")
    List<OrderDishBill> findBillsByDishId(@Param("dishId") Long dishId);

    @Query("SELECT o FROM OrderDishBill o WHERE o.order.client.id = :clientId")
    List<OrderDishBill> findOrdersAndDishesByClientId(@Param("clientId") Long clientId);

    Optional<OrderDishBill> findByOrderIdAndDishIdAndBillId(Long orderId, Long dishId, Long billId);
}
