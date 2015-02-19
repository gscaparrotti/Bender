package view;

import controller.IDialogController;

public interface ITableDialog {

	public void addOrderToView(String name, double price, int amount,
			int processed);

	public void billUpdate(double bill, double effectiveBill);

	public void clearTab();

	public void showError(Exception e);

	public void clearErrors();
	
	public void setControllerAndBuildView(IDialogController dialogCtrl);

}