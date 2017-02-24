package viewdialogs;

import java.awt.event.MouseEvent;
import java.util.Arrays;

import controller.IMainController;
import model.Dish;
import model.IDish;

/**
 *
 */
public class MainViewJTable extends AbstractBenderJTable {

    private static final long serialVersionUID = 6204587604586213984L;

    /**
     * @param newProps 
     * @param newMainCtrl 
     */
    public MainViewJTable(final String[] newProps, final IMainController newMainCtrl) {
        super(newProps, newMainCtrl);
        if (!Arrays.asList(newProps).containsAll(Arrays.asList("Piatto", "Tavolo"))) {
            newMainCtrl.showIrreversibleErrorOnMainView("Errore nella creazione della tabella principale dei piatti.\n");
        }
    }

    @Override
    public void specificMouseListener(final int button, final int rowIndex) {
        final String name = (String) tm.getValueAt(rowIndex, this.getColumn("Piatto").getModelIndex());
        final int table = (int) this.getValueAt(rowIndex, this.getColumn("Tavolo").getModelIndex());
        double cost = 0;
        for (final IDish i : mainCtrl.getRestaurant().getOrders(table).keySet()) {
            if (i.getName().equals(name)) {
                cost = i.getPrice();
            }
        }
        final IDish item = new Dish(name, cost);
        if (button == MouseEvent.BUTTON1) {
            try {
                mainCtrl.getRestaurant().setOrderAsProcessed(table, item);
                mainCtrl.getMainViewController().updateUnprocessedOrders();
                mainCtrl.autoSave();
            } catch (final Exception exc) {
                mainCtrl.showMessageOnMainView(exc.getMessage());
            }
        }
    }
}
