package restaurantdb.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import restaurantdb.dto.RestaurantOrderDTO;
import restaurantdb.model.*;
import restaurantdb.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class RestaurantOrderMapper {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private WaiterRepository waiterRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private RestaurantOrderRepository restaurantOrderRepository;

    public RestaurantOrderDTO toDTO(RestaurantOrder restaurantOrder) {
        return new RestaurantOrderDTO(
                restaurantOrder.getId(),
                restaurantOrder.getOrderDate(),
                restaurantOrder.getTotalAmount(),
                restaurantOrder.getStatus() != null ? restaurantOrder.getStatus().name() : null,
                restaurantOrder.getClient() != null ? restaurantOrder.getClient().getId() : null,
                restaurantOrder.getWaiter() != null ? restaurantOrder.getWaiter().getId() : null,
                restaurantOrder.getOrderDishBills() != null ?
                        restaurantOrder.getOrderDishBills().stream()
                                .map(orderDishBill -> orderDishBill.getDish().getId())
                                .distinct()
                                .collect(Collectors.toList()) : null,
                restaurantOrder.getOrderDishBills() != null ?
                        restaurantOrder.getOrderDishBills().stream()
                                .map(orderDishBill -> (orderDishBill.getBill() != null ? orderDishBill.getBill().getId() : null))
                                .distinct()
                                .collect(Collectors.toList()) : null,
                restaurantOrder.getOrderDishBills() != null ?
                        restaurantOrder.getOrderDishBills().stream()
                                .map(OrderDishBill::getDishQuantity)
                                .collect(Collectors.toList()) : null
        );
    }

    public RestaurantOrder toEntity(RestaurantOrderDTO restaurantOrderDTO) {
        RestaurantOrder restaurantOrder = new RestaurantOrder();
        restaurantOrder.setId(restaurantOrderDTO.getId());
        restaurantOrder.setOrderDate(restaurantOrderDTO.getOrderDate() != null ? restaurantOrderDTO.getOrderDate() : LocalDateTime.now());
        restaurantOrder.setTotalAmount(restaurantOrderDTO.getTotalAmount());
        restaurantOrder.setStatus(RestaurantOrder.OrderStatus.valueOf(restaurantOrderDTO.getStatus()));

        Optional<Client> client = clientRepository.findById(restaurantOrderDTO.getClientId());
        if (client.isPresent()) {
            restaurantOrder.setClient(client.get());
        } else {
            throw new IllegalArgumentException("Client with ID " + restaurantOrderDTO.getClientId() + " not found");
        }

        Optional<Waiter> waiter = waiterRepository.findById(restaurantOrderDTO.getWaiterId());
        if (waiter.isPresent()) {
            restaurantOrder.setWaiter(waiter.get());
        } else {
            throw new IllegalArgumentException("Waiter with ID " + restaurantOrderDTO.getWaiterId() + " not found");
        }

        if (restaurantOrderDTO.getDishIds() != null && restaurantOrderDTO.getDishQuantities() != null) {
            List<OrderDishBill> orderDishBills = new ArrayList<>();
            for (int i = 0; i < restaurantOrderDTO.getDishIds().size(); i++) {
                Optional<Dish> dish = dishRepository.findById(restaurantOrderDTO.getDishIds().get(i));
                if (dish.isPresent()) {
                    OrderDishBill orderDishBill = new OrderDishBill();
                    orderDishBill.setOrder(restaurantOrder);
                    orderDishBill.setDish(dish.get());
                    orderDishBill.setDishQuantity(restaurantOrderDTO.getDishQuantities().get(i));
                    orderDishBills.add(orderDishBill);
                } else {
                    throw new IllegalArgumentException("Dish with ID " + restaurantOrderDTO.getDishIds().get(i) + " not found");
                }
            }
            restaurantOrder.setOrderDishBills(orderDishBills);
        } else {
            restaurantOrder.setOrderDishBills(new ArrayList<>());
        }

        return restaurantOrder;
    }
}


