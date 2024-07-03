package restaurantdb.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import restaurantdb.model.Dish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import restaurantdb.model.Ingredient;

import java.util.List;

@Repository
public interface DishRepository extends JpaRepository<Dish, Long> {
    // Запрос с фильтрацией и сортировкой:
    // Найти все доступные блюда, отсортированные по цене.
    List<Dish> findByAvailabilityTrueOrderByPriceAsc();

    // Запрос upsert
    @Modifying
    @Transactional
    @Query(value = "INSERT INTO dish (id, name, description, price, availability, category) VALUES (:id, :name, :description, :price, :availability, :category) " +
            "ON CONFLICT (id) DO UPDATE SET name = :name, description = :description, price = :price, availability = :availability, category = :category", nativeQuery = true)
    void upsertDish(Long id, String name, String description, Double price, Boolean availability, String category);

    // Запрос CTE для получения всех блюд из меню определенного типа
    @Query(value = "WITH MenuDishCTE AS ( " +
            "SELECT md.dish_id " +
            "FROM menu_dish md " +
            "JOIN menu m ON md.menu_id = m.id " +
            "WHERE m.type = :type) " +
            "SELECT d.* " +
            "FROM dish d " +
            "JOIN MenuDishCTE cte ON d.id = cte.dish_id", nativeQuery = true)
    List<Dish> findDishesByMenuType(String type);

    Page<Dish> findAll(Pageable pageable);
}
