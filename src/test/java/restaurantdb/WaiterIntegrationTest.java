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
import restaurantdb.dto.WaiterDTO;
import restaurantdb.service.WaiterService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class WaiterIntegrationTest extends BaseTestConfiguration {

    @Autowired
    private WaiterService waiterService;

    @BeforeEach
    void setUp() {
        waiterService.deleteAllWaiters();

        WaiterDTO manager = new WaiterDTO(null, "Alice", "Smith", null, null, null, null);
        manager = waiterService.saveWaiter(manager);

        WaiterDTO subordinate = new WaiterDTO(null, "Bob", "Johnson", manager.getId(), null, null, null);
        waiterService.saveWaiter(subordinate);
    }

    @Test
    void testCreateAndGetWaiter() {
        WaiterDTO manager = waiterService.getAllWaiters(PageRequest.of(0, 1)).getContent().get(0);

        WaiterDTO waiter = new WaiterDTO(null, "John", "Doe", manager.getId(), null, null, null);
        WaiterDTO savedWaiter = waiterService.saveWaiter(waiter);

        Optional<WaiterDTO> fetchedWaiter = waiterService.getWaiterById(savedWaiter.getId());
        assertThat(fetchedWaiter).isPresent();
        assertThat(fetchedWaiter.get().getName()).isEqualTo(waiter.getName());
        assertThat(fetchedWaiter.get().getSurname()).isEqualTo(waiter.getSurname());
        assertThat(fetchedWaiter.get().getManagerId()).isEqualTo(manager.getId());
    }

    @Test
    void testGetAllWaiters() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<WaiterDTO> waitersPage = waiterService.getAllWaiters(pageable);

        assertThat(waitersPage.getContent()).hasSize(2);
    }

    @Test
    void testDeleteWaiter() {
        WaiterDTO manager = waiterService.getAllWaiters(PageRequest.of(0, 1)).getContent().get(0);

        WaiterDTO waiter = new WaiterDTO(null, "John", "Doe", manager.getId(), null, null, null);
        WaiterDTO savedWaiter = waiterService.saveWaiter(waiter);

        waiterService.deleteWaiter(savedWaiter.getId());
        Optional<WaiterDTO> fetchedWaiter = waiterService.getWaiterById(savedWaiter.getId());
        assertThat(fetchedWaiter).isNotPresent();
    }

    @Test
    void testDeleteAllWaiters() {
        waiterService.deleteAllWaiters();
        Pageable pageable = PageRequest.of(0, 10);
        Page<WaiterDTO> waitersPage = waiterService.getAllWaiters(pageable);

        assertThat(waitersPage.getContent()).isEmpty();
    }

    @Test
    void testFindWaiterHierarchy() {
        List<Object[]> hierarchy = waiterService.findWaiterHierarchy();
        assertThat(hierarchy).isNotEmpty();
    }
}
