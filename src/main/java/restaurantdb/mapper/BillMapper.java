package restaurantdb.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import restaurantdb.dto.BillDTO;
import restaurantdb.model.Bill;
import restaurantdb.model.OrderDishBill;
import restaurantdb.repository.ClientRepository;
import restaurantdb.repository.DishRepository;
import restaurantdb.repository.RestaurantOrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class BillMapper {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private RestaurantOrderRepository restaurantOrderRepository;

    public BillDTO toDTO(Bill bill) {
        return new BillDTO(
                bill.getId(),
                bill.getTotalAmount(),
                bill.getIssuedAt(),
                bill.getClient().getId(),
                bill.getClient().getName(),
                bill.getClient().getSurname(),
                bill.getOrderDishBills() != null ?
                        bill.getOrderDishBills().stream()
                                .map(orderDishBill -> orderDishBill.getOrder().getId())
                                .distinct()
                                .collect(Collectors.toList()) : null,
                bill.getOrderDishBills() != null ?
                        bill.getOrderDishBills().stream()
                                .map(orderDishBill -> orderDishBill.getDish().getId())
                                .distinct()
                                .collect(Collectors.toList()) : null,
                bill.getOrderDishBills() != null ? bill.getOrderDishBills().stream()
                        .map(OrderDishBill::getDishQuantity)
                        .collect(Collectors.toList()) : null
        );
    }

    public Bill toEntity(BillDTO billDTO) {
        Bill bill = new Bill();
        bill.setId(billDTO.getId());
        bill.setTotalAmount(billDTO.getTotalAmount());
        bill.setIssuedAt(billDTO.getIssuedAt());

        bill.setClient(billDTO.getClientId() != null ? clientRepository.findById(billDTO.getClientId()).orElse(null) : null);

        if (billDTO.getOrderIds() != null && billDTO.getDishIds() != null && billDTO.getDishQuantities() != null) {
            List<OrderDishBill> orderDishBills = IntStream.range(0, billDTO.getOrderIds().size())
                    .mapToObj(i -> new OrderDishBill(
                            null,
                            restaurantOrderRepository.findById(billDTO.getOrderIds().get(i)).orElse(null),
                            dishRepository.findById(billDTO.getDishIds().get(i)).orElse(null),
                            bill,
                            billDTO.getDishQuantities().get(i)
                    ))
                    .filter(orderDishBill -> orderDishBill.getOrder() != null && orderDishBill.getDish() != null)
                    .collect(Collectors.toList());
            bill.setOrderDishBills(orderDishBills);
        } else {
            bill.setOrderDishBills(new ArrayList<>());
        }

        return bill;
    }
}
