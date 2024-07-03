package restaurantdb.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import restaurantdb.annotation.MyTransactional;
import restaurantdb.dto.WaiterDTO;
import restaurantdb.mapper.WaiterMapper;
import restaurantdb.model.Waiter;
import restaurantdb.repository.WaiterRepository;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WaiterService {
    @Autowired
    private WaiterRepository waiterRepository;

    @Autowired
    private WaiterMapper waiterMapper;

    @MyTransactional(readOnly = true)
    public Page<WaiterDTO> getAllWaiters(Pageable pageable) {
        return waiterRepository.findAll(pageable).map(waiterMapper::toDTO);
    }

    @MyTransactional(readOnly = true)
    public Optional<WaiterDTO> getWaiterById(Long id) {
        return waiterRepository.findById(id).map(waiterMapper::toDTO);
    }

    @MyTransactional(readOnly = true)
    public List<Object[]> findWaiterHierarchy() {
        return waiterRepository.findWaiterHierarchy();
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public WaiterDTO saveWaiter(WaiterDTO waiterDTO) {
        Waiter waiter = waiterMapper.toEntity(waiterDTO);
        return waiterMapper.toDTO(waiterRepository.save(waiter));
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public List<WaiterDTO> saveAllWaiters(List<WaiterDTO> waiterDTOs) {
        List<Waiter> waiters = waiterDTOs.stream()
                .map(waiterMapper::toEntity)
                .collect(Collectors.toList());
        waiterRepository.saveAll(waiters);
        return waiters.stream()
                .map(waiterMapper::toDTO)
                .collect(Collectors.toList());
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public void deleteWaiter(Long id) {
        waiterRepository.deleteById(id);
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public void deleteAllWaiters() {
        waiterRepository.deleteAll();
    }
}
