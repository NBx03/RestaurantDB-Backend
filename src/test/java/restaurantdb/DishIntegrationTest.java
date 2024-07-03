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
import restaurantdb.dto.DishDTO;
import restaurantdb.service.DishService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class DishIntegrationTest extends BaseTestConfiguration {

    @Autowired
    private DishService dishService;

    private DishDTO dish1;
    private DishDTO dish2;

    @BeforeEach
    void setUp() {
        // Удаляем все блюда перед каждым тестом, чтобы избежать накопления данных
        dishService.deleteAllDishes();

        dish1 = new DishDTO(null, "Pasta", "Delicious pasta", BigDecimal.valueOf(12.99), true, "MAIN_COURSE", null, null, null, null);
        dish2 = new DishDTO(null, "Salad", "Fresh salad", BigDecimal.valueOf(7.99), true, "APPETIZER", null, null, null, null);

        dish1 = dishService.saveDish(dish1);
        dish2 = dishService.saveDish(dish2);
    }

    @Test
    void testCreateAndGetDish() {
        DishDTO dish = new DishDTO(null, "Soup", "Hot soup", BigDecimal.valueOf(5.99), true, "APPETIZER", null, null, null, null);
        DishDTO savedDish = dishService.saveDish(dish);

        Optional<DishDTO> fetchedDish = dishService.getDishById(savedDish.getId());
        assertThat(fetchedDish).isPresent();
        assertThat(fetchedDish.get().getName()).isEqualTo(dish.getName());
        assertThat(fetchedDish.get().getDescription()).isEqualTo(dish.getDescription());
        assertThat(fetchedDish.get().getPrice()).isEqualTo(dish.getPrice());
        assertThat(fetchedDish.get().isAvailability()).isEqualTo(dish.isAvailability());
        assertThat(fetchedDish.get().getCategory()).isEqualTo(dish.getCategory());
    }

    @Test
    void testGetAllDishes() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<DishDTO> dishesPage = dishService.getAllDishes(pageable);

        assertThat(dishesPage.getContent()).hasSize(2);
    }

    @Test
    void testDeleteDish() {
        DishDTO dish = new DishDTO(null, "Soup", "Hot soup", BigDecimal.valueOf(5.99), true, "APPETIZER", null, null, null, null);
        DishDTO savedDish = dishService.saveDish(dish);

        dishService.deleteDish(savedDish.getId());
        Optional<DishDTO> fetchedDish = dishService.getDishById(savedDish.getId());
        assertThat(fetchedDish).isNotPresent();
    }

    @Test
    void testDeleteAllDishes() {
        dishService.deleteAllDishes();
        Pageable pageable = PageRequest.of(0, 10);
        Page<DishDTO> dishesPage = dishService.getAllDishes(pageable);

        assertThat(dishesPage.getContent()).isEmpty();
    }

    @Test
    void testGetAvailableDishesSortedByPrice() {
        List<DishDTO> availableDishes = dishService.getAvailableDishesSortedByPrice();
        assertThat(availableDishes).hasSize(2);
        assertThat(availableDishes.get(0).getPrice()).isEqualTo(BigDecimal.valueOf(7.99));
        assertThat(availableDishes.get(1).getPrice()).isEqualTo(BigDecimal.valueOf(12.99));
    }
}
