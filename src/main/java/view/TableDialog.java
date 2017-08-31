package view;

import javax.swing.JDialog;

import java.awt.GridBagLayout;

import javax.swing.JLabel;

import java.awt.GridBagConstraints;
import java.awt.Font;

import javax.swing.JComboBox;

import model.IDish;
import model.OrderedDish;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import controller.IDialogController;
import controller.IMainController;
import net.miginfocom.swing.MigLayout;
import viewdialogs.DialogJTable;

import java.awt.BorderLayout;

/**
 *
 */
public class TableDialog extends JDialog implements ITableDialog {

    private static final long serialVersionUID = -2269793459529910803L;

    private static final String[] PROPS = new String[] { "Piatto", "Costo", "Quantità", "Costo totale", "Evaso" };
    private static final String BILL_TEXT = "Conto Totale: ";
    private static final String EFFECTIVE_BILL_TEXT = "Conto Effettivo: ";
    private static final String CURRENCY_SYMBOL = " €";
    private static final String STRING_SEPARATOR = " - ";
    private static final int[] INSETS = { 0, 0, 5, 0 };
    private static final int[] SIZE = { 650, 450 };
    private final JLabel errorLabel = new JLabel();
    private final JLabel lblContoTotale = new JLabel(BILL_TEXT);
    private final DialogJTable orders;
    private final int tableNumber;
    private JLabel lblGestioneDegliOrdini;
    private boolean isManual;
    private IDialogController ctrl;
    private final IMainController mainCtrl; // NOPMD

    /**
     * @param newCtrl
     *            The {@link IMainController} which will provide all the needed
     *            resources
     * @param newTableNumber
     *            the ID of the table represented by this dialog
     * 
     *            Creates a new table dialog.
     */
    public TableDialog(final IMainController newCtrl, final int newTableNumber) {
        super();
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.tableNumber = newTableNumber;
        this.mainCtrl = newCtrl;
        this.orders = new DialogJTable(PROPS, mainCtrl, newTableNumber);
    }

    @Override
    public void setControllerAndBuildView(final IDialogController dialogCtrl) {
        this.ctrl = dialogCtrl;
        buildView();
    }

    // CHECKSTYLE DISABLE MagicNumber FOR 130 LINES
    private void buildView() {
        this.setTitle("Gestione del tavolo n° " + tableNumber + formattedName());
        this.setResizable(true);
        setBounds(100, 100, SIZE[0], SIZE[1]);
        final GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] { 0, 0 };
        gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
                Double.MIN_VALUE };
        getContentPane().setLayout(gridBagLayout);

        lblGestioneDegliOrdini = new JLabel(
                "GESTIONE DEGLI ORDINI PER IL TAVOLO N° " + Integer.toString(tableNumber) + formattedName());
        lblGestioneDegliOrdini.setFont(lblGestioneDegliOrdini.getFont().deriveFont(Font.BOLD, 16));
        final GridBagConstraints gbcLblGestioneDegliOrdini = new GridBagConstraints();
        gbcLblGestioneDegliOrdini.insets = new Insets(INSETS[0], INSETS[1], INSETS[2], INSETS[3]);
        gbcLblGestioneDegliOrdini.gridx = 0;
        gbcLblGestioneDegliOrdini.gridy = 0;
        getContentPane().add(lblGestioneDegliOrdini, gbcLblGestioneDegliOrdini);

        final JLabel lblSelezionareIlPiatto = new JLabel(
                "Selezionare il piatto da aggiungere, quindi premere AGGIUNGI");
        final GridBagConstraints gbcLblSelezionareIlPiatto = new GridBagConstraints();
        gbcLblSelezionareIlPiatto.insets = new Insets(INSETS[0], INSETS[1], INSETS[2], INSETS[3]);
        gbcLblSelezionareIlPiatto.gridx = 0;
        gbcLblSelezionareIlPiatto.gridy = 1;
        getContentPane().add(lblSelezionareIlPiatto, gbcLblSelezionareIlPiatto);
        
        final JTextField tableNameTextField = new JTextField();
        final JLabel tableNameLabel = new JLabel("Nuovo nome del tavolo: ");    
        final JButton buttonTableName = new JButton("Imposta nome");
        buttonTableName.addActionListener(new ActionListener() {          
            @Override
            public void actionPerformed(final ActionEvent e) {
                ctrl.commandSetTableName(tableNumber, tableNameTextField.getText());
            }
        });
        final JPanel panelTableName = new JPanel(new BorderLayout(5, 0));
        panelTableName.add(tableNameLabel, BorderLayout.WEST);
        panelTableName.add(buttonTableName, BorderLayout.EAST);
        panelTableName.add(tableNameTextField, BorderLayout.CENTER);
        final GridBagConstraints gbcTableName = new GridBagConstraints();
        gbcTableName.insets = new Insets(INSETS[0], 11, INSETS[2], 7);
        gbcTableName.gridx = 0;
        gbcTableName.gridy = 2;
        gbcTableName.fill = GridBagConstraints.HORIZONTAL;
        getContentPane().add(panelTableName, gbcTableName);
        
        final JPanel panel1 = new JPanel();
        final GridBagConstraints gbcPanel1 = new GridBagConstraints();
        gbcPanel1.insets = new Insets(INSETS[0], INSETS[1], INSETS[2], INSETS[3]);
        gbcPanel1.fill = GridBagConstraints.HORIZONTAL;
        gbcPanel1.gridx = 0;
        gbcPanel1.gridy = 3;
        getContentPane().add(panel1, gbcPanel1);

        final JComboBox<IDish> comboBox = new JComboBox<>(ctrl.getMenu()); // ctrl.getMenu()
        final JSpinner spinner = new JSpinner();
        spinner.setValue(1);
        panel1.setLayout(new MigLayout(null, "[60%|20%|2%|9%|9%]", null));
        panel1.add(comboBox, "cell 0 0,growx,aligny top");
        panel1.add(spinner, "flowx,cell 1 0,growx,aligny center");

        final JButton btnOk = new JButton("AGGIUNGI");
        final JButton manual = new JButton("Ins. Manuale");
        final JTextField nomeManual = new JTextField("Nome del piatto");
        final JTextField prezzoManual = new JTextField("0.00", 4);
        final JTextArea euro = new JTextArea("€");
        euro.setBackground(TableDialog.this.getBackground());
        euro.setBorder(BorderFactory.createEmptyBorder());
        euro.setEnabled(false);
        panel1.add(btnOk, "cell 3 0,alignx right");
        panel1.add(manual, "cell 4 0,alignx right");
        btnOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (isManual) {
                    try {
                        final double price = Double.parseDouble(prezzoManual.getText());
                        final String name = nomeManual.getText();
                        if (name.endsWith("*")) {
                            ctrl.commandAdd(tableNumber, new OrderedDish(name, price, 1), 1);
                        } else {
                            ctrl.commandAdd(tableNumber, new OrderedDish(name, price, 0), 1);
                        }
                    } catch (final NumberFormatException ex) {
                        mainCtrl.showMessageOnMainView("Prezzo inserito non valido. Controllare.");
                    }
                } else {
                    ctrl.commandAdd(tableNumber, new OrderedDish((IDish) comboBox.getSelectedItem()), (Integer) spinner.getValue());   
                }
            }
        });
        manual.addActionListener(new ActionListener() {          
            @Override
            public void actionPerformed(final ActionEvent e) {
                if (isManual) {
                    panel1.remove(nomeManual);
                    panel1.remove(prezzoManual);
                    panel1.remove(euro);
                    panel1.add(comboBox, "cell 0 0,growx,aligny top");
                    panel1.add(spinner, "cell 1 0,growx,aligny top");
                    manual.setText("Ins. Manuale");
                    isManual = false;
                } else {
                    panel1.remove(comboBox);
                    panel1.remove(spinner);
                    panel1.add(nomeManual, "cell 0 0,growx,aligny top");
                    panel1.add(prezzoManual, "cell 1 0,growx,aligny top"); 
                    panel1.add(euro, "cell 2 0");
                    manual.setText("Ins. da Menu");
                    isManual = true;
                }
                panel1.revalidate();
                panel1.repaint();
            }
        });
        final JLabel lblPiattiAttualmenteOrdinati = new JLabel("PIATTI ATTUALMENTE ORDINATI");
        final GridBagConstraints gbcLblPiattiAttualmenteOrdinati = new GridBagConstraints();
        gbcLblPiattiAttualmenteOrdinati.insets = new Insets(INSETS[0], INSETS[1], INSETS[2], INSETS[3]);
        gbcLblPiattiAttualmenteOrdinati.gridx = 0;
        gbcLblPiattiAttualmenteOrdinati.gridy = 4;
        getContentPane().add(lblPiattiAttualmenteOrdinati, gbcLblPiattiAttualmenteOrdinati);

        orders.setToolTipText("Tasto sinistro per indicare come evaso, tasto destro per eliminare un piatto");

        final JScrollPane scroll = new JScrollPane(orders);
        orders.setFillsViewportHeight(true);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        final GridBagConstraints gbcScroll = new GridBagConstraints();
        gbcScroll.fill = GridBagConstraints.BOTH;
        gbcScroll.gridheight = 7;
        gbcScroll.insets = new Insets(INSETS[0], INSETS[1], INSETS[2], INSETS[3]);
        gbcScroll.gridx = 0;
        gbcScroll.gridy = 5;
        getContentPane().add(scroll, gbcScroll);
        ctrl.commandOrdersViewUpdate(tableNumber);

        final JPanel panel2 = new JPanel();
        final GridBagConstraints gbcPanel2 = new GridBagConstraints();
        gbcPanel2.insets = new Insets(INSETS[0], INSETS[1], INSETS[2], INSETS[3]);
        gbcPanel2.fill = GridBagConstraints.BOTH;
        gbcPanel2.gridx = 0;
        gbcPanel2.gridy = 12;
        getContentPane().add(panel2, gbcPanel2);
        panel2.setLayout(new BorderLayout(0, 0));
        panel2.add(lblContoTotale, BorderLayout.WEST);
        errorLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD, 11));
        panel2.add(errorLabel, BorderLayout.EAST);

        final JPanel panel = new JPanel();
        final GridBagConstraints gbcPanel = new GridBagConstraints();
        gbcPanel.anchor = GridBagConstraints.SOUTH;
        gbcPanel.fill = GridBagConstraints.HORIZONTAL;
        gbcPanel.gridx = 0;
        gbcPanel.gridy = 13;
        getContentPane().add(panel, gbcPanel);

        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent e) {
                ctrl.detachView();
                dispose();
            }
        });

        final JButton btnAnnulla = new JButton("CHIUDI");
        btnAnnulla.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                ctrl.detachView();
                dispose();
            }
        });

        final JButton btnStampa = new JButton("STAMPA");
        btnStampa.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {
                printHandler();
            }
        });
        panel.add(btnStampa);

        final JButton btnReset = new JButton("RESET");
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent arg0) {
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
        if (processed < amount) {
            c = '\u2718';
        } else {
            c = '\u2713';
        }
        addRowToTableModel(new Object[] { name, price, amount, price * amount,
                c.toString().concat(STRING_SEPARATOR).concat(Integer.toString(processed)) });
    }

    @Override
    public void billUpdate(final double bill, final double effectiveBill) {
        lblContoTotale.setText(BILL_TEXT.concat(Double.toString(bill)).concat(CURRENCY_SYMBOL).concat(STRING_SEPARATOR)
                .concat(EFFECTIVE_BILL_TEXT).concat(Double.toString(effectiveBill)).concat(CURRENCY_SYMBOL));
    }

    @Override
    public void clearTab() {
        orders.reset();
    }
    
    @Override
    public void updateTableNameInDialog() {
        this.setTitle("Gestione del tavolo n° " + tableNumber + formattedName());
        lblGestioneDegliOrdini.setText("GESTIONE DEGLI ORDINI PER IL TAVOLO N° " + Integer.toString(tableNumber) + formattedName());
    }

    @Override
    public void showError(final Exception e) {
        errorLabel.setText(e.getMessage());
    }

    @Override
    public void clearErrors() {
        errorLabel.setText("");       
    }
    
    private String formattedName() {
        return !ctrl.getTableName(tableNumber).equals("") ? STRING_SEPARATOR + ctrl.getTableName(tableNumber) : "";
    }

    private void addRowToTableModel(final Object... obj) {
        orders.addRow(obj);
    }

    private void printHandler() {
        ctrl.commandPrint(tableNumber, orders, "Conto del tavolo n° " + tableNumber, lblContoTotale.getText());
    }

    private void resetHandler() {
        final int n = JOptionPane.showConfirmDialog(this, "Vuoi davvero eseguire il reset del tavolo?", "Reset",
                JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            ctrl.commandReset(tableNumber);
        }
    }

    @Override
    public void showMessage(final String message) {
        JOptionPane.showMessageDialog(this, "Informazione: ".concat(message), "Messaggio",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public int getTable() {
        return this.tableNumber;
    }

}
