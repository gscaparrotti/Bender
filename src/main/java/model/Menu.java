package model;

import java.util.Arrays;
import java.util.LinkedList;

import utilities.CheckNull;

/**
 *
 */
public class Menu extends LinkedList<IDish> implements IMenu {

    /**
     * 
     */
    private static final long serialVersionUID = -1111129590390041868L;

    @Override
    public void addItems(final IDish... items) {
        for (final IDish i : items) {
            CheckNull.checkNull(i);
        }
        this.addAll(Arrays.asList(items));
    }

    @Override
    public IDish[] getDishesArray() {
        this.sort((t1, t2) -> t1.getName().compareTo(t2.getName()));
        return this.toArray(new Dish[this.size()]);
    }

}
