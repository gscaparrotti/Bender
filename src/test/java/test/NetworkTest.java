package test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Before;
import org.junit.Test;

import controller.IMainController;
import controller.MainController;
import controller.NetworkController;
import model.IDish;
import model.Menu;
import model.Order;
import model.OrderedDish;
import model.Pair;
import model.Restaurant;

public class NetworkTest {
    
    //a high number of threads means a high number of connections, which can cause a "Connection refused"
    //exception when the backlog fills up, depending on the underlying system behaviour 
    //(windows will drop connections causing the exception, while linux will simply ignore the new connections,
    //so it takes longer, if not forever, for them to fail)
    private static final int NET_ADD_THREADS = 100;
    private static final int NET_READ_THREADS = 100;
    public IMainController ctrl;
    public NetworkController net;
    
    @Before
    public void init() {
        this.ctrl = MainController.getInstance();
        ctrl.setModel(new Restaurant(), new Menu());
        ctrl.getRestaurant().addTable();
        final Order order = new Order(1, new OrderedDish(ctrl.getMenu().getDishesArray()[0]), new Pair<>(1, 0));
        ctrl.getRestaurant().addOrder(order.getTable(), order.getDish(), order.getAmounts().getX());
        this.net = new NetworkController(ctrl, 6789);
        net.start();
    }

    @Test
    public void test() throws InterruptedException, ExecutionException {
        final ExecutorService ex = Executors.newFixedThreadPool(NET_ADD_THREADS + NET_READ_THREADS);
        final Set<Callable<Integer>> set = new HashSet<>();
        for (int i = 0; i < NET_READ_THREADS; i++) {
            set.add(new Callable<Integer>() {              
                @SuppressWarnings("unchecked")
                @Override
                public Integer call() throws ClassNotFoundException, IOException {
                    final Object obj = sendMessageAndGetResult("GET TABLE 1");
                    if (!(obj instanceof Map)) {
                        fail("Oggetto ricevuto non corretto");
                    }
                    return ((Map<IDish, Pair<Integer, Integer>>) obj).get(ctrl.getMenu().getDishesArray()[0]).getX();
                }
            });
        }
        for (int i = 0; i < NET_ADD_THREADS; i++) {
            set.add(new Callable<Integer>() {
                @Override
                public Integer call() throws ClassNotFoundException, IOException {
                    final Order order = new Order(1, ctrl.getMenu().getDishesArray()[0], new Pair<>(1, 0));
                    final Object obj = sendMessageAndGetResult(order);
                    if (!(obj.equals("ORDER ADDED CORRECTLY"))) {
                        fail("Oggetto ricevuto non corretto");
                    }
                    return 0;
                }
            });
        }      
        int total = 0;
        final List<Future<Integer>> futures = ex.invokeAll(set);
        for (final Future<Integer> f : futures) {
            final int temp = f.get();
            if (temp > total) {
                total = temp;
            }
        }
        assertTrue("Read orders are more than added orders", total <= NET_ADD_THREADS + 1);
        assertEquals("Added orders amount is wrong", 
                ctrl.getRestaurant().getOrders(1).get(ctrl.getMenu().getDishesArray()[0]).getX(),
                Integer.valueOf(NET_ADD_THREADS + 1));       
    }
    
    private static Object sendMessageAndGetResult(final Object message) throws IOException, ClassNotFoundException {
        Socket socket = new Socket("127.0.0.1", 6789); //NOPMD
        final ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
        oos.writeObject(message);
        //ALWAYS WRITE SOMETHING BEFORE CREATING THE OBJECTINPUTSTREAM!!!
        final ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        final Object obj = ois.readObject();
        socket.close();
        return obj;
    }
}
