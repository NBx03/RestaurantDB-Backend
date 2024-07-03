package restaurantdb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DishDTO {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private boolean availability;
    private String category;
    private List<Long> menuIds;
    private List<Long> orderIds;
    private List<Long> billIds;
    private List<Long> recipeIds;
}
