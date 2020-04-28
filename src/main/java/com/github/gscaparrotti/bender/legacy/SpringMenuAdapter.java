package com.github.gscaparrotti.bender.legacy;

import com.github.gscaparrotti.bender.entities.Dish;
import com.github.gscaparrotti.bender.entities.Drink;
import com.github.gscaparrotti.bender.entities.Food;
import com.github.gscaparrotti.bender.springControllers.MenuController;
import com.github.gscaparrotti.bendermodel.model.IDish;
import com.github.gscaparrotti.bendermodel.model.IMenu;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

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
        return ifBodyNotNull(getController().getMenu(), newMenu -> {
            final Iterator<Dish> newDishIterator = newMenu.iterator();
            final IDish[] oldMenu = new IDish[newMenu.size()];
            for (int i = 0; i < oldMenu.length && newDishIterator.hasNext(); i++) {
                final Dish newDish = newDishIterator.next();
                oldMenu[i] = new com.github.gscaparrotti.bendermodel.model.Dish(newDish.getName(), newDish.getPrice(), newDish instanceof Drink ? 0 : 1);
            }
            Arrays.sort(oldMenu, Comparator.comparing(IDish::getName));
            return oldMenu;
        }, () -> new IDish[0]);
    }

    private static MenuController getController() {
        return ctrl(MenuController.class);
    }
}
