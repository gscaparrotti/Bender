package model;

import java.io.Serializable;

/**
 * 
 *         An interface which models the basic method for managing a dish.
 *
 */
public interface IDish extends Serializable {

    /**
     * @return The name of this dish.
     */
    String getName();

    /**
     * @return The price of this dish.
     */
    double getPrice();

}
