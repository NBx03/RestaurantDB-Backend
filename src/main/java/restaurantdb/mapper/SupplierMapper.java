package restaurantdb.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import restaurantdb.dto.SupplierDTO;
import restaurantdb.model.Ingredient;
import restaurantdb.model.Supplier;
import restaurantdb.repository.IngredientRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class SupplierMapper {

    @Autowired
    private IngredientRepository ingredientRepository;

    public SupplierDTO toDTO(Supplier supplier) {
        return new SupplierDTO(
                supplier.getId(),
                supplier.getName(),
                supplier.getIngredients() != null ?
                        supplier.getIngredients().stream()
                                .map(Ingredient::getId)
                                .collect(Collectors.toList()) : null
        );
    }

    public Supplier toEntity(SupplierDTO supplierDTO) {
        Supplier supplier = new Supplier();
        supplier.setId(supplierDTO.getId());
        supplier.setName(supplierDTO.getName());

        if (supplierDTO.getIngredientIds() != null) {
            List<Ingredient> ingredients = supplierDTO.getIngredientIds().stream()
                    .map(ingredientRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            supplier.setIngredients(ingredients);
        } else {
            supplier.setIngredients(new ArrayList<>());
        }

        return supplier;
    }
}
