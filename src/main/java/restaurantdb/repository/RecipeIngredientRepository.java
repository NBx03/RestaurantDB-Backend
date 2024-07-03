package restaurantdb.repository;

import org.springframework.data.jpa.repository.Query;
import restaurantdb.model.Ingredient;
import restaurantdb.model.Recipe;
import restaurantdb.model.RecipeIngredient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {
    void deleteByRecipeAndIngredient(Recipe recipe, Ingredient ingredient);
}
