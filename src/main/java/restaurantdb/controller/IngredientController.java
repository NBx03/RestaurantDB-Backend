package restaurantdb.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurantdb.dto.IngredientDTO;
import restaurantdb.service.IngredientService;

import org.springframework.beans.factory.annotation.Autowired;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {
    @Autowired
    private IngredientService ingredientService;

    @GetMapping
    public Page<IngredientDTO> getAllIngredients(Pageable pageable) {
        return ingredientService.getAllIngredients(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IngredientDTO> getIngredientById(@PathVariable Long id) {
        Optional<IngredientDTO> ingredient = ingredientService.getIngredientById(id);
        return ingredient.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/with-quantity-greater-than")
    public List<IngredientDTO> findIngredientsWithTotalQuantityGreaterThan(@RequestParam BigDecimal quantity) {
        return ingredientService.findIngredientsWithTotalQuantityGreaterThan(quantity);
    }

    @PostMapping
    public IngredientDTO createIngredient(@RequestBody IngredientDTO ingredientDTO) {
        return ingredientService.saveIngredient(ingredientDTO);
    }

    @PostMapping("/batch")
    public List<IngredientDTO> createIngredients(@RequestBody List<IngredientDTO> ingredientDTOs) {
        return ingredientService.saveAllIngredients(ingredientDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IngredientDTO> updateIngredient(@PathVariable Long id, @Valid @RequestBody IngredientDTO ingredientDTO) {
        Optional<IngredientDTO> existingIngredient = ingredientService.getIngredientById(id);
        if (existingIngredient.isPresent()) {
            ingredientDTO.setId(id);
            return ResponseEntity.ok(ingredientService.saveIngredient(ingredientDTO));
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        ingredientService.deleteIngredient(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllIngredients() {
        ingredientService.deleteAllIngredients();
        return ResponseEntity.noContent().build();
    }
}
