package restaurantdb.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import restaurantdb.dto.IngredientDTO;
import restaurantdb.model.Ingredient;
import restaurantdb.model.Recipe;
import restaurantdb.model.RecipeIngredient;
import restaurantdb.model.Supplier;
import restaurantdb.repository.RecipeRepository;
import restaurantdb.repository.SupplierRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class IngredientMapper {

    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    public IngredientDTO toDTO(Ingredient ingredient) {
        return new IngredientDTO(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getDescription(),
                ingredient.getStockQuantity(),
                ingredient.getSupplier() != null ? ingredient.getSupplier().getId() : null,
                ingredient.getRecipeIngredients() != null ?
                        ingredient.getRecipeIngredients().stream()
                                .map(recipeIngredient -> recipeIngredient.getRecipe().getId())
                                .distinct()
                                .collect(Collectors.toList()) : null
        );
    }

    public Ingredient toEntity(IngredientDTO ingredientDTO) {
        Ingredient ingredient = new Ingredient();
        ingredient.setId(ingredientDTO.getId());
        ingredient.setName(ingredientDTO.getName());
        ingredient.setDescription(ingredientDTO.getDescription());
        ingredient.setStockQuantity(ingredientDTO.getStockQuantity());

        if (ingredientDTO.getSupplierId() != null) {
            Supplier supplier = supplierRepository.findById(ingredientDTO.getSupplierId()).orElse(null);
            ingredient.setSupplier(supplier);
        } else {
            ingredient.setSupplier(null);
        }

        if (ingredientDTO.getRecipeIds() != null) {
            List<Recipe> recipes = ingredientDTO.getRecipeIds().stream()
                    .map(recipeRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
            ingredient.setRecipeIngredients(recipes.stream()
                    .map(recipe -> {
                        RecipeIngredient recipeIngredient = new RecipeIngredient();
                        recipeIngredient.setRecipe(recipe);
                        recipeIngredient.setIngredient(ingredient);
                        return recipeIngredient;
                    })
                    .collect(Collectors.toList()));
        } else {
            ingredient.setRecipeIngredients(new ArrayList<>());
        }

        return ingredient;
    }
}
