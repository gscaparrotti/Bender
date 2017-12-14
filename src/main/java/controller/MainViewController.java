package controller;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

import com.google.common.base.Strings;

import model.IDish;
import model.IRestaurant;
import model.Order;
import model.OrderedDish;
import model.Pair;
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
        Objects.requireNonNull(ctrl);
        this.mainController = ctrl;
    }

    @Override
    public void setView(final IRestaurantView newView) {
        this.view = newView;
    }

    @Override
    public int addTable() {
        return mainController.getRestaurant().addTable();
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
    public void updateTableNames() {
        view.updateTableNames();
    }

    @Override
    public void updateUnprocessedOrders() {
        final IRestaurant model = mainController.getRestaurant();
        final boolean filterEnabled = view.isFilterEnabled();
        view.clearUnprocessedOrders();
        final LinkedList<Order> pending = new LinkedList<>();
        synchronized (model) {
            for (int i = 1; i <= model.getTablesAmount(); i++) {
                for (final Map.Entry<IDish, Pair<Integer, Integer>> entry : model.getOrders(i).entrySet()) {
                    if (entry.getValue().getX() > entry.getValue().getY()) {
                        pending.add(new Order(i, entry.getKey(), entry.getValue()));
                    }
                }
            }
            Collections.sort(pending, new Comparator<Order>() {
                @Override
                public int compare(final Order o1, final Order o2) {
                    if (o1.getDish() instanceof OrderedDish && o2.getDish() instanceof OrderedDish) {
                        return (((OrderedDish) o1.getDish()).getTime().compareTo(((OrderedDish) o2.getDish()).getTime()));
                    } else if (o1.getDish() instanceof OrderedDish && !(o2.getDish() instanceof OrderedDish)) {
                        return -1;
                    } else if (o2.getDish() instanceof OrderedDish && !(o1.getDish() instanceof OrderedDish)){
                        return 1;
                    } else {
                        return 0;
                    }
                }
            });
            for (final Order o : pending) {
                final boolean ok = filterEnabled && o.getDish().getFilterValue() == 0 ? false : true;
                if (ok && o.getAmounts().getY() < o.getAmounts().getX()) {
                    String tableName = Strings.nullToEmpty(mainController.getRestaurant().getTableName(o.getTable()));
                    if (tableName.length() > 0) {
                        tableName = " - " + tableName;
                    }
                    view.addUnprocessedOrder(o.getDish().getName(), o.getTable() + tableName,
                            o.getAmounts().getX() - o.getAmounts().getY());
                }
            }
        }
    }

}
