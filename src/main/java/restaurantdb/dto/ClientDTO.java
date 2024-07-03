package restaurantdb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientDTO {
    private Long id;
    private String name;
    private String surname;
    private String phoneNumber;
    private Long diningTableId;
    private Integer diningTableNumber;
    private List<Long> orderIds;
    private List<Long> billIds;
}
