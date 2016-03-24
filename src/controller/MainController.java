package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import view.IRestaurantView;
import model.Dish;
import model.IMenu;
import model.IRestaurant;
import utilities.CheckNull;

/**
 *
 */
public class MainController implements IMainController {

    /**
     * The current directory.
     */
    public static final String DIR = System.getProperty("user.dir");
    /**
     * System's default file separator.
     */
    public static final String SEPARATOR = System.getProperty("file.separator");
    private static final String DEFAULT_RESTAURANT_FILE = "BenderData.dat";
    private static final String DEFAULT_MENU_FILE = "data" + SEPARATOR + "menu.txt";
    private static final String[] PATHS = new String[] { DIR + SEPARATOR + DEFAULT_RESTAURANT_FILE,
            DIR + SEPARATOR + DEFAULT_MENU_FILE };
    private IRestaurant model;
    private IMenu menu;
    private IRestaurantView view;
    private IDialogController dc;
    private IMainViewController mvc;

    /**
     * Creates a new empty {@link MainController}. Before you can use it, you
     * have to set the model and the other controllers, using the
     * {@link #setModel(IRestaurant, IMenu)} and
     * {@link #setMainViewAndControllers(IRestaurantView, IMainViewController, IDialogController)}
     * methods.
     */
    public MainController() { } //NOPMD

    @Override
    public void setModel(final IRestaurant newModel, final IMenu newMenu) {
        CheckNull.checkNull(newModel, newMenu);
        this.menu = newMenu;
        this.model = newModel;
        loadMenu();
    }

    @Override
    public void setMainViewAndControllers(final IRestaurantView newView, final IMainViewController viewCtrl,
            final IDialogController dialogCtrl) {
        CheckNull.checkNull(newView, viewCtrl, dialogCtrl);
        this.dc = dialogCtrl;
        this.mvc = viewCtrl;
        this.view = newView;
        mvc.setView(newView);
        newView.setControllers(this, viewCtrl);
    }

    @Override
    public IRestaurant getRestaurant() {
        return this.model;
    }

    @Override
    public IMenu getMenu() {
        return this.menu;
    }

    private void loadMenu() {
        try {
            InputStream in = MainController.class.getResourceAsStream("/menu.txt");
            if (new File(PATHS[1]).exists()) {
                in = new FileInputStream(PATHS[1]);
            }
            final BufferedReader r = new BufferedReader(new InputStreamReader(in, "UTF8"));
            while (r.ready()) {
                final String line = r.readLine();
                if (line != null) {
                    final String[] dishStrings = line.split(" -- ");
                    if (dishStrings.length == Dish.FIELDS) {
                        menu.addItems(
                                new Dish(dishStrings[0], Double.parseDouble(dishStrings[dishStrings.length - 1])));
                    }
                }
            }
            r.close();
        } catch (Exception e) {
            showIrreversibleErrorOnMainView("Impossibile caricare il menu " + e.getMessage());
        }
    }

    @Override
    public int commandLoad() {
        try {
            final ObjectInput ois = new ObjectInputStream(new FileInputStream(PATHS[0]));
            this.model = (IRestaurant) ois.readObject();
            ois.close();
            return model.getTablesAmount();
        } catch (Exception e) {
            showMessageOnMainView(e.getMessage());
            return -1;
        }
    }

    @Override
    public void commandSave() {
        try {
            final ObjectOutput oos = new ObjectOutputStream(new FileOutputStream(PATHS[0]));
            oos.writeObject(this.model);
            oos.close();
        } catch (Exception e) {
            showMessageOnMainView(e.getMessage());
        }
    }

    @Override
    public void autoSave() {
        if (view != null && view.isAutoSaveOption()) {
            commandSave();
        }
    }

    @Override
    public void showMessageOnMainView(final String message) {
        if (view == null || message == null) {
            System.out.println(message);
        } else {
            view.showApplicationMessage(message);
        }
    }

    @Override
    public void showIrreversibleErrorOnMainView(final String message) {
        if (view == null || message == null) {
            System.out.println(message);
            exit();
        } else {
            view.showIrreversibleError(message);
        }
    }

    @Override
    public IDialogController getDialogController() {
        return this.dc;
    }

    @Override
    public IMainViewController getMainViewController() {
        return this.mvc;
    }

    private void exit() {
        System.exit(0);
    }

}
