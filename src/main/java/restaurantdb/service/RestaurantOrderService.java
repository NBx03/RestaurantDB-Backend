package restaurantdb.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import restaurantdb.annotation.MyTransactional;
import restaurantdb.dto.RestaurantOrderDTO;
import restaurantdb.mapper.RestaurantOrderMapper;
import restaurantdb.model.OrderDishBill;
import restaurantdb.model.RestaurantOrder;
import restaurantdb.repository.BillRepository;
import restaurantdb.repository.DishRepository;
import restaurantdb.repository.OrderDishBillRepository;
import restaurantdb.repository.RestaurantOrderRepository;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RestaurantOrderService {
    @Autowired
    private RestaurantOrderRepository restaurantOrderRepository;

    @Autowired
    private RestaurantOrderMapper restaurantOrderMapper;

    @Autowired
    private OrderDishBillRepository orderDishBillRepository;

    @Autowired
    private DishRepository dishRepository;

    @Autowired
    private BillRepository billRepository;

    @MyTransactional(readOnly = true)
    public Page<RestaurantOrderDTO> getAllOrders(Pageable pageable) {
        return restaurantOrderRepository.findAll(pageable).map(restaurantOrderMapper::toDTO);
    }

    @MyTransactional(readOnly = true)
    public Optional<RestaurantOrderDTO> getOrderById(Long id) {
        return restaurantOrderRepository.findById(id).map(restaurantOrderMapper::toDTO);
    }

    @MyTransactional(readOnly = true)
    public List<Object[]> countOrdersByStatus() {
        return restaurantOrderRepository.countOrdersByStatus();
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public RestaurantOrderDTO saveOrder(RestaurantOrderDTO orderDTO) {
        RestaurantOrder order = restaurantOrderMapper.toEntity(orderDTO);
        return restaurantOrderMapper.toDTO(restaurantOrderRepository.save(order));
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public List<RestaurantOrderDTO> saveAllOrders(List<RestaurantOrderDTO> orderDTOs) {
        List<RestaurantOrder> orders = orderDTOs.stream()
                .map(restaurantOrderMapper::toEntity)
                .collect(Collectors.toList());
        restaurantOrderRepository.saveAll(orders);
        return orders.stream()
                .map(restaurantOrderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public RestaurantOrderDTO addDishToOrder(Long orderId, Long dishId, Long billId, int quantity) {
        RestaurantOrder order = restaurantOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID"));
        OrderDishBill orderDishBill = new OrderDishBill();
        orderDishBill.setOrder(order);
        orderDishBill.setDish(dishRepository.findById(dishId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid dish ID")));
        orderDishBill.setBill(billRepository.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid bill ID")));
        orderDishBill.setDishQuantity(quantity);
        orderDishBillRepository.save(orderDishBill);
        return restaurantOrderMapper.toDTO(order);
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public RestaurantOrderDTO removeDishFromOrder(Long orderId, Long dishId, Long billId) {
        OrderDishBill orderDishBill = orderDishBillRepository.findByOrderIdAndDishIdAndBillId(orderId, dishId, billId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid combination of order, dish, and bill ID"));
        orderDishBillRepository.delete(orderDishBill);
        RestaurantOrder order = restaurantOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID"));
        return restaurantOrderMapper.toDTO(order);
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public void deleteOrder(Long id) {
        restaurantOrderRepository.deleteById(id);
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public void deleteAllOrders() {
        restaurantOrderRepository.deleteAll();
    }
}
