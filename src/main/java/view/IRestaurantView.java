package view;

import controller.IMainController;
import controller.IMainViewController;

/**
 * 
 *         An interface which models the main view of this program.
 *
 */
public interface IRestaurantView {

    /**
     * @param controller
     *            Sets the {@link IMainController}, which will be used to load,
     *            save and get all the resources.
     * @param viewController
     *            The {@link IMainViewController} which will control this view.
     */
    void setControllers(IMainController controller, IMainViewController viewController);

    /**
     * @return true if autosave is enabled, false otherwise.
     */
    boolean isAutoSaveOption();

    /**
     * @param message
     *            The {@link String} you want to show
     * 
     *            Shows a generic message
     */
    void showApplicationMessage(String message);

    /**
     * @param message
     *            The {@link String} describing the error
     * 
     *            Shows a severe error
     */
    void showIrreversibleError(String message);

    /**
     * Deletes all the previously set unprocessed orders.
     */
    void clearUnprocessedOrders();

    /**
     * @param name
     *            The name of the dish
     * @param table
     *            the table which ordered that dish
     * @param quantity
     *            the amount of dishes that must still be served
     * 
     *            Adds a new unprocessed order.
     */
    void addUnprocessedOrder(String name, String table, int quantity);
    
    /**
     * @return true if the unprocessed orders should be filtered
     */
    boolean isFilterEnabled();
    
    /**
     * Tells the view to update the name of each table. You should call this method when you change the name of one
     * or more tables. 
     */
    void updateTableNames();

}