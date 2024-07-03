package restaurantdb.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import restaurantdb.annotation.MyTransactional;
import restaurantdb.dto.SupplierDTO;
import restaurantdb.mapper.SupplierMapper;
import restaurantdb.model.Supplier;
import restaurantdb.repository.SupplierRepository;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SupplierService {
    @Autowired
    private SupplierRepository supplierRepository;

    @Autowired
    private SupplierMapper supplierMapper;

    @MyTransactional(readOnly = true)
    public Page<SupplierDTO> getAllSuppliers(Pageable pageable) {
        return supplierRepository.findAll(pageable).map(supplierMapper::toDTO);
    }

    @MyTransactional(readOnly = true)
    public Optional<SupplierDTO> getSupplierById(Long id) {
        return supplierRepository.findById(id).map(supplierMapper::toDTO);
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public SupplierDTO saveSupplier(SupplierDTO supplierDTO) {
        Supplier supplier = supplierMapper.toEntity(supplierDTO);
        return supplierMapper.toDTO(supplierRepository.save(supplier));
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public List<SupplierDTO> saveAllSuppliers(List<SupplierDTO> supplierDTOs) {
        List<Supplier> suppliers = supplierDTOs.stream()
                .map(supplierMapper::toEntity)
                .collect(Collectors.toList());
        supplierRepository.saveAll(suppliers);
        return suppliers.stream()
                .map(supplierMapper::toDTO)
                .collect(Collectors.toList());
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public void deleteSupplier(Long id) {
        supplierRepository.deleteById(id);
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public void deleteAllSuppliers() {
        supplierRepository.deleteAll();
    }
}
