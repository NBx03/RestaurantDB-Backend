package restaurantdb.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import restaurantdb.annotation.MyTransactional;
import restaurantdb.dto.MenuDTO;
import restaurantdb.dto.DishDTO;
import restaurantdb.mapper.MenuMapper;
import restaurantdb.mapper.DishMapper;
import restaurantdb.model.Dish;
import restaurantdb.model.Menu;
import restaurantdb.model.MenuDish;
import restaurantdb.repository.MenuRepository;
import restaurantdb.repository.MenuDishRepository;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MenuService {
    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private MenuDishRepository menuDishRepository;

    @MyTransactional(readOnly = true)
    public Page<MenuDTO> getAllMenus(Pageable pageable) {
        return menuRepository.findAll(pageable).map(menuMapper::toDTO);
    }

    @MyTransactional(readOnly = true)
    public Optional<MenuDTO> getMenuById(Long id) {
        return menuRepository.findById(id).map(menuMapper::toDTO);
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public MenuDTO saveMenu(MenuDTO menuDTO) {
        Menu menu = menuMapper.toEntity(menuDTO);
        return menuMapper.toDTO(menuRepository.save(menu));
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public List<MenuDTO> saveAllMenus(List<MenuDTO> menuDTOs) {
        List<Menu> menus = menuDTOs.stream()
                .map(menuMapper::toEntity)
                .collect(Collectors.toList());
        menuRepository.saveAll(menus);
        return menus.stream()
                .map(menuMapper::toDTO)
                .collect(Collectors.toList());
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public void addDishToMenu(MenuDTO menuDTO, DishDTO dishDTO) {
        Menu menu = menuMapper.toEntity(menuDTO);
        Dish dish = dishMapper.toEntity(dishDTO);

        MenuDish menuDish = new MenuDish();
        menuDish.setMenu(menu);
        menuDish.setDish(dish);
        menuDishRepository.save(menuDish);
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public void removeDishFromMenu(MenuDTO menuDTO, DishDTO dishDTO) {
        Menu menu = menuMapper.toEntity(menuDTO);
        Dish dish = dishMapper.toEntity(dishDTO);
        menuDishRepository.deleteByMenuAndDish(menu, dish);
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public void deleteMenu(Long id) {
        menuRepository.deleteById(id);
    }

    @MyTransactional(propagation = Propagation.NESTED)
    public void deleteAllMenus() {
        menuRepository.deleteAll();
    }
}
