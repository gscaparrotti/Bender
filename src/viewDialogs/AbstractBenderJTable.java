package viewDialogs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import controller.IMainController;

public abstract class AbstractBenderJTable extends JTable{

	private static final long serialVersionUID = 4749282523922916150L;
	private static final Object[][] INIT_DATA = new Object[][] {};
	private final String[] PROPS;
	protected DefaultTableModel tm;
	protected final IMainController mainCtrl;
	
	public AbstractBenderJTable(final String[] props, final IMainController mainCtrl) {
		tm = new DefaultTableModel(INIT_DATA, props);
		this.setModel(tm);		
		this.PROPS = props.clone(); //copia difensiva
		this.mainCtrl = mainCtrl;
		this.addDefaultMouseListener();
		this.setEnabled(false);
		this.getColumnModel().setColumnSelectionAllowed(false);
		this.getTableHeader().setReorderingAllowed(false);
		this.showHorizontalLines = true;
		this.showVerticalLines = true;
		this.rowHeight = (int) (this.rowHeight * 2.5);
	}
	
	public void addDefaultMouseListener() {
		this.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseClicked(MouseEvent e) {}
			
			@Override
			public void mousePressed(MouseEvent e) {}
			
			@Override
			public void mouseExited(MouseEvent e) {}
			
			@Override
			public void mouseEntered(MouseEvent e) {}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				int rowIndex = rowAtPoint(e.getPoint());
				if (rowIndex >= 0) {
					specificMouseListener(e.getButton(), rowIndex);
				}
			}
		});
	}
	
	public void addRow(Object[] elements) {
		tm.addRow(elements);
	}
	
	public void reset() {
		tm.setDataVector(INIT_DATA, PROPS);
	}
	
	public abstract void specificMouseListener(final int button, final int rowIndex);

}
