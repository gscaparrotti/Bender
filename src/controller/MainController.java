package controller;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

import view.RestaurantView;
import model.Dish;
import model.IMenu;
import model.IRestaurant;

public class MainController implements IMainController {
	
	public static final String DIR = System.getProperty("user.dir"); //directory corrente
	public static final String SEPARATOR = System.getProperty("file.separator");
	private static final String DEFAULT_RESTAURANT_FILE = "res" + SEPARATOR + "BenderData.dat";
	private static final String DEFAULT_MENU_FILE = "res" + SEPARATOR + "menu.txt";
	private static final String[] PATHS = new String[] {DIR + SEPARATOR + DEFAULT_RESTAURANT_FILE, DIR + SEPARATOR + DEFAULT_MENU_FILE};
	private static final String SETTINGS_FILE = "settings.txt";
	private static final String SETTINGS_PATH = DIR + SEPARATOR + SETTINGS_FILE;
	private IRestaurant model;
	private IMenu menu;
	private RestaurantView view;
	private IDialogController dc;
	
	public MainController() {
	}
	
	@Override
	public void setModel(IRestaurant model, IMenu menu) {
		this.menu = menu;
		this.model = model;
		loadMenu(PATHS[1]);
	}
	
	@Override
	public void setMainViewAndControllers(RestaurantView view, IMainViewController viewCtrl, IDialogController dialogCtrl) {
		this.dc = dialogCtrl;
		this.view = view;
		view.setControllers(this, viewCtrl);
	}
	
	@Override
	public IRestaurant getRestaurant() {
		return this.model;
	}
	
	@Override
	public IMenu getMenu()  {
		return this.menu;
	}

	private void loadMenu(String path) {
		try {
			BufferedReader r = new BufferedReader(new FileReader(path));
			while(r.ready()) {
				String line = r.readLine();
				if (line != null) {
					String[] dishStrings = line.split(" -- ");
					if(dishStrings.length == Dish.Fields) {
						menu.addItems(new Dish(dishStrings[0], Double.parseDouble(dishStrings[dishStrings.length - 1])));
					}
				}
			}
			r.close();
		} catch (Exception e) {
			showMessageOnMainView(e.getMessage());
		}
	}
	
	@Override
	public int commandLoad() {
		try {
			final ObjectInput ois = new ObjectInputStream(new FileInputStream(PATHS[0]));
			this.model = (IRestaurant) ois.readObject();
			ois.close();
			dc.updateReferences();
			return model.getTablesAmount();
		} catch (Exception e) {
			showMessageOnMainView(e.getMessage());
			return 0;
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
	
	public void autoSave() {
		if (view != null && view.getAutoSaveOption()) {
			commandSave();
		}		
	}
	
	public void loadSettings() {
		try {
			BufferedReader r = new BufferedReader(new FileReader(SETTINGS_PATH));
			int count = 0;
			while(r.ready() && count < PATHS.length) {
				PATHS[count] = r.readLine();
				count++;
			}
			r.close();
		} catch (Exception e) {
			showMessageOnMainView(e.getMessage() + " - Verranno utilizzati i percorsi di default");
		}
	}
	
	public void showMessageOnMainView(String message) {
		if (view != null) {
			view.showApplicationMessage(message);
		}
	}
	
	public void showIrreversibleErrorOnMainView(String message) {
		if (view != null) {
			view.showIrreversibleError(message);
		} else {
			System.out.println(message);
		}
	}
 	
	public void checkNull(Object... obj) {
		for(Object o : obj) {
			if(o==null) {
				throw new NullPointerException();
			}
		}
	}

	@Override
	public IDialogController getDialogController() {
		return this.dc;
	}

}
