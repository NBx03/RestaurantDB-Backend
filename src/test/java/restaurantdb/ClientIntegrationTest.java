package restaurantdb;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import restaurantdb.dto.ClientDTO;
import restaurantdb.service.ClientService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ClientIntegrationTest extends BaseTestConfiguration {

    @Autowired
    private ClientService clientService;

    @Test
    void testGetAllClients() {
        Pageable pageable = PageRequest.of(0, 10);
        var clients = clientService.getAllClients(pageable);
        assertThat(clients).isNotNull();
        assertThat(clients.getContent()).isEmpty();
    }

    @Test
    void testGetClientById() {
        ClientDTO client = new ClientDTO(null, "John", "Doe", "+79123456789", null, null, null);
        client = clientService.saveClient(client);

        Optional<ClientDTO> foundClient = clientService.getClientById(client.getId());
        assertThat(foundClient).isPresent();
        assertThat(foundClient.get().getName()).isEqualTo("John");
    }

    @Test
    void testSaveClient() {
        ClientDTO client = new ClientDTO(null, "John", "Doe", "+79123456789", null, null, null);
        client = clientService.saveClient(client);

        assertThat(client.getId()).isNotNull();
        assertThat(client.getName()).isEqualTo("John");
    }

    @Test
    void testDeleteClient() {
        ClientDTO client = new ClientDTO(null, "John", "Doe", "+79123456789", null, null, null);
        client = clientService.saveClient(client);

        clientService.deleteClient(client.getId());

        Optional<ClientDTO> foundClient = clientService.getClientById(client.getId());
        assertThat(foundClient).isEmpty();
    }

    @Test
    void testFindClientsAndDiningTableNumbers() {
        ClientDTO client = new ClientDTO(null, "John", "Doe", "+79123456789", null, null, null);
        clientService.saveClient(client);

        List<Object[]> results = clientService.findClientsAndDiningTableNumbers();
        assertThat(results).isNotNull();
        assertThat(results).isEmpty();
    }

    @Test
    void testFindClientsAndOrders() {
        ClientDTO client = new ClientDTO(null, "John", "Doe", "+79123456789", null, null, null);
        clientService.saveClient(client);

        List<Object[]> results = clientService.findClientsAndOrders();
        assertThat(results).isNotNull();
        assertThat(results).isNotEmpty();
    }
}
