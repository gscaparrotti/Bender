package com.github.gscaparrotti.bender;

import com.github.gscaparrotti.bender.controller.DialogController;
import com.github.gscaparrotti.bender.controller.IMainController;
import com.github.gscaparrotti.bender.controller.MainController;
import com.github.gscaparrotti.bender.controller.MainViewController;
import com.github.gscaparrotti.bender.controller.NetworkController;
import com.github.gscaparrotti.bender.legacy.SpringMenuAdapter;
import com.github.gscaparrotti.bender.legacy.SpringRestaurantAdapter;
import com.github.gscaparrotti.bender.view.RestaurantView;
import java.awt.Color;
import java.util.Arrays;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 *
 *         This class' only purpose is launching Bender. Every concrete class
 *         used in the application is specified in the methods' arguments, so
 *         the behaviour of the program is determined in this class.
 *
 */
@SpringBootApplication
public class AppLauncher {

    public static void main(final String[] args) {
        final Logger logger = LoggerFactory.getLogger(AppLauncher.class);
        final boolean useOldNetworking = Arrays.asList(args).contains("-legacy");
        if (useOldNetworking) {
            logger.info("Using legacy network controller...");
            new SpringApplicationBuilder(AppLauncher.class).headless(false).web(WebApplicationType.NONE).run(args);
        } else {
            if (args.length > 0) {
                logger.info("Unknown CLI parameter");
            }
            logger.info("Using Spring networking facilities...");
            new SpringApplicationBuilder(AppLauncher.class).headless(false).run(args);
        }
        final IMainController ctrl = MainController.getInstance();
        SwingUtilities.invokeLater(() -> {
            try {
                final RestaurantView v = new RestaurantView();
                ctrl.setModel(new SpringRestaurantAdapter(), new SpringMenuAdapter());
                ctrl.setMainViewAndControllers(v, new MainViewController(ctrl), new DialogController(ctrl));
                setLookAndFeel();
                v.buildView();
                v.refreshTables(ctrl.commandLoad());
                ctrl.getMainViewController().updateUnprocessedOrdersInView();
                v.setVisible(true);
                if (useOldNetworking) {
                    logger.info("Starting legacy network controller...");
                    final NetworkController net = new NetworkController(ctrl, 6789);
                    net.start();
                }
            } catch (final Exception e) {
                ctrl.showIrreversibleErrorOnMainView(e.getMessage());
            }
        });
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
