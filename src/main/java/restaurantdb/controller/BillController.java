package restaurantdb.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurantdb.dto.BillDTO;
import restaurantdb.model.OrderDishBill;
import restaurantdb.service.BillService;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/bills")
public class BillController {
    @Autowired
    private BillService billService;

    @GetMapping
    public Page<BillDTO> getAllBills(Pageable pageable) {
        return billService.getAllBills(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillDTO> getBillById(@PathVariable Long id) {
        Optional<BillDTO> bill = billService.getBillById(id);
        return bill.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{billId}/dishes")
    public List<OrderDishBill> findDishesByBillId(@PathVariable Long billId) {
        return billService.findDishesByBillId(billId);
    }

    @GetMapping("/total-order-amount")
    public List<Object[]> findBillsWithTotalOrderAmount() {
        return billService.findBillsWithTotalOrderAmount();
    }

    @PostMapping
    public BillDTO createBill(@RequestBody BillDTO billDTO) {
        return billService.saveBill(billDTO);
    }

    @PostMapping("/batch")
    public List<BillDTO> createBills(@RequestBody List<BillDTO> billDTOs) {
        return billService.saveAllBills(billDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillDTO> updateBill(@PathVariable Long id, @Valid @RequestBody BillDTO billDTO) {
        Optional<BillDTO> existingBill = billService.getBillById(id);
        if (existingBill.isPresent()) {
            billDTO.setId(id);
            return ResponseEntity.ok(billService.saveBill(billDTO));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{billId}/add-order-dish")
    public ResponseEntity<BillDTO> addOrderDishToBill(@PathVariable Long billId,
                                                      @RequestParam Long orderId,
                                                      @RequestParam Long dishId,
                                                      @RequestParam int quantity) {
        return ResponseEntity.ok(billService.addOrderDishToBill(billId, orderId, dishId, quantity));
    }

    @DeleteMapping("/{billId}/remove-order-dish")
    public ResponseEntity<BillDTO> removeOrderDishFromBill(@PathVariable Long billId,
                                                           @RequestParam Long orderId,
                                                           @RequestParam Long dishId) {
        return ResponseEntity.ok(billService.removeOrderDishFromBill(billId, orderId, dishId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBill(@PathVariable Long id) {
        billService.deleteBill(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllBills() {
        billService.deleteAllBills();
        return ResponseEntity.noContent().build();
    }
}
