package restaurantdb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiningTableDTO {
    private Long id;
    private int number;
    private int capacity;
    private String location;
    private Long clientId;
    private String clientName; // Имя клиента
    private String clientSurname; // Фамилия клиента
    private Long waiterId;
}
