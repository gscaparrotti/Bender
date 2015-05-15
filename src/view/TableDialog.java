package view;

import javax.swing.JDialog;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import java.awt.Font;

import javax.swing.JComboBox;

import model.IDish;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JSpinner;

import viewDialogs.DialogJTable;
import controller.IDialogController;
import controller.IMainController;
import net.miginfocom.swing.MigLayout;

import java.awt.BorderLayout;

/**
 * @author Giacomo Scaparrotti
 *
 */
public class TableDialog extends JDialog implements ITableDialog {
	
	private static final long serialVersionUID = -2269793459529910803L;

	private static final String[] PROPS = new String[] {"Piatto", "Costo", "Quantità", "Costo totale", "Evaso"};
	private static final String BILL_TEXT = "Conto Totale: ";
	private static final String EFFECTIVE_BILL_TEXT = "Conto Effettivo: ";
	private static final String CURRENCY_SYMBOL = " €";
	private static final String STRING_SEPARATOR = " - ";
	private JLabel errorLabel = new JLabel();
	private JLabel lblContoTotale = new JLabel(BILL_TEXT);
	private DialogJTable orders;
	private int tableNumber;
	private IDialogController ctrl;
	private IMainController mainCtrl;


	/**
	 * @param ctrl The {@link IMainController} which will provide all the needed resources
	 * @param tableNumber the ID of the table represented by this dialog
	 * 
	 * Creates a new table dialog.
	 */
	public TableDialog(IMainController ctrl, int tableNumber) {
		super();
		this.setModalityType(ModalityType.APPLICATION_MODAL);
		this.tableNumber = tableNumber;
		this.mainCtrl = ctrl;
		this.orders = new DialogJTable(PROPS, mainCtrl, tableNumber);
	}
	
	public void setControllerAndBuildView(IDialogController dialogCtrl) {
		this.ctrl = dialogCtrl;
		buildView();
	}
	
	private void buildView() {
		this.setTitle("Gestione del tavolo n° " + tableNumber);
		this.setResizable(true);
		setBounds(100, 100, 650, 373);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		JLabel lblGestioneDegliOrdini = new JLabel("GESTIONE DEGLI ORDINI PER IL TAVOLO N° ".concat(Integer.toString(tableNumber)));
		lblGestioneDegliOrdini.setFont(lblGestioneDegliOrdini.getFont().deriveFont(Font.BOLD, 16));
		GridBagConstraints gbc_lblGestioneDegliOrdini = new GridBagConstraints();
		gbc_lblGestioneDegliOrdini.insets = new Insets(0, 0, 5, 0);
		gbc_lblGestioneDegliOrdini.gridx = 0;
		gbc_lblGestioneDegliOrdini.gridy = 0;
		getContentPane().add(lblGestioneDegliOrdini, gbc_lblGestioneDegliOrdini);
		
		JLabel lblSelezionareIlPiatto = new JLabel("Selezionare il piatto da aggiungere, quindi premere AGGIUNGI");
		GridBagConstraints gbc_lblSelezionareIlPiatto = new GridBagConstraints();
		gbc_lblSelezionareIlPiatto.insets = new Insets(0, 0, 5, 0);
		gbc_lblSelezionareIlPiatto.gridx = 0;
		gbc_lblSelezionareIlPiatto.gridy = 1;
		getContentPane().add(lblSelezionareIlPiatto, gbc_lblSelezionareIlPiatto);

		
		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 2;
		getContentPane().add(panel_1, gbc_panel_1);
		
		JComboBox<IDish> comboBox = new JComboBox<>(ctrl.getMenu()); //ctrl.getMenu()
		JSpinner spinner = new JSpinner();
		spinner.setValue(1);
		panel_1.setLayout(new MigLayout(null, "[75%|15%|10%]", null));
		panel_1.add(comboBox, "cell 0 0,growx,aligny top");
		panel_1.add(spinner, "flowx,cell 1 0,growx,aligny center");
		
		JButton btnOk = new JButton("AGGIUNGI");
		panel_1.add(btnOk, "cell 2 0,alignx right");
		btnOk.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				ctrl.commandAdd(tableNumber, (IDish) comboBox.getSelectedItem(), (Integer) spinner.getValue());
			}
		});
		
		JLabel lblPiattiAttualmenteOrdinati = new JLabel("PIATTI ATTUALMENTE ORDINATI");
		GridBagConstraints gbc_lblPiattiAttualmenteOrdinati = new GridBagConstraints();
		gbc_lblPiattiAttualmenteOrdinati.insets = new Insets(0, 0, 5, 0);
		gbc_lblPiattiAttualmenteOrdinati.gridx = 0;
		gbc_lblPiattiAttualmenteOrdinati.gridy = 3;
		getContentPane().add(lblPiattiAttualmenteOrdinati, gbc_lblPiattiAttualmenteOrdinati);
		
		orders.setToolTipText("Tasto sinistro per indicare come evaso, tasto destro per eliminare un piatto");
		
		final JScrollPane scroll = new JScrollPane(orders);
		orders.setFillsViewportHeight(true);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		GridBagConstraints gbc_scroll = new GridBagConstraints();
		gbc_scroll.fill = GridBagConstraints.BOTH;
		gbc_scroll.gridheight = 7;
		gbc_scroll.insets = new Insets(0, 0, 5, 0);
		gbc_scroll.gridx = 0;
		gbc_scroll.gridy = 4;
		getContentPane().add(scroll, gbc_scroll);
		ctrl.commandOrdersViewUpdate(tableNumber);
		
		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 11;
		getContentPane().add(panel_2, gbc_panel_2);
		panel_2.setLayout(new BorderLayout(0, 0));
		panel_2.add(lblContoTotale, BorderLayout.WEST);
		errorLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 11));
		
		panel_2.add(errorLabel, BorderLayout.EAST);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.SOUTH;
		gbc_panel.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 12;
		getContentPane().add(panel, gbc_panel);
		
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent e) {
				dispose();
			}
		});
		
		JButton btnAnnulla = new JButton("CHIUDI");
		btnAnnulla.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		
		JButton btnStampa = new JButton("STAMPA");
		btnStampa.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				printHandler();
			}
		});
		panel.add(btnStampa);
		
		JButton btnReset = new JButton("RESET");
		btnReset.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				resetHandler();	
			}
		});
		
		panel.add(btnReset);
		panel.add(btnAnnulla);
		this.setLocationByPlatform(true);
	}
	
	@Override
	public void addOrderToView(final String name, final double price, final int amount, final int processed) {
		Character c;
		if(processed < amount) {
			c = '\u2718';
		} else {
			c = '\u2713';
		}
		addRowToTableModel(new Object[] {name, price, amount, price * amount, c.toString().concat(STRING_SEPARATOR).concat(Integer.toString(processed))});
	}
	
	@Override
	public void billUpdate(final double bill, final double effectiveBill) {
		lblContoTotale.setText(BILL_TEXT.concat(Double.toString(bill)).concat(CURRENCY_SYMBOL).concat(STRING_SEPARATOR).concat(EFFECTIVE_BILL_TEXT)
				               .concat(Double.toString(effectiveBill)).concat(CURRENCY_SYMBOL));
	}
	
	@Override
	public void clearTab() {
		orders.reset();
	}
	
	@Override
	public void showError(Exception e) {
		errorLabel.setText(e.getMessage());
	}
	
	@Override
	public void clearErrors() {
		errorLabel.setText("");
	}
	
	private void addRowToTableModel(Object[] obj) {
		orders.addRow(obj);
	}
	
	private void printHandler() {
		ctrl.commandPrint(tableNumber, orders, "Conto del tavolo n° " + tableNumber, lblContoTotale.getText());
	}
	
	private void resetHandler() {
		final int n = JOptionPane.showConfirmDialog(this, "Vuoi davvero eseguire il reset del tavolo?", "Reset",  JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION) {
			ctrl.commandReset(tableNumber);
		}	
	}

	@Override
	public void showMessage(String message) {
		JOptionPane.showMessageDialog(this, "Informazione: ".concat(message), "Messaggio",  JOptionPane.INFORMATION_MESSAGE);		
	}

}
