package restaurantdb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WaiterDTO {
    private Long id;
    private String name;
    private String surname;
    private Long managerId;
    private List<Long> diningTableIds;
    private List<Long> restaurantOrderIds;
    private List<Long> subordinateIds;
}
