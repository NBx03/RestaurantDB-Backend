package restaurantdb.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import restaurantdb.annotation.MyTransactional;
import restaurantdb.dto.BillDTO;
import restaurantdb.mapper.BillMapper;
import restaurantdb.model.Bill;
import restaurantdb.model.OrderDishBill;
import restaurantdb.repository.BillRepository;
import restaurantdb.repository.DishRepository;
import restaurantdb.repository.OrderDishBillRepository;

import org.springframework.beans.factory.annotation.Autowired;
import restaurantdb.repository.RestaurantOrderRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BillService {
    @Autowired
    private BillRepository billRepository;

    @Autowired
    private OrderDishBillRepository orderDishBillRepository;

    @Autowired
    private BillMapper billMapper;

    @Autowired
    private RestaurantOrderRepository restaurantOrderRepository;

    @Autowired
    private DishRepository dishRepository;

    @MyTransactional(readOnly = true)
    public Page<BillDTO> getAllBills(Pageable pageable) {
        return billRepository.findAll(pageable).map(billMapper::toDTO);
    }

    @MyTransactional(readOnly = true)
    public Optional<BillDTO> getBillById(Long id) {
        return billRepository.findById(id).map(bill -> {
            BillDTO billDTO = billMapper.toDTO(bill);
            // Assuming Bill entity has a getClient() method
            if (bill.getClient() != null) {
                billDTO.setClientName(bill.getClient().getName());
                billDTO.setClientSurname(bill.getClient().getSurname());
            }
            return billDTO;
        });
    }

    @MyTransactional(readOnly = true)
    public List<BillDTO> getBillsByClientId(Long clientId) {
        List<Bill> bills = billRepository.findByClientId(clientId);
        return bills.stream()
                .map(billMapper::toDTO)
                .collect(Collectors.toList());
    }

    @MyTransactional(readOnly = true)
    public List<Object[]> findBillsWithTotalOrderAmount() {
        return billRepository.findBillsWithTotalOrderAmount();
    }

    @MyTransactional(readOnly = true)
    public List<OrderDishBill> findDishesByBillId(Long billId) {
        return orderDishBillRepository.findDishesByBillId(billId);
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public BillDTO saveBill(BillDTO billDTO) {
        Bill bill = billMapper.toEntity(billDTO);
        return billMapper.toDTO(billRepository.save(bill));
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public List<BillDTO> saveAllBills(List<BillDTO> billDTOs) {
        List<Bill> bills = billDTOs.stream()
                .map(billMapper::toEntity)
                .collect(Collectors.toList());
        billRepository.saveAll(bills);
        return bills.stream()
                .map(billMapper::toDTO)
                .collect(Collectors.toList());
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public BillDTO addOrderDishToBill(Long billId, Long orderId, Long dishId, int quantity) {
        OrderDishBill orderDishBill = new OrderDishBill();
        orderDishBill.setBill(billRepository.findById(billId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid bill ID")));
        orderDishBill.setOrder(restaurantOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid order ID")));
        orderDishBill.setDish(dishRepository.findById(dishId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid dish ID")));
        orderDishBill.setDishQuantity(quantity);
        orderDishBillRepository.save(orderDishBill);
        return billMapper.toDTO(billRepository.findById(billId).orElseThrow(() -> new IllegalArgumentException("Invalid bill ID")));
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public BillDTO removeOrderDishFromBill(Long billId, Long orderId, Long dishId) {
        OrderDishBill orderDishBill = orderDishBillRepository.findByOrderIdAndDishIdAndBillId(orderId, dishId, billId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid combination of order, dish, and bill ID"));
        orderDishBillRepository.delete(orderDishBill);
        return billMapper.toDTO(billRepository.findById(billId).orElseThrow(() -> new IllegalArgumentException("Invalid bill ID")));
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public void deleteBill(Long id) {
        billRepository.deleteById(id);
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public void deleteAllBills() {
        billRepository.deleteAll();
    }
}
