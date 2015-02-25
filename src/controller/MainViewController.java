package controller;

import benderUtilities.CheckNull;

public class MainViewController implements IMainViewController {
	
	private IMainController mainController;
	
	public MainViewController(IMainController ctrl) {
		CheckNull.checkNull(ctrl);
		this.mainController = ctrl;
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

}
