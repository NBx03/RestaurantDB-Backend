package restaurantdb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import restaurantdb.model.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import restaurantdb.model.DiningTable;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    // Запрос с использованием оконной функции для получения суммы заказов в каждом чеке.
    @Query(value = "SELECT b.id, b.total_amount, b.issued_at, b.client_id, SUM(odb.dish_quantity * d.price) OVER (PARTITION BY b.id) AS total_order_amount " +
            "FROM bill b " +
            "JOIN order_dish_bill odb ON b.id = odb.bill_id " +
            "JOIN dish d ON odb.dish_id = d.id", nativeQuery = true)
    List<Object[]> findBillsWithTotalOrderAmount();

    Page<Bill> findAll(Pageable pageable);

    List<Bill> findByClientId(Long clientId);
}
