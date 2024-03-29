package com.github.gscaparrotti.bender.controller;

import com.github.gscaparrotti.bendermodel.model.IDish;
import com.github.gscaparrotti.bendermodel.model.IMenu;
import com.github.gscaparrotti.bendermodel.model.Menu;
import com.github.gscaparrotti.bendermodel.model.Order;
import com.github.gscaparrotti.bendermodel.model.OrderedDish;
import com.github.gscaparrotti.bendermodel.model.Pair;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.*;

/**
 * This class provides a mean to trasmit datas from this application to a remote destination.
 *
 * @deprecated
 * Use the REST-ful endpoints instead
 */
@Deprecated
public class NetworkController extends Thread {

    private final int port;
    private boolean listen = true;
    private final IMainController mainController;

    /**
     * @param mainCtrl The main application.controller which will provide references to all the needed resources (eg:
     *                  the application.view or the model).
     * @param port the port the welcome socket will listen to.
     */
    public NetworkController(final IMainController mainCtrl, final int port) {
        super();
        this.port = port;
        if (mainCtrl != null) {
            this.mainController = mainCtrl;
            if (mainCtrl.getNetworkController() != null) {
                throw new IllegalStateException("You are trying to have more than one network application.controller at once");
            }
            mainCtrl.setNetworkController(this);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void run() {
        try {
            final ServerSocket welcomeSocket = new ServerSocket(port);
            welcomeSocket.setSoTimeout(1000);
            while (listen) {
                try {
                    final ClientInteractor cl = new ClientInteractor(welcomeSocket.accept());
                    cl.start();
                } catch (final SocketTimeoutException e) {
                    //this is normal, it's used to exit from the blocking method to check if listening should continue.
                    //Blame Java for not having a better way to do it.
                }
            }
            welcomeSocket.close();
        } catch (IOException e) {
            this.stopListening();
            showErrorMessage("Errore nei servizi di rete: " + e.getMessage());
        }
    }

    /**
     * Closes the welcome socket and all the existing sockets. 
     * After you call this method, you cannot restart listening;
     * you must create a new NetworkController instead.
     */
    public void stopListening() {
        this.listen = false;
    }
    
    private void showErrorMessage(final String error) {
        SwingUtilities.invokeLater(() -> mainController.showIrreversibleErrorOnMainView(error));
    }

    private void showMessage(final String message) {
        SwingUtilities.invokeLater(() -> mainController.showMessageOnMainView(message));
    }

    private class ClientInteractor extends Thread {

        private final Socket socket;

        ClientInteractor(final Socket socket) {
            super();
            if (socket == null) {
                throw new IllegalArgumentException();
            } else {
                this.socket = socket;
            }
        }

        @Override
        public void run() {
            super.run();
            try {
                initializeSocket();
                final Object received = receiveObject();
                final byte[] toSend;
                synchronized (mainController.getRestaurant()) {
                    final Object response = decodeInputAndGenerateResponse(received);
                    toSend = serializeResponse(response);
                }
                sendSerializedResponse(toSend);
                closeSocket();
            } catch (final IOException|IllegalStateException|IllegalArgumentException|ClassNotFoundException e) {
                final String error = "Avviso: " + e + " - " + e.getMessage();
                try {
                    closeSocket();
                    showMessage(error + ". La socket è stata chiusa.");
                } catch (final IOException e1) {
                    showErrorMessage(error + ". Non è stato possibile chiudere la socket: " + e1.getMessage());
                }
            }
        }
        
        private void initializeSocket() throws IOException {
            socket.setSoTimeout(10000);
            socket.setTcpNoDelay(true);
        }
        
        private Object receiveObject() throws ClassNotFoundException, IOException {
            return new ObjectInputStream(socket.getInputStream()).readObject();
        }
        
        private Object decodeInputAndGenerateResponse(final Object clientInput) {
            Object response;
            try {
                if (clientInput != null) {
                    if (clientInput instanceof String) {
                        final String stringInput = (String) clientInput;
                        //In the following two cases a conversion is needed because CustomOrderedDish is not available inside Bender Mobile
                        if (stringInput.startsWith("GET TABLE")) {
                            final int tableNmbr = Integer.parseInt(stringInput.substring("GET TABLE".length() + 1));
                            response = mainController.getRestaurant().getOrders(tableNmbr).entrySet().stream().collect(Collectors.toMap(k -> {
                                    if (k.getKey() instanceof OrderedDish) {
                                        final OrderedDish dishInKey = (OrderedDish) k.getKey();
                                        return new OrderedDish(dishInKey.getName(), dishInKey.getPrice(), dishInKey.getFilterValue(), dishInKey);
                                    } else {
                                        return k.getKey();
                                    }
                                }, Map.Entry::getValue
                            ));
                        } else if (stringInput.equals("GET PENDING ORDERS")) {
                            final List<Order> pending = new LinkedList<>();
                            for (int i = 1; i <= mainController.getRestaurant().getTablesAmount(); i++) {
                                for (final Map.Entry<IDish, Pair<Integer, Integer>> entry : mainController.getRestaurant().getOrders(i).entrySet()) {
                                    if (entry.getValue().getX() > entry.getValue().getY()) {
                                        if (entry.getKey() instanceof OrderedDish) {
                                            final OrderedDish dishInKey = (OrderedDish) entry.getKey();
                                            final OrderedDish toSendDish = new OrderedDish(dishInKey.getName(), dishInKey.getPrice(), dishInKey.getFilterValue(), dishInKey);
                                            pending.add(new Order(i, toSendDish, entry.getValue()));
                                        } else {
                                            pending.add(new Order(i, entry.getKey(), entry.getValue()));
                                        }
                                    }
                                }
                            }
                            response = pending;
                        } else if (stringInput.equals("GET AMOUNT")) {
                            response = mainController.getRestaurant().getTablesAmount();
                        } else if (stringInput.equals("GET MENU")) {
                            IMenu menu = new Menu();
                            menu.addItems(mainController.getMenu().getDishesArray());
                            response = menu;
                        } else if (stringInput.startsWith("RESET TABLE")) {
                            final int tableNmbr = Integer.parseInt(stringInput.substring("RESET TABLE".length() + 1));
                            mainController.getRestaurant().resetTable(tableNmbr);
                            updateFinished(tableNmbr);
                            response = "TABLE RESET CORRECTLY";
                        } else if (stringInput.startsWith("GET NAMES")) {
                            response = mainController.getRestaurant().getAllNames();
                        } else if (stringInput.startsWith("SET NAME")) {
                            final String[] strings = stringInput.split(" ", 4);
                            final int tableNmbr = Integer.parseInt(strings[2]);
                            mainController.getRestaurant().setTableName(tableNmbr, strings[3]);
                            updateFinished(tableNmbr);
                            response = "NAME SET CORRECTLY";
                        } else if (stringInput.startsWith("REMOVE NAME")) {
                            final int tableNmbr = Integer.parseInt(stringInput.substring("REMOVE NAME".length() + 1));
                            mainController.getRestaurant().setTableName(tableNmbr, null);
                            updateFinished(tableNmbr);
                            response = "NAME SET CORRECTLY";
                        } else if (stringInput.equals("CLOSE CONNECTION")) {
                            response = "CLOSE CONNECTION";
                        } else {
                            throw new IllegalStateException("Ricevuti dati non validi: " + stringInput);
                        }
                    } else if (clientInput instanceof Order) {
                        Order orderInput = (Order) clientInput;
                        if (orderInput.getDish() instanceof OrderedDish) {
                            if (((OrderedDish) orderInput.getDish()).getTime().getTime() == 0L) {
                                orderInput = new Order(orderInput.getTable(), new OrderedDish(orderInput.getDish()), orderInput.getAmounts());
                            }
                        }
                        if (orderInput.getAmounts().getY() == 0 && orderInput.getAmounts().getX() > 0) {
                            mainController.getRestaurant().addOrder(orderInput.getTable(), orderInput.getDish(), orderInput.getAmounts().getX());
                            response = "ORDER ADDED CORRECTLY";
                        } else if (orderInput.getAmounts().getX() < 0) {
                            if (mainController.getRestaurant().getOrders(orderInput.getTable()).containsKey(orderInput.getDish())
                                    && orderInput.getAmounts().getY() <= mainController.getRestaurant().getOrders(orderInput.getTable()).get(orderInput.getDish()).getX()) {
                                mainController.getRestaurant().removeOrder(orderInput.getTable(), orderInput.getDish(), orderInput.getAmounts().getY());
                            }
                            response = "ORDER UPDATED CORRECTLY";
                        } else {
                            if (mainController.getRestaurant().getOrders(orderInput.getTable()).containsKey(orderInput.getDish())) {
                                mainController.getRestaurant().setOrderAsProcessed(orderInput.getTable(), orderInput.getDish());
                            }
                            response = "ORDER UPDATED CORRECTLY";
                        }
                        updateFinished(orderInput.getTable());
                    } else {
                        throw new IllegalStateException("Ricevuti dati non validi: " + clientInput);
                    }
                } else {
                    throw new IllegalStateException("Ricevuto un input nullo");
                }
            } catch (final NumberFormatException e1) {
                throw new IllegalStateException("Il client " + socket + " ha richiesto gli ordini" + "di un tavolo non valido.");
            }
            return response;
        }
        
        private byte[] serializeResponse(final Object response) throws IOException {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            final ObjectOutputStream ous = new ObjectOutputStream(baos);
            ous.writeObject(response);
            final byte[] serializedObject = baos.toByteArray();
            ous.close();
            return serializedObject;
            
        }
        
        private void sendSerializedResponse(final byte[] toSend) throws IOException {
            socket.getOutputStream().write(toSend);
        }
        
        private void closeSocket() throws IOException {
            socket.close();
        }
        
        private void updateFinished(final int tableNumber) {
            SwingUtilities.invokeLater(() -> {
                if (mainController.getDialogController() != null) {
                    mainController.getDialogController().updateOrdersInView(tableNumber);
                    mainController.getDialogController().updateTableNameInView(tableNumber);
                }
                if (mainController.getMainViewController() != null) {
                    mainController.getMainViewController().refreshTablesInView();
                    mainController.getMainViewController().updateUnprocessedOrdersInView();
                    mainController.getMainViewController().updateTableNamesInView();
                }
                mainController.autoSave();
            });
        }
    }
}
