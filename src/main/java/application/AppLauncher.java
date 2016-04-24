package application;

import java.awt.Color;

import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;

import model.Menu;
import model.Restaurant;
import controller.IMainController;
import controller.MainController;
import controller.MainViewController;
import controller.NetworkController;
import controller.DialogController;
import view.RestaurantView;

/**
 *
 *         This class' only purpose is launching Bender. Every concrete class
 *         used in the application is specified in the methods' arguments, so
 *         the behaviour of the program is determined in this class.
 *
 */
public final class AppLauncher {

    /**
     * Private constructor, so that you can't instantiate this class.
     */
    private AppLauncher() { };

    /**
     * @param args
     *            unused
     *
     *            Bender's main method.
     */
    public static void main(final String... args) {
        final IMainController ctrl = MainController.getInstance();
        try {
            final RestaurantView v = new RestaurantView();
            ctrl.setModel(new Restaurant(), new Menu());
            ctrl.setMainViewAndControllers(v, new MainViewController(ctrl), new DialogController(ctrl));
            final NetworkController net = new NetworkController(ctrl, 6789);
            setLookAndFeel();
            v.buildView();
            v.setVisible(true);
            net.start();
        } catch (final Exception e) {
            ctrl.showIrreversibleErrorOnMainView(e.getMessage());
        }
    }

    //CHECKSTYLE:OFF
    private static void setLookAndFeel() {
        UIManager.put("nimbusBase", new Color(255, 180, 100));
        UIManager.put("nimbusBlueGrey", new Color(255, 255, 200));
        UIManager.put("control", new Color(255, 255, 180));
        UIManager.put("nimbusFocus", new Color(255, 200, 150));
        UIManager.put("nimbusSelectionBackground", new Color(255, 200, 150));
        UIManager.put("nimbusSelectedText", new Color(0, 0, 0));
        UIManager.put("nimbusInfoBlue", new Color(255, 200, 110));
        try {
            for (final LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException
                | UnsupportedLookAndFeelException e) {
            System.out.println("Impossibile inizializzare correttamente il layout."); //NOPMD
        }
    }
    //CHECKSTYLE:ON

}
