package model;

import java.util.Date;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * A IDish which keeps infos about the moment when the instance was created.
 *
 */
@SuppressFBWarnings(value = "EQ_DOESNT_OVERRIDE_EQUALS", justification = "The added fields does not involve equals()")
public class OrderedDish extends Dish {

    private static final long serialVersionUID = 1637868088291903941L;
    private final Date time;

    /**
     * Creates a new OrderedDish given a name and a price and 
     * keeping a reference about the moment when the object was instantiated.
     * 
     * @param newName the name of the dish
     * @param newPrice the price of the dish
     */
    public OrderedDish(final String newName, final double newPrice) {
        super(newName, newPrice);
        time = new Date();
    }

    /**
     * Creates a new OrderedDish given a name and a price and 
     * keeping a reference about the moment when the object was instantiated.
     * 
     * @param dish an existing dish which will be used to get a name and a price
     */
    public OrderedDish(final IDish dish) {
        super(dish.getName(), dish.getPrice());
        time = new Date();
    }

    /**
     * Creates a new OrderedDish setting its creation time to a moment provided by another OrderedDish.
     * 
     * @param dish an existing dish which will be used to get a moment
     * @param name the name of the dish
     * @param price the price of the dish
     */
    public OrderedDish(final String name, final double price, final OrderedDish dish) {
        super(name, price);
        time = dish.getTime();
    }

    /**
     * @return the moment when this object was created
     */
    public Date getTime() {
        return new Date(time.getTime());
    }
}
