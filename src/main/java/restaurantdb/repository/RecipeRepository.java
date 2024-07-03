package restaurantdb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import restaurantdb.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import restaurantdb.model.RestaurantOrder;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    // Запрос с HAVING:
    // Найти рецепты, где общее количество ингредиентов больше определенного значения.
    @Query("SELECT r.recipeName, SUM(ri.quantity) FROM Recipe r JOIN r.recipeIngredients ri GROUP BY r.recipeName HAVING SUM(ri.quantity) > :quantity")
    List<Object[]> findRecipesWithIngredientQuantityGreaterThan(@Param("quantity") BigDecimal quantity);

    Page<Recipe> findAll(Pageable pageable);

    List<Recipe> findByDishId(Long dishId);
}
