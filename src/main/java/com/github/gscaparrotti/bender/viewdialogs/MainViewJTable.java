package com.github.gscaparrotti.bender.viewdialogs;

import com.github.gscaparrotti.bender.controller.IMainController;
import com.github.gscaparrotti.bender.controller.IMainViewController;
import java.awt.event.MouseEvent;
import java.util.Arrays;

/**
 *
 */
public class MainViewJTable extends AbstractBenderJTable {

    private static final long serialVersionUID = 6204587604586213984L;
    private final IMainViewController mainViewCtrl;

    public MainViewJTable(final String[] newProps, final IMainController newMainCtrl) {
        super(newProps, newMainCtrl);
        this.mainViewCtrl = mainCtrl.getMainViewController();
        if (!Arrays.asList(newProps).containsAll(Arrays.asList("Piatto", "Tavolo"))) {
            newMainCtrl.showIrreversibleErrorOnMainView("Errore nella creazione della tabella principale dei piatti.\n");
        }
    }

    @Override
    public void specificMouseListener(final int button, final int rowIndex) {
        if (button == MouseEvent.BUTTON1) {
            final String name = (String) tm.getValueAt(rowIndex, this.getColumn("Piatto").getModelIndex());
            final String tableString = (String) this.getValueAt(rowIndex, this.getColumn("Tavolo").getModelIndex());
            final int table = Integer.parseInt(tableString.substring(0, tableString.indexOf(' ') != -1 ? tableString.indexOf(' ') : tableString.length()));
            Arrays.stream(mainCtrl.getMenu().getDishesArray())
                .filter(dish -> dish.getName().equals(name))
                .findAny()
                .ifPresent(foundDish -> mainViewCtrl.commandUpdateUnprocessedOrder(table, foundDish));
        }
    }
}
