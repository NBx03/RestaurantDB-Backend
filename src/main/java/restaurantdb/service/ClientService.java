package restaurantdb.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import restaurantdb.annotation.MyTransactional;
import restaurantdb.dto.ClientDTO;
import restaurantdb.mapper.ClientMapper;
import restaurantdb.model.Client;
import restaurantdb.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ClientService {
    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ClientMapper clientMapper;

    @MyTransactional(readOnly = true)
    public Page<ClientDTO> getAllClients(Pageable pageable) {
        return clientRepository.findAll(pageable).map(clientMapper::toDTO);
    }

    @MyTransactional(readOnly = true)
    public Optional<ClientDTO> getClientById(Long id) {
        return clientRepository.findById(id).map(clientMapper::toDTO);
    }

    @MyTransactional(readOnly = true)
    public Optional<ClientDTO> findByClientName(String name) {
        return clientRepository.findByClientName(name).map(clientMapper::toDTO);
    }

    @MyTransactional(readOnly = true)
    public List<Object[]> findClientsAndDiningTableNumbers() {
        return clientRepository.findClientsAndDiningTableNumbers();
    }

    @MyTransactional(readOnly = true)
    public List<Object[]> findClientsAndOrders() {
        return clientRepository.findClientsAndOrders();
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public ClientDTO saveClient(@Valid ClientDTO clientDTO) {
        Client client = clientMapper.toEntity(clientDTO);
        return clientMapper.toDTO(clientRepository.save(client));
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public List<ClientDTO> saveAllClients(List<ClientDTO> clientDTOs) {
        List<Client> clients = clientDTOs.stream()
                .map(clientMapper::toEntity)
                .collect(Collectors.toList());
        clientRepository.saveAll(clients);
        return clients.stream()
                .map(clientMapper::toDTO)
                .collect(Collectors.toList());
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public void deleteClient(Long id) {
        clientRepository.deleteById(id);
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public void deleteAllClients() {
        clientRepository.deleteAll();
    }
}
