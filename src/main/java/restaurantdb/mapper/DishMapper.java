package restaurantdb.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import restaurantdb.dto.DishDTO;
import restaurantdb.model.*;
import restaurantdb.repository.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class DishMapper {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private RestaurantOrderRepository restaurantOrderRepository;

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    public DishDTO toDTO(Dish dish) {
        return new DishDTO(
                dish.getId(),
                dish.getName(),
                dish.getDescription(),
                dish.getPrice(),
                dish.isAvailability(),
                dish.getCategory() != null ? dish.getCategory().name() : null,
                dish.getMenuDishes() != null ?
                        dish.getMenuDishes().stream()
                                .map(menuDish -> menuDish.getMenu().getId())
                                .distinct()
                                .collect(Collectors.toList()) : null,
                dish.getOrderDishBills() != null ?
                        dish.getOrderDishBills().stream()
                                .map(orderDishBill -> orderDishBill.getOrder().getId())
                                .distinct()
                                .collect(Collectors.toList()) : null,
                dish.getOrderDishBills() != null ?
                        dish.getOrderDishBills().stream()
                                .map(orderDishBill -> (orderDishBill.getBill() != null ? orderDishBill.getBill().getId() : null))
                                .distinct()
                                .collect(Collectors.toList()) : null,
                dish.getRecipes() != null ?
                        dish.getRecipes().stream()
                                .map(Recipe::getId)
                                .collect(Collectors.toList()) : null
        );
    }

    public Dish toEntity(DishDTO dishDTO) {
        Dish dish = new Dish();
        dish.setId(dishDTO.getId());
        dish.setName(dishDTO.getName());
        dish.setDescription(dishDTO.getDescription());
        dish.setPrice(dishDTO.getPrice());
        dish.setAvailability(dishDTO.isAvailability());

        if (dishDTO.getCategory() != null) {
            dish.setCategory(Dish.Category.valueOf(dishDTO.getCategory()));
        }

        if (dishDTO.getMenuIds() != null) {
            List<MenuDish> menuDishes = dishDTO.getMenuIds().stream()
                    .map(menuId -> menuRepository.findById(menuId)
                            .map(menu -> new MenuDish(null, menu, dish))
                            .orElse(null))
                    .collect(Collectors.toList());
            dish.setMenuDishes(menuDishes);
        } else {
            dish.setMenuDishes(new ArrayList<>());
        }

        if (dishDTO.getOrderIds() != null && dishDTO.getBillIds() != null) {
            List<OrderDishBill> orderDishBills = new ArrayList<>();
            for (int i = 0; i < dishDTO.getOrderIds().size(); i++) {
                Optional<RestaurantOrder> order = restaurantOrderRepository.findById(dishDTO.getOrderIds().get(i));
                Optional<Bill> bill = billRepository.findById(dishDTO.getBillIds().get(i));
                if (order.isPresent() && bill.isPresent()) {
                    OrderDishBill orderDishBill = new OrderDishBill(null, order.get(), dish, bill.get(), 1);
                    orderDishBills.add(orderDishBill);
                }
            }
            dish.setOrderDishBills(orderDishBills);
        } else {
            dish.setOrderDishBills(new ArrayList<>());
        }

        if (dishDTO.getRecipeIds() != null) {
            List<Recipe> recipes = dishDTO.getRecipeIds().stream()
                    .map(recipeId -> recipeRepository.findById(recipeId).orElse(null))
                    .collect(Collectors.toList());
            dish.setRecipes(recipes);
        } else {
            dish.setRecipes(new ArrayList<>());
        }

        return dish;
    }
}
