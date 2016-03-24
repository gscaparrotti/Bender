package controller;

import model.IMenu;
import model.IRestaurant;
import view.IRestaurantView;
import view.ITableDialog;

/**
 * 
 *         This is the Main Controller of the entire application. Its presence
 *         is mandatory. Its purpose is providing all the needed objects, such
 *         as the menu or the instance of the Restaurant class, and managing the
 *         file I/O, which is to say, saving and loading the application's data
 *         from the file system. It also dispatches the other controllers to the
 *         respective views, calling the relative methods or providing an ad-hoc
 *         method (see the methods' doc for further details).
 *
 */
public interface IMainController {

    /**
     * @param model
     *            an {@link IRestaurant} instance
     * @param menu
     *            an {@link IMenu} instance
     * 
     *            Set the model classes which will be used during the execution
     *            of the program. It is mandatory to call this method every time
     *            you create a new instance of this class.
     */
    void setModel(IRestaurant model, IMenu menu);

    /**
     * @param view
     *            The {@link IRestaurantView} instance used in the program.
     * @param viewCtrl
     *            Your {@link IMainViewController}, which controls the view.
     * @param dialogCtrl
     *            Your {@link IDialogController}, which will control the
     *            {@link ITableDialog} instances.
     * 
     *            Sets the main view and the related controllers.
     */
    void setMainViewAndControllers(IRestaurantView view, IMainViewController viewCtrl, IDialogController dialogCtrl);

    /**
     * @return the prevoiously set {@link IDialogController} instance.
     * 
     *         Provides the {@link IDialogController} which will be used when
     *         creating a new {@link ITableDialog}. Before calling this method,
     *         you must have called the
     *         {@link #setMainViewAndControllers(IRestaurantView, IMainViewController, IDialogController)}
     *         method at least once, otherwise the behaviour of this method is
     *         undefined.
     */
    IDialogController getDialogController();

    /**
     * @return the prevoiously set {@link IMainViewController} instance.
     * 
     *         Provides the {@link IMainViewController} used by the
     *         {@link IRestaurantView}. Before calling this method, you must
     *         have called the
     *         {@link #setMainViewAndControllers(IRestaurantView, IMainViewController, IDialogController)}
     *         method at least once, otherwise the behaviour of this method is
     *         undefined.
     */
    IMainViewController getMainViewController();

    /**
     * @return The previously set {@link IRestaurant} instance
     */
    IRestaurant getRestaurant();

    /**
     * @return The previously set {@link IMenu} instance
     */
    IMenu getMenu();

    /**
     * @return the amount of loaded tables, or -1 if the loading failed.
     * 
     *         Loads a previously saved status of the program. If the loading
     *         fails, -1 will be returned, otherwise, the method will return the
     *         amount of tables in the loaded file.
     */
    int commandLoad();

    /**
     * Saves the entire status of the program. You will likely call this method
     * at the exit of the program.
     */
    void commandSave();

    /**
     * Checks if the autosaving option is selected, and if so, saves the status
     * of the program.
     */
    void autoSave();

    /**
     * @param message
     *            a {@link String} containing the message
     * 
     *            Shows a generic message on the main view. You can use this
     *            method to show any kind of information, such as notifications
     *            or soft errors. If you want to show a severe error, use the
     *            {@link #showIrreversibleErrorOnMainView(String)} method.
     */
    void showMessageOnMainView(String message);

    /**
     * @param message
     *            a {@link String} containing the error description
     * 
     *            Shows a severe error on the main view.
     */
    void showIrreversibleErrorOnMainView(String message);

}