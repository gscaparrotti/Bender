package controller;

import java.util.Iterator;
import java.util.Map.Entry;

import model.IDish;
import model.IRestaurant;
import model.Pair;
import utilities.CheckNull;
import view.IRestaurantView;

/**
 *
 */
public class MainViewController implements IMainViewController {

    private final IMainController mainController;
    private IRestaurantView view;

    /**
     * @param ctrl
     *            The used {@link IMainController}
     * 
     *            Creates a new {@link MainViewController}, which will affect
     *            the model provided by the selected {@link IMainController}.
     */
    public MainViewController(final IMainController ctrl) {
        CheckNull.checkNull(ctrl);
        this.mainController = ctrl;
    }

    @Override
    public void setView(final IRestaurantView newView) {
        this.view = newView;
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

    @Override
    public void updateUnprocessedOrders() {
        final IRestaurant model = mainController.getRestaurant();
        final int tablesAmount = model.getTablesAmount();
        view.clearUnprocessedOrders();
        for (int i = 1; i <= tablesAmount; i++) {
            final Iterator<Entry<IDish, Pair<Integer, Integer>>> iterator = model.getOrders(i).entrySet().iterator();
            while (iterator.hasNext()) {
                final Entry<IDish, Pair<Integer, Integer>> entry = iterator.next();
                if (entry.getValue().getY() < entry.getValue().getX()) {
                    view.addUnprocessedOrder(entry.getKey().getName(), i,
                            entry.getValue().getX() - entry.getValue().getY());
                }
            }
        }
    }

}
