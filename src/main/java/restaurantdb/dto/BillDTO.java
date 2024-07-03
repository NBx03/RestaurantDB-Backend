package restaurantdb.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BillDTO {
    private Long id;
    private BigDecimal totalAmount;
    private LocalDateTime issuedAt;
    private Long clientId;
    private String clientName;
    private String clientSurname;
    private List<Long> orderIds;
    private List<Long> dishIds;
    private List<Integer> dishQuantities;
}
