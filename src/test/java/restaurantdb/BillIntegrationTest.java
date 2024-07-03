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
import restaurantdb.dto.BillDTO;
import restaurantdb.dto.ClientDTO;
import restaurantdb.dto.DishDTO;
import restaurantdb.dto.RestaurantOrderDTO;
import restaurantdb.service.BillService;
import restaurantdb.service.ClientService;
import restaurantdb.service.DishService;
import restaurantdb.service.RestaurantOrderService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BillIntegrationTest extends BaseTestConfiguration {

    @Autowired
    private BillService billService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private DishService dishService;

    @Autowired
    private RestaurantOrderService orderService;

    private ClientDTO client;
    private DishDTO dish;
    private RestaurantOrderDTO order;

    @BeforeEach
    void setUp() {
        client = new ClientDTO(null, "John", "Doe", "+79123456789", null, null, null);
        client = clientService.saveClient(client);

        dish = new DishDTO(null, "Pizza", "Delicious pizza", new BigDecimal("10.00"), true, "MAIN_COURSE", null, null, null, null);
        dish = dishService.saveDish(dish);

        order = new RestaurantOrderDTO(null, LocalDateTime.now(), new BigDecimal("100.00"), "PENDING", client.getId(), null, null, null, List.of(2));
        order = orderService.saveOrder(order);
    }

    @Test
    void testCreateAndGetBill() {
        BillDTO bill = new BillDTO(null, new BigDecimal("50.00"), LocalDateTime.now(), client.getId(), List.of(order.getId()), List.of(dish.getId()), List.of(2));
        BillDTO savedBill = billService.saveBill(bill);

        Optional<BillDTO> fetchedBill = billService.getBillById(savedBill.getId());
        assertThat(fetchedBill).isPresent();
        assertThat(fetchedBill.get().getTotalAmount()).isEqualTo(bill.getTotalAmount());
        assertThat(fetchedBill.get().getClientId()).isEqualTo(client.getId());
        assertThat(fetchedBill.get().getOrderIds()).containsExactlyInAnyOrder(order.getId());
        assertThat(fetchedBill.get().getDishIds()).containsExactlyInAnyOrder(dish.getId());
        assertThat(fetchedBill.get().getDishQuantities()).containsExactly(2);
    }

    @Test
    void testFindBillsWithTotalOrderAmount() {
        BillDTO bill = new BillDTO(null, new BigDecimal("50.00"), LocalDateTime.now(), client.getId(), List.of(order.getId()), List.of(dish.getId()), List.of(2));
        billService.saveBill(bill);

        List<Object[]> results = billService.findBillsWithTotalOrderAmount();
        assertThat(results).isNotEmpty();

        for (Object[] result : results) {
            assertThat(result).hasSize(5);  // id, total_amount, issued_at, client_id, total_order_amount
            assertThat(result[4]).isInstanceOf(BigDecimal.class);  // total_order_amount
        }
    }

    @Test
    void testGetAllBills() {
        BillDTO bill1 = new BillDTO(null, new BigDecimal("50.00"), LocalDateTime.now(), client.getId(), List.of(order.getId()), List.of(dish.getId()), List.of(2));
        BillDTO bill2 = new BillDTO(null, new BigDecimal("75.00"), LocalDateTime.now(), client.getId(), List.of(order.getId()), List.of(dish.getId()), List.of(3));

        billService.saveBill(bill1);
        billService.saveBill(bill2);

        Pageable pageable = PageRequest.of(0, 10);
        Page<BillDTO> billsPage = billService.getAllBills(pageable);

        assertThat(billsPage.getContent()).hasSize(2);
    }

    @Test
    void testDeleteBill() {
        BillDTO bill = new BillDTO(null, new BigDecimal("50.00"), LocalDateTime.now(), client.getId(), List.of(order.getId()), List.of(dish.getId()), List.of(2));
        BillDTO savedBill = billService.saveBill(bill);

        billService.deleteBill(savedBill.getId());
        Optional<BillDTO> fetchedBill = billService.getBillById(savedBill.getId());
        assertThat(fetchedBill).isNotPresent();
    }

    @Test
    void testDeleteAllBills() {
        BillDTO bill1 = new BillDTO(null, new BigDecimal("50.00"), LocalDateTime.now(), client.getId(), List.of(order.getId()), List.of(dish.getId()), List.of(2));
        BillDTO bill2 = new BillDTO(null, new BigDecimal("75.00"), LocalDateTime.now(), client.getId(), List.of(order.getId()), List.of(dish.getId()), List.of(3));

        billService.saveBill(bill1);
        billService.saveBill(bill2);

        billService.deleteAllBills();
        Pageable pageable = PageRequest.of(0, 10);
        Page<BillDTO> billsPage = billService.getAllBills(pageable);

        assertThat(billsPage.getContent()).isEmpty();
    }
}
