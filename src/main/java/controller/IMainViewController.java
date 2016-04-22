package controller;

import view.IRestaurantView;

/**
 * 
 *         This is the {@link IRestaurantView} controller. Since that class only
 *         deal with very basic informations, this class is very simple.
 *
 */
public interface IMainViewController {

    /**
     * @param view
     * 
     *            Set the provided view as the affected view.
     */
    void setView(IRestaurantView view);

    /**
     * @return the ID of the new table
     * 
     *         Adds a new table to the model.
     */
    int addTable();

    /**
     * @return true if the removal succeded, false otherwise
     * 
     *         Removes the last added table from the model.
     */
    boolean removeTable();

    /**
     * Updateds the unprocessed orders.
     */
    void updateUnprocessedOrders();

}