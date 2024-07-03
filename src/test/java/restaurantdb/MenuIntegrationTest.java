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
import restaurantdb.dto.MenuDTO;
import restaurantdb.service.MenuService;
import restaurantdb.service.DishService;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class MenuIntegrationTest extends BaseTestConfiguration {

    @Autowired
    private MenuService menuService;

    @Autowired
    private DishService dishService;

    private MenuDTO menu1;
    private MenuDTO menu2;

    @BeforeEach
    void setUp() {
        // Удаляем все меню перед каждым тестом, чтобы избежать накопления данных
        menuService.deleteAllMenus();

        menu1 = new MenuDTO(null, "Breakfast Menu", "Delicious breakfast options", "BREAKFAST", null);
        menu2 = new MenuDTO(null, "Lunch Menu", "Tasty lunch options", "LUNCH", null);

        menu1 = menuService.saveMenu(menu1);
        menu2 = menuService.saveMenu(menu2);
    }

    @Test
    void testCreateAndGetMenu() {
        MenuDTO menu = new MenuDTO(null, "Dinner Menu", "Savory dinner options", "STANDARD", null);
        MenuDTO savedMenu = menuService.saveMenu(menu);

        Optional<MenuDTO> fetchedMenu = menuService.getMenuById(savedMenu.getId());
        assertThat(fetchedMenu).isPresent();
        assertThat(fetchedMenu.get().getName()).isEqualTo(menu.getName());
        assertThat(fetchedMenu.get().getDescription()).isEqualTo(menu.getDescription());
        assertThat(fetchedMenu.get().getType()).isEqualTo(menu.getType());
    }

    @Test
    void testGetAllMenus() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<MenuDTO> menusPage = menuService.getAllMenus(pageable);

        assertThat(menusPage.getContent()).hasSize(2);
    }

    @Test
    void testDeleteMenu() {
        MenuDTO menu = new MenuDTO(null, "Dinner Menu", "Savory dinner options", "STANDARD", null);
        MenuDTO savedMenu = menuService.saveMenu(menu);

        menuService.deleteMenu(savedMenu.getId());
        Optional<MenuDTO> fetchedMenu = menuService.getMenuById(savedMenu.getId());
        assertThat(fetchedMenu).isNotPresent();
    }

    @Test
    void testDeleteAllMenus() {
        menuService.deleteAllMenus();
        Pageable pageable = PageRequest.of(0, 10);
        Page<MenuDTO> menusPage = menuService.getAllMenus(pageable);

        assertThat(menusPage.getContent()).isEmpty();
    }
}
