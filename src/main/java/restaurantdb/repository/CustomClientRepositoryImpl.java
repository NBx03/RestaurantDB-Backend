package restaurantdb.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import restaurantdb.model.Client;

import java.util.Optional;

@Repository
public class CustomClientRepositoryImpl implements CustomClientRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Optional<Client> findByClientName(String name) {
        TypedQuery<Client> query = entityManager.createQuery(
                "SELECT c FROM Client c WHERE c.name = :name", Client.class);
        query.setParameter("name", name);
        return query.getResultList().stream().findFirst();
    }
}
