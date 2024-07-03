package restaurantdb;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import restaurantdb.dto.ClientDTO;
import restaurantdb.dto.DiningTableDTO;
import restaurantdb.dto.WaiterDTO;
import restaurantdb.service.ClientService;
import restaurantdb.service.DiningTableService;
import restaurantdb.service.WaiterService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DiningTableIntegrationTest extends BaseTestConfiguration {

    @Autowired
    private DiningTableService diningTableService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private WaiterService waiterService;

    private ClientDTO client1;
    private ClientDTO client2;
    private WaiterDTO waiter;

    @BeforeEach
    void setUp() {
        client1 = new ClientDTO(null, "John", "Doe", "+79123456789", null, null, null);
        client1 = clientService.saveClient(client1);

        client2 = new ClientDTO(null, "Jane", "Doe", "+79123456788", null, null, null);
        client2 = clientService.saveClient(client2);

        waiter = new WaiterDTO(null, "Alice", "Smith", null, null, null, null);
        waiter = waiterService.saveWaiter(waiter);
    }

    @Test
    void testCreateAndGetDiningTable() {
        DiningTableDTO diningTable = new DiningTableDTO(null, 1, 4, "ORDINARY_HALL", client1.getId(), waiter.getId());
        DiningTableDTO savedDiningTable = diningTableService.saveDiningTable(diningTable);

        Optional<DiningTableDTO> fetchedDiningTable = diningTableService.getDiningTableById(savedDiningTable.getId());
        assertThat(fetchedDiningTable).isPresent();
        assertThat(fetchedDiningTable.get().getNumber()).isEqualTo(diningTable.getNumber());
        assertThat(fetchedDiningTable.get().getCapacity()).isEqualTo(diningTable.getCapacity());
        assertThat(fetchedDiningTable.get().getLocation()).isEqualTo(diningTable.getLocation());
        assertThat(fetchedDiningTable.get().getClientId()).isEqualTo(client1.getId());
        assertThat(fetchedDiningTable.get().getWaiterId()).isEqualTo(waiter.getId());
    }

    @Test
    void testGetAllDiningTables() {
        DiningTableDTO diningTable1 = new DiningTableDTO(null, 1, 4, "ORDINARY_HALL", client1.getId(), waiter.getId());
        DiningTableDTO diningTable2 = new DiningTableDTO(null, 2, 2, "VIP_ROOM", client2.getId(), waiter.getId());

        diningTableService.saveDiningTable(diningTable1);
        diningTableService.saveDiningTable(diningTable2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<DiningTableDTO> diningTablesPage = diningTableService.getAllDiningTables(pageable);

        assertThat(diningTablesPage.getContent()).hasSize(2);
    }

    @Test
    void testDeleteDiningTable() {
        DiningTableDTO diningTable = new DiningTableDTO(null, 1, 4, "ORDINARY_HALL", client1.getId(), waiter.getId());
        DiningTableDTO savedDiningTable = diningTableService.saveDiningTable(diningTable);

        diningTableService.deleteDiningTable(savedDiningTable.getId());
        Optional<DiningTableDTO> fetchedDiningTable = diningTableService.getDiningTableById(savedDiningTable.getId());
        assertThat(fetchedDiningTable).isNotPresent();
    }

    @Test
    void testDeleteAllDiningTables() {
        DiningTableDTO diningTable1 = new DiningTableDTO(null, 1, 4, "ORDINARY_HALL", client1.getId(), waiter.getId());
        DiningTableDTO diningTable2 = new DiningTableDTO(null, 2, 2, "VIP_ROOM", client2.getId(), waiter.getId());

        diningTableService.saveDiningTable(diningTable1);
        diningTableService.saveDiningTable(diningTable2);

        diningTableService.deleteAllDiningTables();
        Pageable pageable = PageRequest.of(0, 10);
        Page<DiningTableDTO> diningTablesPage = diningTableService.getAllDiningTables(pageable);

        assertThat(diningTablesPage.getContent()).isEmpty();
    }
}
