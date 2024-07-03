package restaurantdb.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import restaurantdb.dto.DishDTO;
import restaurantdb.model.OrderDishBill;
import restaurantdb.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dishes")
public class DishController {
    @Autowired
    private DishService dishService;

    @GetMapping
    public Page<DishDTO> getAllDishes(Pageable pageable) {
        return dishService.getAllDishes(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DishDTO> getDishById(@PathVariable Long id) {
        Optional<DishDTO> dish = dishService.getDishById(id);
        return dish.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/available")
    public List<DishDTO> getAvailableDishesSortedByPrice() {
        return dishService.getAvailableDishesSortedByPrice();
    }

    @GetMapping("/{dishId}/orders")
    public List<OrderDishBill> findOrdersByDishId(@PathVariable Long dishId) {
        return dishService.findOrdersByDishId(dishId);
    }

    @GetMapping("/{dishId}/bills")
    public List<OrderDishBill> findBillsByDishId(@PathVariable Long dishId) {
        return dishService.findBillsByDishId(dishId);
    }

    @GetMapping("/menu-type")
    public List<DishDTO> findDishesByMenuType(@RequestParam String type) {
        return dishService.findDishesByMenuType(type);
    }

    @PostMapping
    public DishDTO createDish(@RequestBody DishDTO dishDTO) {
        return dishService.saveDish(dishDTO);
    }

    @PostMapping("/batch")
    public List<DishDTO> createDishes(@RequestBody List<DishDTO> dishDTOs) {
        return dishService.saveAllDishes(dishDTOs);
    }

    @PutMapping("/{id}/upsert")
    public ResponseEntity<DishDTO> upsertDish(@PathVariable Long id, @Valid @RequestBody DishDTO dishDTO) {
        dishDTO.setId(id);
        dishService.upsertDish(dishDTO);
        return ResponseEntity.ok(dishDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DishDTO> updateDish(@PathVariable Long id, @RequestBody DishDTO dishDTO) {
        Optional<DishDTO> existingDish = dishService.getDishById(id);
        if (existingDish.isPresent()) {
            dishDTO.setId(id);
            return ResponseEntity.ok(dishService.saveDish(dishDTO));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{dishId}/add-order")
    public ResponseEntity<DishDTO> addOrderToDish(@PathVariable Long dishId,
                                                  @RequestParam Long orderId,
                                                  @RequestParam Long billId,
                                                  @RequestParam int quantity) {
        return ResponseEntity.ok(dishService.addOrderToDish(dishId, orderId, billId, quantity));
    }

    @DeleteMapping("/{dishId}/remove-order")
    public ResponseEntity<DishDTO> removeOrderFromDish(@PathVariable Long dishId,
                                                       @RequestParam Long orderId,
                                                       @RequestParam Long billId) {
        return ResponseEntity.ok(dishService.removeOrderFromDish(dishId, orderId, billId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDish(@PathVariable Long id) {
        dishService.deleteDish(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllDishes() {
        dishService.deleteAllDishes();
        return ResponseEntity.noContent().build();
    }
}
