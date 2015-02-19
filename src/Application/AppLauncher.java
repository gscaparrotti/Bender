package Application;

import model.Menu;
import model.Restaurant;
import controller.IMainController;
import controller.MainController;
import controller.MainViewController;
import controller.DialogController;
import view.RestaurantView;

/**
 * @author Giacomo Scaparrotti
 * 
 * This class' only purpose is launching Bender.
 * 
 */
public class AppLauncher {

	public static void main(String[] args) {
		RestaurantView v = new RestaurantView();
		IMainController ctrl = new MainController();
		ctrl.setModel(new Restaurant(), new Menu());
		ctrl.setMainViewAndControllers(v, new MainViewController(ctrl), new DialogController(ctrl));
		ctrl.loadSettings();
		v.setVisible(true);
	}

}
