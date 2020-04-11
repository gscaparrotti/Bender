package viewdialogs;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import controller.IMainController;

/**
 *
 * A generic table with facilities for adding multiple rows and mouse interaction.
 */
public abstract class AbstractBenderJTable extends JTable {

    private static final long serialVersionUID = 4749282523922916150L;
    private static final Object[][] INIT_DATA = new Object[][] {};
    private final String[] props;
    //CHECKSTYLE:OFF
    protected final DefaultTableModel tm;
    protected final IMainController mainCtrl;
    //CHECKSTYLE:ON

    /**
     * @param newProps the columns names
     * @param newMainCtrl the main controller
     */
    public AbstractBenderJTable(final String[] newProps, final IMainController newMainCtrl) {
        super();
        tm = new DefaultTableModel(INIT_DATA, newProps);
        this.setModel(tm);
        this.props = Arrays.copyOf(newProps, newProps.length); // copia difensiva
        this.mainCtrl = newMainCtrl;
        this.addDefaultMouseListener();
        this.setEnabled(false);
        this.getColumnModel().setColumnSelectionAllowed(false);
        this.getTableHeader().setReorderingAllowed(false);
        this.showHorizontalLines = true;
        this.showVerticalLines = true;
        //CHECKSTYLE:OFF
        this.rowHeight = (int) (this.rowHeight * 2.5);
        //CHECKSTYLE:ON
    }

    private void addDefaultMouseListener() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(final MouseEvent e) {
                final int rowIndex = rowAtPoint(e.getPoint());
                if (rowIndex >= 0) {
                    specificMouseListener(e.getButton(), rowIndex);
                }
            }
        });
    }

    /**
     * @param elements the elements to be added to this table
     */
    public void addRow(final Object... elements) {
        tm.addRow(elements);          
    }

    /**
     * Deletes all the elements from this table.
     */
    public void reset() {
        tm.setDataVector(INIT_DATA, props);       
    }

    /**
     * Override this method to specify the behavior of a mouse click in your specific table.
     * 
     * @param button the pressed mouse button
     * @param rowIndex the row which has been clicked
     */
    protected abstract void specificMouseListener(int button, int rowIndex);

}
