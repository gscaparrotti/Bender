package viewDialogs;

import java.awt.event.MouseEvent;
import java.util.Arrays;

import model.Dish;
import model.IDish;
import controller.IMainController;

public class DialogJTable extends AbstractBenderJTable{

	private static final long serialVersionUID = 8973323889463203812L;
	private int tableNumber;

	public DialogJTable(final String[] props, final IMainController mainCtrl, final int tableNumber) {
		super(props, mainCtrl);
		this.tableNumber = tableNumber;
		if(!Arrays.asList(props).containsAll(Arrays.asList("Piatto", "Costo"))) {
			mainCtrl.showIrreversibleErrorOnMainView("Errore nella creazione della tabella di dialogo dei piatti.\n");
		}
	}

	@Override
	public void specificMouseListener(final int button, final int rowIndex) {
		if (rowIndex >= 0) {
			IDish item = new Dish((String) (tm.getValueAt(rowIndex, this.getColumn("Piatto").getModelIndex())),
					             (Double) (tm.getValueAt(rowIndex, this.getColumn("Costo").getModelIndex())));
			if (button == MouseEvent.BUTTON1) {
				mainCtrl.getDialogController().commandUpdateProcessedOrders(tableNumber, item);
			} else if (button == MouseEvent.BUTTON3) {
				mainCtrl.getDialogController().commandRemove(tableNumber, item, 1);
			}
		}	
	}

}
