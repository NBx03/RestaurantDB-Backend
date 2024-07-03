package restaurantdb.controller;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import restaurantdb.dto.DiningTableDTO;
import restaurantdb.service.DiningTableService;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/dining-tables")
public class DiningTableController {
    @Autowired
    private DiningTableService diningTableService;

    @GetMapping
    public Page<DiningTableDTO> getAllDiningTables(Pageable pageable) {
        return diningTableService.getAllDiningTables(pageable);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DiningTableDTO> getDiningTableById(@PathVariable Long id) {
        Optional<DiningTableDTO> table = diningTableService.getDiningTableById(id);
        return table.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public DiningTableDTO createDiningTable(@RequestBody DiningTableDTO diningTableDTO) {
        return diningTableService.saveDiningTable(diningTableDTO);
    }

    @PostMapping("/batch")
    public List<DiningTableDTO> createDiningTables(@RequestBody List<DiningTableDTO> diningTableDTOs) {
        return diningTableService.saveAllDiningTables(diningTableDTOs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DiningTableDTO> updateDiningTable(@PathVariable Long id, @Valid @RequestBody DiningTableDTO diningTableDTO) {
        Optional<DiningTableDTO> existingDiningTable = diningTableService.getDiningTableById(id);
        if (existingDiningTable.isPresent()) {
            diningTableDTO.setId(id);
            return ResponseEntity.ok(diningTableService.saveDiningTable(diningTableDTO));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiningTable(@PathVariable Long id) {
        diningTableService.deleteDiningTable(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteAllDiningTables() {
        diningTableService.deleteAllDiningTables();
        return ResponseEntity.noContent().build();
    }
}
