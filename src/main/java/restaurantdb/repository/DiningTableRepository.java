package restaurantdb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import restaurantdb.model.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import restaurantdb.model.Dish;

import java.util.Optional;

@Repository
public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {
    Page<DiningTable> findAll(Pageable pageable);

    Optional<DiningTable> findByNumber(Integer diningTableNumber);
}
