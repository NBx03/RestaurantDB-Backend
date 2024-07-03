package restaurantdb.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import restaurantdb.dto.DiningTableDTO;
import restaurantdb.model.Client;
import restaurantdb.model.DiningTable;
import restaurantdb.model.Waiter;
import restaurantdb.repository.ClientRepository;
import restaurantdb.repository.WaiterRepository;

import java.util.Optional;

@Component
public class DiningTableMapper {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private WaiterRepository waiterRepository;

    public DiningTableDTO toDTO(DiningTable diningTable) {
        return new DiningTableDTO(
                diningTable.getId(),
                diningTable.getNumber(),
                diningTable.getCapacity(),
                diningTable.getLocation() != null ? diningTable.getLocation().name() : null,
                diningTable.getClient() != null ? diningTable.getClient().getId() : null,
                diningTable.getClient() != null ? diningTable.getClient().getName() : null,
                diningTable.getClient() != null ? diningTable.getClient().getSurname() : null,
                diningTable.getWaiter() != null ? diningTable.getWaiter().getId() : null
        );
    }

    public DiningTable toEntity(DiningTableDTO diningTableDTO) {
        DiningTable diningTable = new DiningTable();
        diningTable.setId(diningTableDTO.getId());
        diningTable.setNumber(diningTableDTO.getNumber());
        diningTable.setCapacity(diningTableDTO.getCapacity());

        if (diningTableDTO.getClientId() != null) {
            Client client = clientRepository.findById(diningTableDTO.getClientId()).orElse(null);
            diningTable.setClient(client);
        } else {
            diningTable.setClient(null);
        }

        if (diningTableDTO.getClientName() != null && diningTableDTO.getClientSurname() != null) {
            Optional<Client> client = clientRepository.findByNameAndSurname(diningTableDTO.getClientName(), diningTableDTO.getClientSurname());
            client.ifPresent(diningTable::setClient);
        }

        if (diningTableDTO.getWaiterId() != null) {
            Waiter waiter = waiterRepository.findById(diningTableDTO.getWaiterId()).orElse(null);
            diningTable.setWaiter(waiter);
        } else {
            diningTable.setWaiter(null);
        }

        return diningTable;
    }
}
