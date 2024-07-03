package restaurantdb.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import restaurantdb.dto.WaiterDTO;
import restaurantdb.service.WaiterService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/waiters",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class WaiterController {
    @Autowired
    private WaiterService waiterService;

    @GetMapping
    public Page<WaiterDTO> getAllWaiters(Pageable pageable) {
        return waiterService.getAllWaiters(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WaiterDTO> getWaiterById(@PathVariable Long id) {
        Optional<WaiterDTO> waiter = waiterService.getWaiterById(id);
        return waiter.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/hierarchy")
    public List<Object[]> findWaiterHierarchy() {
        return waiterService.findWaiterHierarchy();
    }

    @PostMapping
    public WaiterDTO createWaiter(@RequestBody WaiterDTO waiterDTO) {
        return waiterService.saveWaiter(waiterDTO);
    }

    @PostMapping("/batch")
    public List<WaiterDTO> createWaiters(@RequestBody List<WaiterDTO> waiterDTOs) {
        return waiterService.saveAllWaiters(waiterDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<WaiterDTO> updateWaiter(@PathVariable Long id, @Valid @RequestBody WaiterDTO waiterDTO) {
        Optional<WaiterDTO> existingWaiter = waiterService.getWaiterById(id);
        if (existingWaiter.isPresent()) {
            waiterDTO.setId(id);
            return ResponseEntity.ok(waiterService.saveWaiter(waiterDTO));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/manager/{managerId}")
    public ResponseEntity<WaiterDTO> updateManager(@PathVariable Long id, @PathVariable Long managerId) {
        Optional<WaiterDTO> waiterOpt = waiterService.getWaiterById(id);
        Optional<WaiterDTO> managerOpt = waiterService.getWaiterById(managerId);

        if (waiterOpt.isPresent() && managerOpt.isPresent()) {
            WaiterDTO waiterDTO = waiterOpt.get();
            waiterDTO.setManagerId(managerOpt.get().getId());
            waiterService.saveWaiter(waiterDTO);
            return ResponseEntity.ok(waiterDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWaiter(@PathVariable Long id) {
        waiterService.deleteWaiter(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllWaiters() {
        waiterService.deleteAllWaiters();
        return ResponseEntity.noContent().build();
    }
}
