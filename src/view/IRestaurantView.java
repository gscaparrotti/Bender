package view;

import controller.IMainController;
import controller.IMainViewController;

/**
 * @author Giacomo Scaparrotti
 * 
 * An interface which models the main view of this program.
 *
 */
public interface IRestaurantView {

	/**
	 * @param controller Sets the {@link IMainController}, which will be used to load, save and get all the resources.
	 * @param viewController
	 */
	public void setControllers(IMainController controller,
			IMainViewController viewController);

	/**
	 * @return true if autosave is enabled, false otherwise.
	 */
	public boolean getAutoSaveOption();

	/**
	 * @param message The {@link String} you want to show
	 * 
	 * Shows a generic message
	 */
	public void showApplicationMessage(String message);

	/**
	 * @param message The {@link String} describing the error
	 * 
	 * Shows a severe error
	 */
	public void showIrreversibleError(String message);

}