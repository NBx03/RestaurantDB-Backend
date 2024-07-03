package restaurantdb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import restaurantdb.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import restaurantdb.model.Recipe;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
    Page<Menu> findAll(Pageable pageable);
}
