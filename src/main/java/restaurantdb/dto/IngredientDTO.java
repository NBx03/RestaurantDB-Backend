package restaurantdb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IngredientDTO {
    private Long id;
    private String name;
    private String description;
    private Integer stockQuantity;
    private Long supplierId;
    private List<Long> recipeIds;
}
