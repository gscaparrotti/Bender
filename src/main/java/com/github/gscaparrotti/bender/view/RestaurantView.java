package com.github.gscaparrotti.bender.view;

import com.github.gscaparrotti.bender.controller.IMainController;
import com.github.gscaparrotti.bender.controller.IMainViewController;
import com.github.gscaparrotti.bender.controller.MainController;
import com.github.gscaparrotti.bender.legacy.LegacyNetHelper;
import com.github.gscaparrotti.bender.springUtils.ApplicationContextProvider;
import com.github.gscaparrotti.bender.viewdialogs.MainViewJTable;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Objects;
import javax.swing.*;
import org.springframework.context.ConfigurableApplicationContext;

/**
 *
 */
public class RestaurantView extends JFrame implements IRestaurantView {

    private static final long serialVersionUID = 2118299654730994785L;
    private static final Dimension SCREEN = Toolkit.getDefaultToolkit().getScreenSize();
    private static final String[] PROPS = new String[] { "Piatto", "Tavolo", "Quantità" };
    private int columns;
    private final JPanel tablePanel = new JPanel(new GridBagLayout());
    private GridBagConstraints tablecnst = new GridBagConstraints();
    private final JCheckBox autoSaveCheckBox = new JCheckBox("Auto-Salvataggio");
    private boolean filter;
    private IMainController ctrl;
    private IMainViewController viewCtrl;
    private MainViewJTable toBeServed;

    /**
     * Creates a new {@link RestaurantView} windows. It is resizable, and its
     * preferred size is 75% of each screen's dimensions.
     */
    public RestaurantView() {
        super("Bender");
        // CHECKSTYLE:OFF
        this.setPreferredSize(new Dimension((int) (SCREEN.width * 0.75), (int) (SCREEN.height * 0.75)));
        // CHECKSTYLE:ON
        this.setResizable(true);
        this.setLocationByPlatform(true);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    }

    @Override
    public void setControllers(final IMainController controller, final IMainViewController viewController) {
        Objects.requireNonNull(controller);
        Objects.requireNonNull(viewController);
        this.ctrl = controller;
        this.viewCtrl = viewController;
    }

    /**
     * Creates the application.view.
     */
    // CHECKSTYLE DISABLE MagicNumber FOR 120 LINES
    public void buildView() {
        // creazione del pannello principale
        final JPanel mainPanel = new JPanel(new BorderLayout());
        // creazione di buttonPanelInternal e buttonPanel con relativi pulsanti
        // e icone
        final JButton addTable = new JButton("Nuovo Tavolo");
        final JButton removeTable = new JButton("Rimuovi tavolo");
        final JButton exit = new JButton("Esci");
        final JButton load = new JButton("Carica");
        final JButton save = new JButton("Salva");
        save.setVisible(false);
        final ImageIcon icon = new ImageIcon(RestaurantView.class.getResource("/icon.png"));
        final JLabel iconLabel = new JLabel(icon);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        final JPanel buttonPanelInternal = new JPanel(new GridBagLayout());
        GridBagConstraints buttonCnst = new GridBagConstraints();
        buttonCnst.gridy = 0;
        buttonCnst.fill = GridBagConstraints.HORIZONTAL;
        buttonPanelInternal.add(addTable, buttonCnst);
        buttonCnst.gridy++;
        buttonPanelInternal.add(removeTable, buttonCnst);
        buttonCnst.gridy++;
        buttonPanelInternal.add(load, buttonCnst);
        buttonCnst.gridy++;
        buttonPanelInternal.add(save, buttonCnst);
        buttonCnst.gridy++;
        buttonPanelInternal.add(exit, buttonCnst);
        buttonCnst.gridy++;
        buttonPanelInternal.add(autoSaveCheckBox, buttonCnst);
        autoSaveCheckBox.setSelected(true);
        autoSaveCheckBox.setBackground(new Color(255, 180, 100));
        buttonCnst.gridy++;
        buttonPanelInternal.add(iconLabel, buttonCnst);
        buttonCnst.gridy++;
        final JLabel ip = new JLabel(LegacyNetHelper.getCurrentIP(), JLabel.CENTER);
        ip.setFont(ip.getFont().deriveFont(Font.BOLD, 16));
        ip.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        buttonPanelInternal.add(ip, buttonCnst);
        buttonPanelInternal.setVisible(false);
        final JPanel buttonPanel = new JPanel(new BorderLayout());
        final ImageIcon arrowLeft = new ImageIcon(new ImageIcon(RestaurantView.class.getResource("/arrow_left.png"))
                .getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        final JLabel iconArrow = new JLabel(arrowLeft);
        final ImageIcon arrowRight = new ImageIcon(new ImageIcon(RestaurantView.class.getResource("/arrow_right.png"))
                .getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        iconArrow.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                super.mouseClicked(e);
                buttonPanelInternal.setVisible(!buttonPanelInternal.isVisible());
                if (buttonPanelInternal.isVisible()) {
                    iconArrow.setIcon(arrowRight);
                } else {
                    iconArrow.setIcon(arrowLeft);
                }
            }
        });
        buttonPanelInternal.setBackground(new Color(255, 180, 100));
        buttonPanel.setBackground(new Color(255, 180, 100));
        buttonPanel.add(iconArrow, BorderLayout.WEST);
        final JPanel innerButtonPanel = new JPanel();
        innerButtonPanel.add(buttonPanelInternal);
        innerButtonPanel.setBackground(new Color(255, 180, 100));
        buttonPanel.add(innerButtonPanel, BorderLayout.CENTER);
        // creazione di tablePanel
        initLayout();
        final JScrollPane jsp = new JScrollPane(tablePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jsp.setAutoscrolls(true);
        tablePanel.setBackground(new Color(255, 255, 200));
        // creazione della tabella con gli ordini da evadere
        toBeServed = new MainViewJTable(PROPS, ctrl);
        toBeServed.setBackground(new Color(255, 255, 200));
        final JScrollPane scrollToBeServed = new JScrollPane(toBeServed, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        toBeServed.setFillsViewportHeight(true);
        scrollToBeServed.setPreferredSize(new Dimension((int) (buttonPanel.getPreferredSize().getWidth() * 1.5),
                (int) (buttonPanel.getPreferredSize().getHeight())));
        final JPanel toBeServedPanel = new JPanel(new BorderLayout());
        final JLabel daServire = new JLabel("<html>Piatti da Servire <br/> <br/> </html>");
        daServire.setHorizontalAlignment(JLabel.CENTER);
        daServire.setFont(daServire.getFont().deriveFont(Font.BOLD, 24));
        daServire.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        final JCheckBox filterOrders = new JCheckBox("Filtra ordini");
        toBeServedPanel.add(daServire, BorderLayout.NORTH);
        toBeServedPanel.add(scrollToBeServed, BorderLayout.CENTER);
        toBeServedPanel.add(filterOrders, BorderLayout.SOUTH);
        toBeServedPanel.setBackground(new Color(255, 190, 100));
        // aggiunta dei nuovi JPanel a mainPanel
        mainPanel.add(toBeServedPanel, BorderLayout.WEST);
        mainPanel.add(jsp, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.EAST);
        // aggiunta di mainPanel al JFrame
        this.add(mainPanel);
        // aggiunta degli actionListener ai pulsanti
        addTable.addActionListener(arg0 -> addTable(viewCtrl.commandAddTable()));
        removeTable.addActionListener(e -> {
            if (viewCtrl.commandRemoveTable() && tablePanel.getComponentCount() > 0) {
                tablePanel.remove(tablePanel.getComponentCount() - 1);
                tablePanel.repaint();
            }
        });
        save.addActionListener(e -> ctrl.commandSave());
        load.addActionListener(e -> {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            final int amount = ctrl.commandLoad();
            if (amount == -1) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                showApplicationMessage("Impossibile caricare i dati");
            } else {
                refreshTables(amount);
            }
            viewCtrl.updateUnprocessedOrdersInView();
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        });
        filterOrders.addChangeListener(e -> {
            filter = filterOrders.isSelected();
            viewCtrl.updateUnprocessedOrdersInView();
        });
        exit.addActionListener(e -> quitHandler());
        // aggiunta di un windowListener alla finestra principale
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent e) {
                quitHandler();
            }
        });
        pack();
        // termine della creazione della schermata principale
    }

    private void addTable(final int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        final JButton newButton = new JButton(Integer.toString(n));
        newButton.setFont(newButton.getFont().deriveFont(Font.BOLD, 32));
        newButton.setPreferredSize(new Dimension(RestaurantView.SCREEN.width / 10, RestaurantView.SCREEN.height / 10));
        // CHECKSTYLE DISABLE MagicNumber FOR 1 LINES
        newButton.setBackground(new Color(255, 255, 70));
        newButton.addActionListener(e -> {
            final TableDialog tableDialog = new TableDialog(ctrl, Integer.parseInt(newButton.getText().substring(0, newButton.getText().contains(" ") ? newButton.getText().indexOf(" ") : newButton.getText().length())));
            ctrl.getDialogController().setView(tableDialog);
            tableDialog.setVisible(true);
            viewCtrl.updateUnprocessedOrdersInView();
            viewCtrl.updateTableNamesInView();
        });
        if (columns == 0) {
            columns = tablePanel.getWidth() / newButton.getPreferredSize().width;
        }
        tablePanel.add(newButton, tablecnst);
        tablecnst.gridx++;
        if ((tablecnst.gridx) % columns == 0) {
            tablecnst.gridy++;
            tablecnst.gridx = 0;
        }
        validate();
        updateTableNames();
    }

    @Override
    public boolean isAutoSaveOption() {
        return autoSaveCheckBox.isSelected();
    }

    @Override
    public void showApplicationMessage(final String message) {
        Objects.requireNonNull(message);
        JOptionPane.showMessageDialog(this, "Informazione: ".concat(message), "Messaggio",
                JOptionPane.INFORMATION_MESSAGE);
    }

    @Override
    public void showIrreversibleError(final String message) {
        Objects.requireNonNull(message);
        JOptionPane.showMessageDialog(this,
                "Si è verificato un errore irreversibile: ".concat(message).concat(". L'applicazione verrà chiusa"),
                "Errore Fatale", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void clearUnprocessedOrders() {
        toBeServed.reset();
    }

    @Override
    public void addUnprocessedOrder(final String name, final String table, final int quantity) {
        toBeServed.addRow(name, table, quantity);
    }
    
    public boolean isFilterEnabled() {
        return filter;
    }
    
    @Override
    public void updateTableNames() {
        synchronized (tablePanel.getTreeLock()) {
            int tableNumber = 0;
            for (final Component c : tablePanel.getComponents()) {
                if (c instanceof JButton) {
                    tableNumber++;
                    final String name = formattedName(tableNumber);
                    final JButton b = (JButton) c;
                    b.setText(tableNumber + name);
                }
            }
        }
    }

    @Override
    public void refreshTables(final int amount) {
        tablePanel.removeAll();
        tablePanel.repaint();
        initLayout();
        for (int i = 1; i <= amount; i++) {
            addTable(i);
        }
    }
    
    private String formattedName(final int tableNumber) {
        final String name = ctrl.getRestaurant().getTableName(tableNumber);
        return !name.equals("") ? " - " + name : "";
    }

    private void initLayout() {
        tablecnst.gridx = 0;
        tablecnst.gridy = 0;
        tablecnst.weightx = 1;
        tablecnst.gridwidth = 1;
        tablecnst.fill = GridBagConstraints.BOTH;
        tablecnst.insets = new Insets(3, 3, 3, 3);
    }

    private void quitHandler() {
        final int n = JOptionPane.showConfirmDialog(this, "Vuoi davvero uscire?", "Uscita", JOptionPane.YES_NO_OPTION);
        if (n == JOptionPane.YES_OPTION) {
            if (MainController.getInstance().getNetworkController() != null) {
                MainController.getInstance().getNetworkController().stopListening();
            }
            ((ConfigurableApplicationContext) ApplicationContextProvider.getApplicationContext()).close();
            this.dispose();
        }
    }
}
