package restaurantdb.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Propagation;
import restaurantdb.annotation.MyTransactional;
import restaurantdb.dto.DishDTO;
import restaurantdb.mapper.DishMapper;
import restaurantdb.model.Dish;
import restaurantdb.model.OrderDishBill;
import restaurantdb.repository.BillRepository;
import restaurantdb.repository.DishRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import restaurantdb.repository.OrderDishBillRepository;
import restaurantdb.repository.RestaurantOrderRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DishService {
    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private OrderDishBillRepository orderDishBillRepository;

    @Autowired
    private RestaurantOrderRepository restaurantOrderRepository;

    @Autowired
    private BillRepository billRepository;

    @MyTransactional(readOnly = true)
    public Page<DishDTO> getAllDishes(Pageable pageable) {
        return dishRepository.findAll(pageable).map(dishMapper::toDTO);
    }

    @MyTransactional(readOnly = true)
    public Optional<DishDTO> getDishById(Long id) {
        return dishRepository.findById(id).map(dishMapper::toDTO);
    }

    @MyTransactional(readOnly = true)
    public List<DishDTO> getAvailableDishesSortedByPrice() {
        return dishRepository.findByAvailabilityTrueOrderByPriceAsc()
                .stream().map(dishMapper::toDTO).collect(Collectors.toList());
    }

    @MyTransactional(readOnly = true)
    public List<DishDTO> findDishesByMenuType(String type) {
        return dishRepository.findDishesByMenuType(type)
                .stream().map(dishMapper::toDTO).collect(Collectors.toList());
    }

    @MyTransactional(readOnly = true)
    public List<OrderDishBill> findOrdersByDishId(Long dishId) {
        return orderDishBillRepository.findOrdersByDishId(dishId);
    }

    @MyTransactional(readOnly = true)
    public List<OrderDishBill> findBillsByDishId(Long dishId) {
        return orderDishBillRepository.findBillsByDishId(dishId);
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public DishDTO saveDish(DishDTO dishDTO) {
        Dish dish = dishMapper.toEntity(dishDTO);
        return dishMapper.toDTO(dishRepository.save(dish));
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public List<DishDTO> saveAllDishes(List<DishDTO> dishDTOs) {
        List<Dish> dishes = dishDTOs.stream()
                .map(dishMapper::toEntity)
                .collect(Collectors.toList());
        dishRepository.saveAll(dishes);
        return dishes.stream()
                .map(dishMapper::toDTO)
                .collect(Collectors.toList());
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public void upsertDish(DishDTO dishDTO) {
        Dish dish = dishMapper.toEntity(dishDTO);
        dishRepository.upsertDish(
                dish.getId(),
                dish.getName(),
                dish.getDescription(),
                dish.getPrice().doubleValue(),
                dish.isAvailability(),
                dish.getCategory().name()
        );
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public DishDTO addOrderToDish(Long dishId, Long orderId, Long billId, int quantity) {
        OrderDishBill orderDishBill = new OrderDishBill();
        orderDishBill.setDish(dishRepository.findById(dishId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid dish ID")));
        orderDishBill.setOrder(restaurantOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID")));
        orderDishBill.setBill(billRepository.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid bill ID")));
        orderDishBill.setDishQuantity(quantity);
        orderDishBillRepository.save(orderDishBill);
        return dishMapper.toDTO(dishRepository.findById(dishId).orElseThrow(() -> new IllegalArgumentException("Invalid dish ID")));
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public DishDTO removeOrderFromDish(Long dishId, Long orderId, Long billId) {
        OrderDishBill orderDishBill = orderDishBillRepository.findByOrderIdAndDishIdAndBillId(orderId, dishId, billId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid combination of order, dish, and bill ID"));
        orderDishBillRepository.delete(orderDishBill);
        return dishMapper.toDTO(dishRepository.findById(dishId).orElseThrow(() -> new IllegalArgumentException("Invalid dish ID")));
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public void deleteDish(Long id) {
        dishRepository.deleteById(id);
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public void deleteAllDishes() {
        dishRepository.deleteAll();
    }
}
