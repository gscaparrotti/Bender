package model;

import java.util.Objects;

/**
 * 
 *         This class is a decorator for a {@link IDish} object. You will likely
 *         use it if want to add a little variation to an existing {@link IDish}
 *         .
 *
 */
public class Variation implements IDish {

    /**
     * 
     */
    private static final long serialVersionUID = 3826515854104382947L;
    private final String variationName;
    private final double cost;
    private final IDish decoratedDish;

    /**
     * @param newVariationName
     *            The name of the variation
     * @param newCost
     *            the cost of the variation
     * @param newItem
     *            the {@link IDish} you want to decorate
     * 
     *            Creates a new {@link IDish}, which is the decoration of
     *            another {@link IDish}.
     */
    public Variation(final String newVariationName, final double newCost, final IDish newItem) {
        Objects.requireNonNull(newItem);
        Objects.requireNonNull(newVariationName);
        this.decoratedDish = newItem;
        this.variationName = newVariationName;
        this.cost = newCost;
    }

    @Override
    public String getName() {
        return decoratedDish.getName().concat(this.variationName);
    }

    @Override
    public double getPrice() {
        return decoratedDish.getPrice() + this.cost;
    }

}
