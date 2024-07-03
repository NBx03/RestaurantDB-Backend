package restaurantdb.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurantdb.dto.MenuDTO;
import restaurantdb.dto.DishDTO;
import restaurantdb.service.MenuService;
import restaurantdb.service.DishService;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/menus")
public class MenuController {
    @Autowired
    private MenuService menuService;

    @Autowired
    private DishService dishService;

    @GetMapping
    public Page<MenuDTO> getAllMenus(Pageable pageable) {
        return menuService.getAllMenus(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuDTO> getMenuById(@PathVariable Long id) {
        Optional<MenuDTO> menu = menuService.getMenuById(id);
        return menu.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public MenuDTO createMenu(@RequestBody MenuDTO menuDTO) {
        return menuService.saveMenu(menuDTO);
    }

    @PostMapping("/batch")
    public List<MenuDTO> createMenus(@RequestBody List<MenuDTO> menuDTOs) {
        return menuService.saveAllMenus(menuDTOs);
    }

    @PostMapping("/{menuId}/dishes/{dishId}")
    public ResponseEntity<Void> addDishToMenu(@PathVariable Long menuId, @PathVariable Long dishId) {
        Optional<MenuDTO> menu = menuService.getMenuById(menuId);
        Optional<DishDTO> dish = dishService.getDishById(dishId);

        if (menu.isPresent() && dish.isPresent()) {
            menuService.addDishToMenu(menu.get(), dish.get());
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<MenuDTO> updateMenu(@PathVariable Long id, @Valid @RequestBody MenuDTO menuDTO) {
        Optional<MenuDTO> existingMenu = menuService.getMenuById(id);
        if (existingMenu.isPresent()) {
            menuDTO.setId(id);
            return ResponseEntity.ok(menuService.saveMenu(menuDTO));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{menuId}/dishes/{dishId}")
    public ResponseEntity<Void> removeDishFromMenu(@PathVariable Long menuId, @PathVariable Long dishId) {
        Optional<MenuDTO> menu = menuService.getMenuById(menuId);
        Optional<DishDTO> dish = dishService.getDishById(dishId);

        if (menu.isPresent() && dish.isPresent()) {
            menuService.removeDishFromMenu(menu.get(), dish.get());
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(@PathVariable Long id) {
        menuService.deleteMenu(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllMenus() {
        menuService.deleteAllMenus();
        return ResponseEntity.noContent().build();
    }
}
