package restaurantdb.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import restaurantdb.dto.WaiterDTO;
import restaurantdb.model.DiningTable;
import restaurantdb.model.RestaurantOrder;
import restaurantdb.model.Waiter;
import restaurantdb.repository.DiningTableRepository;
import restaurantdb.repository.RestaurantOrderRepository;
import restaurantdb.repository.WaiterRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class WaiterMapper {

    @Autowired
    private DiningTableRepository diningTableRepository;

    @Autowired
    private RestaurantOrderRepository restaurantOrderRepository;

    @Autowired
    private WaiterRepository waiterRepository;

    public WaiterDTO toDTO(Waiter waiter) {
        return new WaiterDTO(
                waiter.getId(),
                waiter.getName(),
                waiter.getSurname(),
                waiter.getManager() != null ? waiter.getManager().getId() : null,
                waiter.getDiningTables() != null ?
                        waiter.getDiningTables().stream()
                                .map(DiningTable::getId)
                                .collect(Collectors.toList()) : null,
                waiter.getRestaurantOrders() != null ?
                        waiter.getRestaurantOrders().stream()
                                .map(RestaurantOrder::getId)
                                .collect(Collectors.toList()) : null,
                waiter.getSubordinates() != null ?
                        waiter.getSubordinates().stream()
                                .map(Waiter::getId)
                                .collect(Collectors.toList()) : null
        );
    }

    public Waiter toEntity(WaiterDTO waiterDTO) {
        Waiter waiter = new Waiter();
        waiter.setId(waiterDTO.getId());
        waiter.setName(waiterDTO.getName());
        waiter.setSurname(waiterDTO.getSurname());

        if (waiterDTO.getManagerId() != null) {
            Optional<Waiter> manager = waiterRepository.findById(waiterDTO.getManagerId());
            manager.ifPresent(waiter::setManager);
        }

        if (waiterDTO.getDiningTableIds() != null) {
            List<DiningTable> diningTables = waiterDTO.getDiningTableIds().stream()
                    .map(diningTableRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            waiter.setDiningTables(diningTables);
        } else {
            waiter.setDiningTables(new ArrayList<>());
        }

        if (waiterDTO.getRestaurantOrderIds() != null) {
            List<RestaurantOrder> restaurantOrders = waiterDTO.getRestaurantOrderIds().stream()
                    .map(restaurantOrderRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            waiter.setRestaurantOrders(restaurantOrders);
        } else {
            waiter.setRestaurantOrders(new ArrayList<>());
        }

        if (waiterDTO.getSubordinateIds() != null) {
            List<Waiter> subordinates = waiterDTO.getSubordinateIds().stream()
                    .map(waiterRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
            waiter.setSubordinates(subordinates);
        } else {
            waiter.setSubordinates(new ArrayList<>());
        }

        return waiter;
    }
}
