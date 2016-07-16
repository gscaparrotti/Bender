package controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import model.IDish;
import model.IRestaurant;
import model.Order;
import model.Pair;

/**
 * This class provides a mean to trasmit datas from this application to a remote destination.
 *
 */
public class NetworkController extends Thread {

    private final int port;
    private boolean listen = true;
    private final Set<Socket> sockets = new HashSet<>();
    private final IMainController mainController;

    /**
     * @return the current local IPv4 address of this machine.
     */
    public static String getCurrentIP() {
        try {
            String ips = "<html>";
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface n = e.nextElement();
                Enumeration<InetAddress> ee = n.getInetAddresses();
                while (ee.hasMoreElements()) {
                    InetAddress i = ee.nextElement();
                    if (i.getHostAddress().startsWith("192.168") || i.getHostAddress().startsWith("10.0")) {
                        ips = ips.concat(i.getHostAddress() + "<br/>");
                    }
                }
            }
            ips += "</html>";
            return ips;
        } catch (SocketException e) {
            return "(IP Non Disponibile)";
        }
    }

    /**
     * @param mainCtrl The main controller which will provide references to all the needed resources (eg: 
     *                  the view or the model).
     * @param port the port the welcome socket will listen to.
     */
    public NetworkController(final IMainController mainCtrl, final int port) {
        super();
        this.port = port;
        if (mainCtrl != null) {
            this.mainController = mainCtrl;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void run() {
        try {
            final ServerSocket welcomeSocket = new ServerSocket(port);
            while (listen) {
                final Socket socket = welcomeSocket.accept();
                final NetClientListener cl = new NetClientListener(socket);
                sockets.add(socket);
                System.out.println(sockets.size());
                cl.start();
            }
            welcomeSocket.close();
            for (final Socket s : sockets) {
                s.close();
                sockets.remove(s);
            }
        } catch (IOException e) {
            mainController.showIrreversibleErrorOnMainView("Impossibile avviare i servizi di rete: " + e.getMessage());
        }
    }

    /**
     * @param restaurant the object which contains all the ordered dishes.
     * @param table the table whose orders you want to dispatch or 0 if you want to send all the orders.
     */
    public void dispatchOrders(final IRestaurant restaurant, final int table) {
        if (restaurant == null || table < 0) {
            throw new IllegalArgumentException();
        }
        if (table == 0) {
            for (int i = 0; i < mainController.getRestaurant().getTablesAmount(); i++) {
                for (final Socket s : sockets) {
                    final NetClientSender sender = new NetClientSender(s, i);
                    sender.start();
                }
            }
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


    private class NetClientListener extends Thread {

        private final Socket socket;
        private ObjectInputStream input;

        NetClientListener(final Socket socket) {
            super();
            if (socket == null) {
                throw new IllegalArgumentException();
            }
            this.socket = socket;
        }

        @Override
        public void run() {
            super.run();
            try {
                this.input = new ObjectInputStream(socket.getInputStream());
                final Object clientInput = input.readObject();
                if (clientInput != null) {
                    if (clientInput instanceof String) {
                        final String stringInput = (String) clientInput;
                        if (stringInput.startsWith("GET TABLE")) {
                            final int tableNmbr = Integer.parseInt(stringInput.substring("GET TABLE".length() + 1));
                            new NetClientSender(socket, mainController.getRestaurant().getOrders(tableNmbr)).start();
                        } else if (stringInput.equals("GET PENDING ORDERS")) {
                            final List<Order> pending = new LinkedList<>();
                            for (int i = 1; i <= mainController.getRestaurant().getTablesAmount(); i++) {
                                for (Map.Entry<IDish, Pair<Integer, Integer>> entry : mainController.getRestaurant().getOrders(i).entrySet()) {
                                    if (entry.getValue().getX() > entry.getValue().getY()) {
                                        pending.add(new Order(i, entry.getKey(), entry.getValue()));
                                    }
                                }
                            }
                            new NetClientSender(socket, pending).start();
                        } else if (stringInput.equals("GET AMOUNT")) {
                            new NetClientSender(socket, Integer.valueOf(mainController.getRestaurant().getTablesAmount())).start();
                        } else if (stringInput.equals("GET MENU")) {
                            new NetClientSender(socket, mainController.getMenu()).start();
                        } else if (stringInput.startsWith("RESET TABLE")) {
                            final int tableNmbr = Integer.parseInt(stringInput.substring("RESET TABLE".length() + 1));
                            mainController.getRestaurant().resetTable(tableNmbr);
                            updateFinished(tableNmbr);
                            new NetClientSender(socket, "TABLE RESET CORRECTLY").start();
                        } else if (stringInput.equals("CLOSE CONNECTION")) {
                            new NetClientSender(socket, "CLOSE CONNECTION").start();
                        }
                    } else if (clientInput instanceof Order) {
                        final Order orderInput = (Order) clientInput;
                        if (orderInput.getAmounts().getY() == 0 && orderInput.getAmounts().getX() > 0) {
                            mainController.getRestaurant().addOrder(orderInput.getTable(), orderInput.getDish(), orderInput.getAmounts().getX());
                            new NetClientSender(socket, "ORDER ADDED CORRECTLY").start();
                        } else if (orderInput.getAmounts().getX() < 0) {
                            if (mainController.getRestaurant().getOrders(orderInput.getTable()).containsKey(orderInput.getDish())) {
                                mainController.getRestaurant().removeOrder(orderInput.getTable(), orderInput.getDish(), orderInput.getAmounts().getY());
                                new NetClientSender(socket, "ORDER UPDATED CORRECTLY").start();
                            }
                        } else {
                            mainController.getRestaurant().setOrderAsProcessed(orderInput.getTable(), orderInput.getDish());
                            new NetClientSender(socket, "ORDER UPDATED CORRECTLY").start();
                        }
                        updateFinished(orderInput.getTable());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                sockets.remove(socket);
                //mainController.showMessageOnMainView("Il client " + socket + " si è disconnesso.");
            } catch (NumberFormatException i) {
                mainController.showMessageOnMainView("Il client " + socket + " ha richiesto gli ordini"
                        + "di un tavolo non valido.");
            } catch (ClassNotFoundException e) {
                mainController.showMessageOnMainView("Il client " + socket + " ha inviato dati non validi.");
            }
        }
    }

    private void updateFinished(final int tableNumber) {
        mainController.getDialogController().commandOrdersViewUpdate(tableNumber);
        mainController.getMainViewController().updateUnprocessedOrders();
        mainController.autoSave();
    }

    private final class NetClientSender extends Thread {

        private final Socket socket;
        private ObjectOutputStream output;
        private final Object toSend;

        NetClientSender(final Socket socket, final Object toSend) {
            super();
            if (socket == null || toSend == null) {
                throw new IllegalArgumentException();
            }
            this.socket = socket;
            this.toSend = toSend;
            try {
                this.output = new ObjectOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                mainController.showIrreversibleErrorOnMainView("Impossibile ottenere l'outputStream: " + e.getMessage());
            }
        }

        @Override
        public void run() {
            super.run();
            try {
                output.writeObject(toSend);
                output.close();
                sockets.remove(socket);
            } catch (IOException e) {
                sockets.remove(socket);
                mainController.showMessageOnMainView("Il client " + socket + " si è disconnesso inaspettatamente."
                        + "\nIl client potrebbe non aver ricevuto dei dati.");
            }
        }
    }
}
