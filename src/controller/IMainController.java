package controller;

import model.IMenu;
import model.IRestaurant;
import view.RestaurantView;

public interface IMainController {

	public void setModel(IRestaurant model, IMenu menu);

	public void setControllers(RestaurantView view, IMainViewController viewCtrl, Class<DialogController> dialogCtrl);

	public IRestaurant getRestaurant();

	public IMenu getMenu();

	public int commandLoad();

	public void commandSave();
	
	public void autoSave();
	
	public void loadSettings();
	
	public void showMessageOnMainView(String message);
	
	public void showIrreversibleErrorOnMainView(String message);

}