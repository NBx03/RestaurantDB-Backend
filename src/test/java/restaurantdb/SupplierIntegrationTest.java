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
import restaurantdb.dto.SupplierDTO;
import restaurantdb.service.SupplierService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class SupplierIntegrationTest extends BaseTestConfiguration {

    @Autowired
    private SupplierService supplierService;

    private SupplierDTO supplier1;
    private SupplierDTO supplier2;

    @BeforeEach
    void setUp() {
        // Удаляем всех поставщиков перед каждым тестом, чтобы избежать накопления данных
        supplierService.deleteAllSuppliers();

        supplier1 = new SupplierDTO(null, "Supplier One", null);
        supplier2 = new SupplierDTO(null, "Supplier Two", null);

        supplier1 = supplierService.saveSupplier(supplier1);
        supplier2 = supplierService.saveSupplier(supplier2);
    }

    @Test
    void testCreateAndGetSupplier() {
        SupplierDTO supplier = new SupplierDTO(null, "Supplier Three", null);
        SupplierDTO savedSupplier = supplierService.saveSupplier(supplier);

        Optional<SupplierDTO> fetchedSupplier = supplierService.getSupplierById(savedSupplier.getId());
        assertThat(fetchedSupplier).isPresent();
        assertThat(fetchedSupplier.get().getName()).isEqualTo(supplier.getName());
    }

    @Test
    void testGetAllSuppliers() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<SupplierDTO> suppliersPage = supplierService.getAllSuppliers(pageable);

        assertThat(suppliersPage.getContent()).hasSize(2);
    }

    @Test
    void testDeleteSupplier() {
        SupplierDTO supplier = new SupplierDTO(null, "Supplier Four", null);
        SupplierDTO savedSupplier = supplierService.saveSupplier(supplier);

        supplierService.deleteSupplier(savedSupplier.getId());
        Optional<SupplierDTO> fetchedSupplier = supplierService.getSupplierById(savedSupplier.getId());
        assertThat(fetchedSupplier).isNotPresent();
    }

    @Test
    void testDeleteAllSuppliers() {
        supplierService.deleteAllSuppliers();
        Pageable pageable = PageRequest.of(0, 10);
        Page<SupplierDTO> suppliersPage = supplierService.getAllSuppliers(pageable);

        assertThat(suppliersPage.getContent()).isEmpty();
    }
}
