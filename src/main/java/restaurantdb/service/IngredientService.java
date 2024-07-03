package restaurantdb.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import restaurantdb.annotation.MyTransactional;
import restaurantdb.dto.IngredientDTO;
import restaurantdb.mapper.IngredientMapper;
import restaurantdb.model.Ingredient;
import restaurantdb.repository.IngredientRepository;

import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IngredientService {
    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private IngredientMapper ingredientMapper;

    @MyTransactional(readOnly = true)
    public Page<IngredientDTO> getAllIngredients(Pageable pageable) {
        return ingredientRepository.findAll(pageable).map(ingredientMapper::toDTO);
    }

    @MyTransactional(readOnly = true)
    public Optional<IngredientDTO> getIngredientById(Long id) {
        return ingredientRepository.findById(id).map(ingredientMapper::toDTO);
    }

    @MyTransactional(readOnly = true)
    public List<IngredientDTO> findIngredientsWithTotalQuantityGreaterThan(BigDecimal quantity) {
        return ingredientRepository.findIngredientsWithTotalQuantityGreaterThan(quantity)
                .stream().map(ingredientMapper::toDTO).collect(Collectors.toList());
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public IngredientDTO saveIngredient(IngredientDTO ingredientDTO) {
        Ingredient ingredient = ingredientMapper.toEntity(ingredientDTO);
        return ingredientMapper.toDTO(ingredientRepository.save(ingredient));
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public List<IngredientDTO> saveAllIngredients(List<IngredientDTO> ingredientDTOs) {
        List<Ingredient> ingredients = ingredientDTOs.stream()
                .map(ingredientMapper::toEntity)
                .collect(Collectors.toList());
        ingredientRepository.saveAll(ingredients);
        return ingredients.stream()
                .map(ingredientMapper::toDTO)
                .collect(Collectors.toList());
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public void deleteIngredient(Long id) {
        ingredientRepository.deleteById(id);
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public void deleteAllIngredients() {
        ingredientRepository.deleteAll();
    }
}
