package restaurantdb.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurantdb.dto.RecipeDTO;
import restaurantdb.service.RecipeService;

import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/recipes")
public class RecipeController {
    @Autowired
    private RecipeService recipeService;

    @GetMapping
    public Page<RecipeDTO> getAllRecipes(Pageable pageable) {
        return recipeService.getAllRecipes(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RecipeDTO> getRecipeById(@PathVariable Long id) {
        Optional<RecipeDTO> recipe = recipeService.getRecipeById(id);
        return recipe.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-dish/{dishId}")
    public ResponseEntity<List<RecipeDTO>> getRecipesByDishId(@PathVariable Long dishId) {
        List<RecipeDTO> recipes = recipeService.getRecipesByDishId(dishId);
        return ResponseEntity.ok(recipes);
    }

    @GetMapping("/with-ingredients-greater-than")
    public List<Object[]> findRecipesWithIngredientQuantityGreaterThan(@RequestParam BigDecimal quantity) {
        return recipeService.findRecipesWithIngredientQuantityGreaterThan(quantity);
    }

    @PostMapping
    public RecipeDTO createRecipe(@RequestBody RecipeDTO recipeDTO) {
        return recipeService.saveRecipe(recipeDTO);
    }

    @PostMapping("/batch")
    public List<RecipeDTO> createRecipes(@RequestBody List<RecipeDTO> recipeDTOs) {
        return recipeService.saveAllRecipes(recipeDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RecipeDTO> updateRecipe(@PathVariable Long id, @Valid @RequestBody RecipeDTO recipeDTO) {
        Optional<RecipeDTO> existingRecipe = recipeService.getRecipeById(id);
        if (existingRecipe.isPresent()) {
            recipeDTO.setId(id);
            return ResponseEntity.ok(recipeService.saveRecipe(recipeDTO));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllRecipes() {
        recipeService.deleteAllRecipes();
        return ResponseEntity.noContent().build();
    }
}
