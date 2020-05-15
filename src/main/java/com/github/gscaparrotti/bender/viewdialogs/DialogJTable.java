package com.github.gscaparrotti.bender.viewdialogs;

import com.github.gscaparrotti.bender.controller.IMainController;
import com.github.gscaparrotti.bendermodel.model.Dish;
import com.github.gscaparrotti.bendermodel.model.IDish;
import java.awt.event.MouseEvent;
import java.util.Arrays;

/**
 *
 */
public class DialogJTable extends AbstractBenderJTable {

    private static final long serialVersionUID = 8973323889463203812L;
    private final int tableNumber;

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
                    (Double) (tm.getValueAt(rowIndex, this.getColumn("Costo").getModelIndex())), 0);
            if (button == MouseEvent.BUTTON1) {
                mainCtrl.getDialogController().commandUpdateProcessedOrders(tableNumber, item);
            } else if (button == MouseEvent.BUTTON3) {
                mainCtrl.getDialogController().commandRemove(tableNumber, item, 1);
            }
        }
    }

}
