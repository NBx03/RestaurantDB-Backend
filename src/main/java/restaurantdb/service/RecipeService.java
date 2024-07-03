package restaurantdb.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import restaurantdb.annotation.MyTransactional;
import restaurantdb.dto.RecipeDTO;
import restaurantdb.mapper.RecipeMapper;
import restaurantdb.model.Recipe;
import restaurantdb.repository.RecipeRepository;

import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RecipeService {
    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private RecipeMapper recipeMapper;

    @MyTransactional(readOnly = true)
    public Page<RecipeDTO> getAllRecipes(Pageable pageable) {
        return recipeRepository.findAll(pageable).map(recipeMapper::toDTO);
    }

    @MyTransactional(readOnly = true)
    public Optional<RecipeDTO> getRecipeById(Long id) {
        return recipeRepository.findById(id).map(recipeMapper::toDTO);
    }

    public List<RecipeDTO> getRecipesByDishId(Long dishId) {
        List<Recipe> recipes = recipeRepository.findByDishId(dishId);
        return recipes.stream()
                .map(recipeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @MyTransactional(readOnly = true)
    public List<Object[]> findRecipesWithIngredientQuantityGreaterThan(BigDecimal quantity) {
        return recipeRepository.findRecipesWithIngredientQuantityGreaterThan(quantity);
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public RecipeDTO saveRecipe(RecipeDTO recipeDTO) {
        Recipe recipe = recipeMapper.toEntity(recipeDTO);
        return recipeMapper.toDTO(recipeRepository.save(recipe));
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public List<RecipeDTO> saveAllRecipes(List<RecipeDTO> recipeDTOs) {
        List<Recipe> recipes = recipeDTOs.stream()
                .map(recipeMapper::toEntity)
                .collect(Collectors.toList());
        recipeRepository.saveAll(recipes);
        return recipes.stream()
                .map(recipeMapper::toDTO)
                .collect(Collectors.toList());
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public void deleteAllRecipes() {
        recipeRepository.deleteAll();
    }
}
