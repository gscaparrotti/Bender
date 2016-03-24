package viewdialogs;

import java.awt.event.MouseEvent;
import java.util.Arrays;

import model.Dish;
import model.IDish;
import controller.IMainController;

/**
 *
 */
public class DialogJTable extends AbstractBenderJTable {

    private static final long serialVersionUID = 8973323889463203812L;
    private final int tableNumber;

    /**
     * @param newProps 
     * @param newMainCtrl 
     * @param newTableNumber 
     */
    public DialogJTable(final String[] newProps, final IMainController newMainCtrl, final int newTableNumber) {
        super(newProps, newMainCtrl);
        this.tableNumber = newTableNumber;
        if (!Arrays.asList(newProps).containsAll(Arrays.asList("Piatto", "Costo"))) {
            newMainCtrl
                    .showIrreversibleErrorOnMainView("Errore nella creazione della tabella di dialogo dei piatti.\n");
        }
    }

    @Override
    protected void specificMouseListener(final int button, final int rowIndex) {
        if (rowIndex >= 0) {
            final IDish item = new Dish((String) (tm.getValueAt(rowIndex, this.getColumn("Piatto").getModelIndex())),
                    (Double) (tm.getValueAt(rowIndex, this.getColumn("Costo").getModelIndex())));
            if (button == MouseEvent.BUTTON1) {
                mainCtrl.getDialogController().commandUpdateProcessedOrders(tableNumber, item);
            } else if (button == MouseEvent.BUTTON3) {
                mainCtrl.getDialogController().commandRemove(tableNumber, item, 1);
            }
        }
    }

}
