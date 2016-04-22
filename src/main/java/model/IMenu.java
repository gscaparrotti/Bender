package model;

import java.io.Serializable;

/**
 * 
 * An interface with the necessary methods to model a restaurant menu.
 *
 */
public interface IMenu extends Serializable {

    /**
     * @param items
     *            A variable number of {@link IDish} to add
     * 
     *            Adds all the given {@link IDish} to this menu.
     */
    void addItems(IDish... items);

    /**
     * @return An array containing all the {@link IDish} in this menu
     */
    IDish[] getDishesArray();

}
