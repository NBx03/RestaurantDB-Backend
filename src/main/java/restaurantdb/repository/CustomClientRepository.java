package restaurantdb.repository;

import restaurantdb.model.Client;

import java.util.Optional;

public interface CustomClientRepository {
    Optional<Client> findByClientName(String name);
}
