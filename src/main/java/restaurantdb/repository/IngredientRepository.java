package restaurantdb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import restaurantdb.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import restaurantdb.model.Menu;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    // Запрос с подзапросом:
    // Найти все ингредиенты, которые используются в рецептах, где количество больше определенного значения.
    @Query("SELECT i FROM Ingredient i WHERE i.id IN (SELECT ri.ingredient.id FROM RecipeIngredient ri GROUP BY ri.ingredient.id HAVING SUM(ri.quantity) > :quantity)")
    List<Ingredient> findIngredientsWithTotalQuantityGreaterThan(@Param("quantity") BigDecimal quantity);

    Page<Ingredient> findAll(Pageable pageable);
}
