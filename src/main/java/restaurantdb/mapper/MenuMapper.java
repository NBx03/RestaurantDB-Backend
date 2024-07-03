package restaurantdb.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import restaurantdb.dto.MenuDTO;
import restaurantdb.model.Dish;
import restaurantdb.model.Menu;
import restaurantdb.model.MenuDish;
import restaurantdb.repository.DishRepository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MenuMapper {

    @Autowired
    private DishRepository dishRepository;

    public MenuDTO toDTO(Menu menu) {
        return new MenuDTO(
                menu.getId(),
                menu.getName(),
                menu.getDescription(),
                menu.getType() != null ? menu.getType().name() : null,
                menu.getMenuDishes() != null ?
                        menu.getMenuDishes().stream()
                                .map(menuDish -> menuDish.getDish().getId())
                                .distinct()
                                .collect(Collectors.toList()) : null
        );
    }

    public Menu toEntity(MenuDTO menuDTO) {
        Menu menu = new Menu();
        menu.setId(menuDTO.getId());
        menu.setName(menuDTO.getName());
        menu.setDescription(menuDTO.getDescription());

        if (menuDTO.getType() != null) {
            menu.setType(Menu.MenuType.valueOf(menuDTO.getType()));
        }

        if (menuDTO.getDishIds() != null) {
            List<Dish> dishes = menuDTO.getDishIds().stream()
                    .map(dishRepository::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
            menu.setMenuDishes(dishes.stream()
                    .map(dish -> {
                        MenuDish menuDish = new MenuDish();
                        menuDish.setMenu(menu);
                        menuDish.setDish(dish);
                        return menuDish;
                    })
                    .collect(Collectors.toList()));
        } else {
            menu.setMenuDishes(new ArrayList<>());
        }

        return menu;
    }
}
