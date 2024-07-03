package restaurantdb.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import restaurantdb.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long>, CustomClientRepository {
    Optional<Client> findByClientName(String name);

    // Запрос с INNER JOIN:
    // Найти всех клиентов и номера их столов.
    @Query("SELECT c.name, dt.number FROM Client c INNER JOIN c.diningTable dt")
    List<Object[]> findClientsAndDiningTableNumbers();

    // Запрос с OUTER JOIN:
    // Найти всех клиентов и их заказы, включая клиентов без заказов.
    @Query("SELECT c.name, o.id FROM Client c LEFT JOIN c.restaurantOrder o")
    List<Object[]> findClientsAndOrders();

    Page<Client> findAll(Pageable pageable);

    Optional<Client> findByNameAndSurname(String clientName, String clientSurname);
}
