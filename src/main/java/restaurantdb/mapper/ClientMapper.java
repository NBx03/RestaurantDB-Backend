package restaurantdb.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import restaurantdb.dto.ClientDTO;
import restaurantdb.model.Bill;
import restaurantdb.model.Client;
import restaurantdb.model.DiningTable;
import restaurantdb.model.RestaurantOrder;
import restaurantdb.repository.DiningTableRepository;
import restaurantdb.repository.RestaurantOrderRepository;
import restaurantdb.repository.BillRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ClientMapper {

    @Autowired
    private DiningTableRepository diningTableRepository;

    @Autowired
    private RestaurantOrderRepository restaurantOrderRepository;

    @Autowired
    private BillRepository billRepository;

    public ClientDTO toDTO(Client client) {
        return new ClientDTO(
                client.getId(),
                client.getName(),
                client.getSurname(),
                client.getPhoneNumber(),
                client.getDiningTable() != null ? client.getDiningTable().getId() : null,
                client.getDiningTable() != null ? client.getDiningTable().getNumber() : null,
                client.getRestaurantOrder() != null ?
                        client.getRestaurantOrder().stream()
                                .map(RestaurantOrder::getId)
                                .collect(Collectors.toList()) : null,
                client.getBills() != null ?
                        client.getBills().stream()
                                .map(Bill::getId)
                                .collect(Collectors.toList()) : null
        );
    }

    public Client toEntity(ClientDTO clientDTO) {
        Client client = new Client();
        client.setId(clientDTO.getId());
        client.setName(clientDTO.getName());
        client.setSurname(clientDTO.getSurname());
        client.setPhoneNumber(clientDTO.getPhoneNumber());

        if (clientDTO.getDiningTableId() != null) {
            DiningTable diningTable = diningTableRepository.findById(clientDTO.getDiningTableId()).orElse(null);
            if (diningTable != null) {
                diningTable.setClient(client);
                client.setDiningTable(diningTable);
            }
        } else {
            client.setDiningTable(null);
        }

        if (clientDTO.getDiningTableNumber() != null) {
            Optional<DiningTable> diningTable = diningTableRepository.findByNumber(clientDTO.getDiningTableNumber());
            diningTable.ifPresent(client::setDiningTable);
        }

        if (clientDTO.getOrderIds() != null) {
            List<RestaurantOrder> orders = clientDTO.getOrderIds().stream()
                    .map(orderId -> restaurantOrderRepository.findById(orderId).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            client.setRestaurantOrder(orders);
        } else {
            client.setRestaurantOrder(new ArrayList<>());
        }

        if (clientDTO.getBillIds() != null) {
            List<Bill> bills = clientDTO.getBillIds().stream()
                    .map(billId -> billRepository.findById(billId).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            client.setBills(bills);
        } else {
            client.setBills(new ArrayList<>());
        }

        return client;
    }
}
