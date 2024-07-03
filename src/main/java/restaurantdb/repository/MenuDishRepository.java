package restaurantdb.repository;

import restaurantdb.model.Dish;
import restaurantdb.model.Menu;
import restaurantdb.model.MenuDish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MenuDishRepository extends JpaRepository<MenuDish, Long> {
    void deleteByMenuAndDish(Menu menu, Dish dish);
}
