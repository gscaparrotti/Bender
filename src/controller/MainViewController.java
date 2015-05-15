package controller;

import java.util.Iterator;
import java.util.Map.Entry;

import model.IDish;
import model.IRestaurant;
import model.Pair;
import view.IRestaurantView;
import benderUtilities.CheckNull;

/**
 * @author Giacomo Scaparrotti
 *
 */
public class MainViewController implements IMainViewController {
	
	private IMainController mainController;
	private IRestaurantView view;
	
	/**
	 * @param ctrl The used {@link IMainController}
	 * 
	 * Creates a new {@link MainViewController}, which will affect the model
	 * provided by the selected {@link IMainController}.
	 */
	public MainViewController(IMainController ctrl) {
		CheckNull.checkNull(ctrl);
		this.mainController = ctrl;
	}
	
	@Override
	public void setView(IRestaurantView view) {
		this.view = view;		
	}
	
	@Override
	public int addTable() {
		mainController.getRestaurant().addTable();
		return mainController.getRestaurant().getTablesAmount();
	}
	
	@Override
	public boolean removeTable() {
		try {
			mainController.getRestaurant().removeTable();
			mainController.autoSave();
			return true;
		} catch (Exception e) {
			mainController.showMessageOnMainView("Impossibile rimuovere il tavolo: " + e.getMessage());
			return false;
		}
	}
	
	public void updateUnprocessedOrders() {
		IRestaurant model = mainController.getRestaurant();
		int tablesAmount = model.getTablesAmount();
		view.clearUnprocessedOrders();
		for(int i=1; i<=tablesAmount; i++) {
			Iterator<Entry<IDish, Pair<Integer, Integer>>> iterator = model.getOrders(i).entrySet().iterator();
			while (iterator.hasNext()) {
				Entry<IDish, Pair<Integer, Integer>> entry = iterator.next();
				if(entry.getValue().getY() < entry.getValue().getX()) {
					view.addUnprocessedOrder(entry.getKey().getName(), i, entry.getValue().getX() - entry.getValue().getY());
				}
			}
		}
	}

}
