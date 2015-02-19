package controller;

import model.IMenu;
import model.IRestaurant;
import view.RestaurantView;

public interface IMainController {

	public void setModel(IRestaurant model, IMenu menu);

	public void setMainViewAndControllers(RestaurantView view, IMainViewController viewCtrl, IDialogController dialogCtrl);
	
	public IDialogController getDialogController();

	public IRestaurant getRestaurant();

	public IMenu getMenu();

	public int commandLoad();

	public void commandSave();
	
	public void autoSave();
	
	public void loadSettings();
	
	public void showMessageOnMainView(String message);
	
	public void showIrreversibleErrorOnMainView(String message);

}