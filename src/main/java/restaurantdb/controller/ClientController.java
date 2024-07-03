package restaurantdb.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import restaurantdb.dto.BillDTO;
import restaurantdb.dto.ClientDTO;
import restaurantdb.service.BillService;
import restaurantdb.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/clients")
@Validated
public class ClientController {
    @Autowired
    private ClientService clientService;

    @Autowired
    private BillService billService;

    @GetMapping
    public Page<ClientDTO> getAllClients(Pageable pageable) {
        return clientService.getAllClients(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDTO> getClientById(@PathVariable Long id) {
        Optional<ClientDTO> client = clientService.getClientById(id);
        return client.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/bills")
    public ResponseEntity<List<BillDTO>> getClientBills(@PathVariable Long id) {
        Optional<ClientDTO> client = clientService.getClientById(id);
        if (client.isPresent()) {
            List<BillDTO> bills = billService.getBillsByClientId(id);
            return ResponseEntity.ok(bills);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/name")
    public ResponseEntity<ClientDTO> findByClientName(@RequestParam String name) {
        Optional<ClientDTO> clientDTO = clientService.findByClientName(name);
        return clientDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/with-dining-tables")
    public List<Object[]> findClientsAndDiningTableNumbers() {
        return clientService.findClientsAndDiningTableNumbers();
    }

    @GetMapping("/with-orders")
    public List<Object[]> findClientsAndOrders() {
        return clientService.findClientsAndOrders();
    }

    @PostMapping
    public ClientDTO createClient(@Valid @RequestBody ClientDTO clientDTO) {
        return clientService.saveClient(clientDTO);
    }

    @PostMapping("/batch")
    public List<ClientDTO> createClients(@RequestBody List<ClientDTO> clientDTOs) {
        return clientService.saveAllClients(clientDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientDTO> updateClient(@PathVariable Long id, @Valid @RequestBody ClientDTO clientDTO) {
        Optional<ClientDTO> existingClient = clientService.getClientById(id);
        if (existingClient.isPresent()) {
            clientDTO.setId(id);
            return ResponseEntity.ok(clientService.saveClient(clientDTO));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(@PathVariable Long id) {
        clientService.deleteClient(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllClients() {
        clientService.deleteAllClients();
        return ResponseEntity.noContent().build();
    }
}
