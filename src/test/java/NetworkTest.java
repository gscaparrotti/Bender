import static org.junit.Assert.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Test;

import controller.IMainController;
import controller.MainController;
import controller.NetworkController;
import model.IMenu;
import model.Menu;
import model.Restaurant;

public class NetworkTest {

    @Test
    public void test() {
        final IMainController ctrl = MainController.getInstance();
        ctrl.setModel(new Restaurant(), new Menu());
        NetworkController net = new NetworkController(ctrl, 6789);
        net.start();
        final ExecutorService ex = Executors.newFixedThreadPool(30);
        final Set<Callable<Void>> set = new HashSet<>();
        for (int i = 0; i < 30; i++) {
            set.add(new Callable<Void>() {              
                @Override
                public Void call() throws Exception {
                    try {
                        Socket socket = new Socket("127.0.0.1", 6789);
                        ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                        oos.writeObject("GET MENU");
                        //ALWAYS WRITE SOMETHING BEFORE CREATING THE OBJECTINPUTSTREAM!!!
                        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                        Object obj = ois.readObject();
                        socket.close();
                        if (!(obj instanceof IMenu)) {
                            fail("Oggetto ricevuto non corretto");
                        }
                    } catch (IOException | ClassNotFoundException e) {
                        fail(e.getMessage() + e.toString());
                    }
                    return null;
                }
            });
        }
        try {
            ex.invokeAll(set);
        } catch (InterruptedException e) {
            fail();
        }
    }
}
