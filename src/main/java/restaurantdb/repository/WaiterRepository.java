package restaurantdb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import restaurantdb.model.Waiter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WaiterRepository extends JpaRepository<Waiter, Long> {

    // Рекурсивный запрос для получения иерархии официантов и их подчиненных.
    @Query(value = "WITH RECURSIVE WaiterHierarchy AS ( " +
            "SELECT w.id, p.name, p.surname, w.manager_id " +
            "FROM waiter w " +
            "JOIN person p ON w.id = p.id " +
            "WHERE w.manager_id IS NULL " +
            "UNION ALL " +
            "SELECT w.id, p.name, p.surname, w.manager_id " +
            "FROM waiter w " +
            "JOIN person p ON w.id = p.id " +
            "JOIN WaiterHierarchy wh ON w.manager_id = wh.id) " +
            "SELECT * FROM WaiterHierarchy", nativeQuery = true)
    List<Object[]> findWaiterHierarchy();

    Page<Waiter> findAll(Pageable pageable);
}
