package restaurantdb.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import restaurantdb.annotation.MyTransactional;
import restaurantdb.dto.DiningTableDTO;
import restaurantdb.mapper.DiningTableMapper;
import restaurantdb.model.DiningTable;
import restaurantdb.repository.DiningTableRepository;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DiningTableService {
    @Autowired
    private DiningTableRepository diningTableRepository;

    @Autowired
    private DiningTableMapper diningTableMapper;

    @MyTransactional(readOnly = true)
    public Page<DiningTableDTO> getAllDiningTables(Pageable pageable) {
        return diningTableRepository.findAll(pageable).map(diningTableMapper::toDTO);
    }

    @MyTransactional(readOnly = true)
    public Optional<DiningTableDTO> getDiningTableById(Long id) {
        return diningTableRepository.findById(id).map(diningTableMapper::toDTO);
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public DiningTableDTO saveDiningTable(DiningTableDTO diningTableDTO) {
        DiningTable diningTable = diningTableMapper.toEntity(diningTableDTO);
        return diningTableMapper.toDTO(diningTableRepository.save(diningTable));
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public List<DiningTableDTO> saveAllDiningTables(List<DiningTableDTO> diningTableDTOs) {
        List<DiningTable> diningTables = diningTableDTOs.stream()
                .map(diningTableMapper::toEntity)
                .collect(Collectors.toList());
        diningTableRepository.saveAll(diningTables);
        return diningTables.stream()
                .map(diningTableMapper::toDTO)
                .collect(Collectors.toList());
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public void deleteDiningTable(Long id) {
        diningTableRepository.deleteById(id);
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public void deleteAllDiningTables() {
        diningTableRepository.deleteAll();
    }
}
