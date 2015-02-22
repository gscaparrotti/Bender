package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import controller.IMainController;
import controller.IMainViewController;
import controller.MainController;
import viewAccessories.ThumbnailIcon;

public class RestaurantView extends JFrame{

	private static final long serialVersionUID = 2118299654730994785L;
	private static final Dimension SCREEN = Toolkit.getDefaultToolkit().getScreenSize();
	private int columns = 0;
	private JPanel tablePanel = new JPanel(new GridBagLayout());
	private GridBagConstraints tablecnst = new GridBagConstraints();
	private JCheckBox autoSaveCheckBox = new JCheckBox("Auto-Salvataggio");
	private IMainController ctrl;
	private IMainViewController viewCtrl;
	
	public RestaurantView() {
		super("Bender");
		this.setPreferredSize(new Dimension((int) (SCREEN.width*0.75), (int) (SCREEN.height*0.75)));
		this.setResizable(true);
		this.setLocationByPlatform(true);
		this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		buildView();
	}
	
	public void setControllers(final IMainController controller, final IMainViewController viewController) {
		this.ctrl = controller;
		this.viewCtrl = viewController;
	}
	
	private void buildView() {
		//creazione del pannello principale
		JPanel mainPanel = new JPanel(new BorderLayout());
		//creazione di buttonPanelInternal e buttonPanel con relativi pulsanti e icone
		JButton addTable = new JButton("Nuovo Tavolo");
		JButton removeTable = new JButton("Rimuovi tavolo");
		JButton exit = new JButton("Esci");
		JButton load = new JButton("Carica");
		JButton save = new JButton("Salva");
		ThumbnailIcon icon = new ThumbnailIcon(MainController.DIR + MainController.SEPARATOR + "res" + MainController.SEPARATOR + "icon.gif");
		JLabel iconLabel = new JLabel(icon);
		JPanel buttonPanelInternal = new JPanel(new GridBagLayout());
		GridBagConstraints cnst = new GridBagConstraints();
		cnst.gridy = 0;
		cnst.fill = GridBagConstraints.HORIZONTAL;
		buttonPanelInternal.add(addTable, cnst);
		cnst.gridy++;
		buttonPanelInternal.add(removeTable, cnst);
		cnst.gridy++;
		buttonPanelInternal.add(load, cnst);
		cnst.gridy++;
		buttonPanelInternal.add(save, cnst);
		cnst.gridy++;
		buttonPanelInternal.add(exit, cnst);
		cnst.gridy++;
		buttonPanelInternal.add(autoSaveCheckBox, cnst);
		autoSaveCheckBox.setSelected(true);
		cnst.gridy++;
		buttonPanelInternal.add(iconLabel, cnst);
		cnst.gridy++;
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanelInternal.setBackground(new Color(255, 180, 100));
		buttonPanel.setBackground(new Color(255, 180, 100));
		buttonPanel.add(buttonPanelInternal);
		//creazione di tablePanel
		initLayout();
		JScrollPane jsp = new JScrollPane(tablePanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jsp.setAutoscrolls(true);
		tablePanel.setBackground(new Color(255, 255, 200));
		//creazione del pannello con l'elenco degli ordini
		/*JPanel ordersPanel = new JPanel(new FlowLayout());*/
		//aggiunta dei nuovi JPanel a mainPanel
		mainPanel.add(jsp, BorderLayout.CENTER);
		mainPanel.add(buttonPanel, BorderLayout.EAST);
		//aggiunta di mainPanel al JFrame
		this.add(mainPanel);
		pack();
		//aggiunta degli actionListener ai pulsanti
		addTable.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addTable(viewCtrl.addTable());
			}
		});
		removeTable.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(viewCtrl.removeTable()) {
					tablePanel.remove(tablePanel.getComponentCount()-1);
					tablePanel.repaint();
				}
			}
		});
		save.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				ctrl.commandSave();			
			}
		});
		load.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				int amount = ctrl.commandLoad();
				if(amount==-1) {
					setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					showApplicationMessage("Impossibile caricare i dati");
				} else {
					tablePanel.removeAll();
					tablePanel.repaint();
					initLayout();
					for (int i=1; i<=amount; i++) {
						addTable(i);
					}
				}
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			}
		});
		exit.addActionListener(new ActionListener() {			
			@Override
			public void actionPerformed(ActionEvent e) {
				quitHandler();
			}
		});
		//aggiunta di un windowListener alla finestra principale
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(final WindowEvent e) {
				quitHandler();
			}
		});
		//termine della creazione della schermata principale
	}
	
	private void addTable(int n) {
		JButton newButton = new JButton(Integer.toString(n));
		newButton.setFont(newButton.getFont().deriveFont(Font.BOLD, 32));
		newButton.setPreferredSize(new Dimension(RestaurantView.SCREEN.width/10, RestaurantView.SCREEN.height/10));
		newButton.setBackground(new Color(255, 255, 70));
		newButton.addActionListener(new ActionListener() {				
			@Override
			public void actionPerformed(ActionEvent e) {
				TableDialog tableDialog = new TableDialog(ctrl, Integer.parseInt(newButton.getText()));	
				ctrl.getDialogController().setView(tableDialog);
				tableDialog.setVisible(true);			
			}
		});
		if(columns==0) {
			columns = tablePanel.getWidth()/newButton.getPreferredSize().width;
		}
		tablePanel.add(newButton, tablecnst);
		tablecnst.gridx++;
		if((tablecnst.gridx) % columns == 0) {
			tablecnst.gridy++;
			tablecnst.gridx=0;
		}
		validate();
	}
	
	public boolean getAutoSaveOption() {
		return autoSaveCheckBox.isSelected();
	}
	
	public void showApplicationMessage(String message) {
		JOptionPane.showMessageDialog(this, "Informazione: ".concat(message), "Messaggio",  JOptionPane.INFORMATION_MESSAGE);
	}
	
	public void showIrreversibleError(String message) {
		JOptionPane.showMessageDialog(this, "Si è verificato un errore irreversibile: ".concat(message).concat
				("L'applicazione verrà chiusa"), "Errore Fatale",  JOptionPane.ERROR_MESSAGE);
		exit();
	}
	
	private void initLayout() {
		tablecnst.gridx=0;
		tablecnst.gridy=0;
		tablecnst.weightx=1;
		tablecnst.gridwidth=1;
		tablecnst.fill=GridBagConstraints.BOTH;
		tablecnst.insets = new Insets(3, 3, 3, 3);
	}
	
	private void quitHandler() {
		final int n = JOptionPane.showConfirmDialog(this, "Vuoi davvero uscire?", "Uscita",  JOptionPane.YES_NO_OPTION);
		if (n == JOptionPane.YES_OPTION) {
			exit();
		}	
	}
	
	private void exit() {
		System.exit(0);
	}
 
}
