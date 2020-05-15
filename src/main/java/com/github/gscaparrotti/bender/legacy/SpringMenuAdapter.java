package com.github.gscaparrotti.bender.legacy;

import com.github.gscaparrotti.bender.entities.Dish;
import com.github.gscaparrotti.bender.entities.Drink;
import com.github.gscaparrotti.bender.entities.Food;
import com.github.gscaparrotti.bender.springControllers.MenuController;
import com.github.gscaparrotti.bendermodel.model.IDish;
import com.github.gscaparrotti.bendermodel.model.IMenu;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Collectors;

import static com.github.gscaparrotti.bender.legacy.LegacyHelper.ctrl;
import static com.github.gscaparrotti.bender.legacy.LegacyHelper.ifBodyNotNull;

public class SpringMenuAdapter implements IMenu {
    @Override
    public void addItems(IDish... items) {
        for (final IDish oldDish : items) {
            final Dish newDish;
            newDish = oldDish.getFilterValue() == 0 ? new Drink() : new Food();
            newDish.setName(oldDish.getName());
            newDish.setPrice(oldDish.getPrice());
            getController().addToMenu(newDish);
        }
    }

    @Override
    public IDish[] getDishesArray() {
        return ifBodyNotNull(getController().getMenu(), menu -> {
            final Set<com.github.gscaparrotti.bendermodel.model.Dish> oldMenuSet = menu.stream()
                .map(dish -> new com.github.gscaparrotti.bendermodel.model.Dish(dish.getName(), dish.getPrice(), dish instanceof Drink ? 0 : 1))
                .collect(Collectors.toUnmodifiableSet());
            final IDish[] oldMenu = oldMenuSet.toArray(new IDish[0]);
            Arrays.sort(oldMenu, Comparator.comparing(IDish::getName));
            return oldMenu;
        }, () -> new IDish[0]);
    }

    private static MenuController getController() {
        return ctrl(MenuController.class);
    }
}
