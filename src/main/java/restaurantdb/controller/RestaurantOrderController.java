package restaurantdb.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurantdb.dto.RestaurantOrderDTO;
import restaurantdb.service.RestaurantOrderService;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/orders")
public class RestaurantOrderController {
    @Autowired
    private RestaurantOrderService restaurantOrderService;

    @GetMapping
    public Page<RestaurantOrderDTO> getAllOrders(Pageable pageable) {
        return restaurantOrderService.getAllOrders(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantOrderDTO> getOrderById(@PathVariable Long id) {
        Optional<RestaurantOrderDTO> order = restaurantOrderService.getOrderById(id);
        return order.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/count-by-status")
    public List<Object[]> countOrdersByStatus() {
        return restaurantOrderService.countOrdersByStatus();
    }

    @PostMapping
    public RestaurantOrderDTO createOrder(@RequestBody RestaurantOrderDTO orderDTO) {
        return restaurantOrderService.saveOrder(orderDTO);
    }

    @PostMapping("/batch")
    public List<RestaurantOrderDTO> createOrders(@RequestBody List<RestaurantOrderDTO> orderDTOs) {
        return restaurantOrderService.saveAllOrders(orderDTOs);
    }


    @PutMapping("/{id}")
    public ResponseEntity<RestaurantOrderDTO> updateOrder(@PathVariable Long id, @Valid @RequestBody RestaurantOrderDTO orderDTO) {
        Optional<RestaurantOrderDTO> existingOrder = restaurantOrderService.getOrderById(id);
        if (existingOrder.isPresent()) {
            orderDTO.setId(id);
            return ResponseEntity.ok(restaurantOrderService.saveOrder(orderDTO));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{orderId}/add-dish")
    public ResponseEntity<RestaurantOrderDTO> addDishToOrder(@PathVariable Long orderId,
                                                             @RequestParam Long dishId,
                                                             @RequestParam Long billId,
                                                             @RequestParam int quantity) {
        return ResponseEntity.ok(restaurantOrderService.addDishToOrder(orderId, dishId, billId, quantity));
    }

    @DeleteMapping("/{orderId}/remove-dish")
    public ResponseEntity<RestaurantOrderDTO> removeDishFromOrder(@PathVariable Long orderId,
                                                                  @RequestParam Long dishId,
                                                                  @RequestParam Long billId) {
        return ResponseEntity.ok(restaurantOrderService.removeDishFromOrder(orderId, dishId, billId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        restaurantOrderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllOrders() {
        restaurantOrderService.deleteAllOrders();
        return ResponseEntity.noContent().build();
    }
}
