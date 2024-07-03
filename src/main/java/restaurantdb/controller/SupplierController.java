package restaurantdb.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurantdb.dto.SupplierDTO;
import restaurantdb.service.SupplierService;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {
    @Autowired
    private SupplierService supplierService;

    @GetMapping
    public Page<SupplierDTO> getAllSuppliers(Pageable pageable) {
        return supplierService.getAllSuppliers(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable Long id) {
        Optional<SupplierDTO> supplier = supplierService.getSupplierById(id);
        return supplier.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public SupplierDTO createSupplier(@RequestBody SupplierDTO supplierDTO) {
        return supplierService.saveSupplier(supplierDTO);
    }

    @PostMapping("/batch")
    public List<SupplierDTO> createSuppliers(@RequestBody List<SupplierDTO> supplierDTOs) {
        return supplierService.saveAllSuppliers(supplierDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SupplierDTO> updateSupplier(@PathVariable Long id, @Valid @RequestBody SupplierDTO supplierDTO) {
        Optional<SupplierDTO> existingSupplier = supplierService.getSupplierById(id);
        if (existingSupplier.isPresent()) {
            supplierDTO.setId(id);
            return ResponseEntity.ok(supplierService.saveSupplier(supplierDTO));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllSuppliers() {
        supplierService.deleteAllSuppliers();
        return ResponseEntity.noContent().build();
    }
}
