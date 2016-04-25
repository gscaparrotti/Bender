package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

import model.IRestaurant;

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
        private BufferedReader input;

        NetClientListener(final Socket socket) {
            super();
            if (socket == null) {
                throw new IllegalArgumentException();
            }
            this.socket = socket;
            try {
                this.input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            } catch (IOException e) {
                mainController.showIrreversibleErrorOnMainView("Impossibile ottenere l'inputStream: " + e.getMessage());
            }
        }

        @Override
        public void run() {
            super.run();
            try {
                while (true) {
                    final String clientInput = input.readLine();
                    if (clientInput != null && clientInput.startsWith("GET TABLE")) {
                        final int tableNmbr = Integer.parseInt(clientInput.substring("GET TABLE".length() + 1));
                        new NetClientSender(socket, tableNmbr).start();
                    }
                }
            } catch (IOException e) {
                sockets.remove(socket);
                mainController.showMessageOnMainView("Il client " + socket + " si è disconnesso.");
            } catch (NumberFormatException i) {
                mainController.showMessageOnMainView("Il client " + socket + " ha richiesto gli ordini"
                        + "di un tavolo non valido.");
            }
        }
    }

    private final class NetClientSender extends Thread {

        private final Socket socket;
        private ObjectOutputStream output;
        private final int tableNmbr;

        NetClientSender(final Socket socket, final int tableNmbr) {
            super();
            if (socket == null || tableNmbr < 0) {
                throw new IllegalArgumentException();
            }
            this.socket = socket;
            this.tableNmbr = tableNmbr;
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
                output.writeObject(mainController.getRestaurant().getOrders(tableNmbr));
            } catch (IOException e) {
                sockets.remove(socket);
                mainController.showMessageOnMainView("Il client " + socket + " si è disconnesso inaspettatamente."
                        + "\nIl client potrebbe non aver ricevuto dei dati.");
            }
        }
    }
}
