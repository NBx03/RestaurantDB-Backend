package restaurantdb.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import restaurantdb.dto.RecipeDTO;
import restaurantdb.model.Dish;
import restaurantdb.model.Ingredient;
import restaurantdb.model.Recipe;
import restaurantdb.model.RecipeIngredient;
import restaurantdb.repository.DishRepository;
import restaurantdb.repository.IngredientRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class RecipeMapper {

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    public RecipeDTO toDTO(Recipe recipe) {
        return new RecipeDTO(
                recipe.getId(),
                recipe.getRecipeName(),
                recipe.getDish() != null ? recipe.getDish().getId() : null,
                recipe.getInstructions(),
                recipe.getPreparationTime(),
                recipe.getRecipeIngredients() != null ?
                        recipe.getRecipeIngredients().stream()
                                .map(recipeIngredient -> recipeIngredient.getIngredient().getId())
                                .distinct()
                                .collect(Collectors.toList()) : null
        );
    }

    public Recipe toEntity(RecipeDTO recipeDTO) {
        Recipe recipe = new Recipe();
        recipe.setId(recipeDTO.getId());
        recipe.setRecipeName(recipeDTO.getRecipeName());
        recipe.setInstructions(recipeDTO.getInstructions());
        recipe.setPreparationTime(recipeDTO.getPreparationTime());

        Optional<Dish> dish = dishRepository.findById(recipeDTO.getDishId());
        if (dish.isPresent()) {
            recipe.setDish(dish.get());
        } else {
            throw new IllegalArgumentException("Dish with ID " + recipeDTO.getDishId() + " not found");
        }

        if (recipeDTO.getIngredientIds() != null) {
            List<Ingredient> ingredients = recipeDTO.getIngredientIds().stream()
                    .map(ingredientRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
            recipe.setRecipeIngredients(ingredients.stream()
                    .map(ingredient -> {
                        RecipeIngredient recipeIngredient = new RecipeIngredient();
                        recipeIngredient.setRecipe(recipe);
                        recipeIngredient.setIngredient(ingredient);
                        return recipeIngredient;
                    })
                    .collect(Collectors.toList()));
        } else {
            recipe.setRecipeIngredients(new ArrayList<>());
        }

        return recipe;
    }
}
