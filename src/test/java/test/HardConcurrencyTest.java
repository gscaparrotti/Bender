package test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import model.IDish;
import model.Menu;
import model.OrderedDish;
import model.Pair;
import model.Restaurant;

public class HardConcurrencyTest {

    private static final int ADD_THREADS = 5000;
    private static final int READ_THREADS = 5000;
    public IMainController ctrl;

    @Before
    public void init() {
        this.ctrl = MainController.getInstance();
        ctrl.setModel(new Restaurant(), new Menu());
        ctrl.getRestaurant().addTable();
    }

    @Test
    public void test() throws InterruptedException, ExecutionException {
        final ExecutorService ex = Executors.newFixedThreadPool(ADD_THREADS + READ_THREADS);
        final Set<Callable<Integer>> set = new HashSet<>();
        for (int i = 0; i < READ_THREADS; i++) {
            set.add(new Callable<Integer>() {
                @SuppressWarnings("unchecked")
                @Override
                public Integer call() throws ClassNotFoundException, IOException {
                    int result = 0;
                    // this resembles what happens in methods which use
                    // getOrders()
                    final Object obj = ctrl.getRestaurant().getOrders(1);
                    synchronized (ctrl.getRestaurant()) {
                        try {
                            final Map<IDish, Pair<Integer, Integer>> orders = (Map<IDish, Pair<Integer, Integer>>) obj;
                            for (final Entry<IDish, Pair<Integer, Integer>> e : orders.entrySet()) {
                                result += e.getValue().getX();
                            }
                        } catch (final ConcurrentModificationException e) {
                            fail("concurrent modification detected");
                        }
                    }
                    return result;
                }
            });
        }
        for (int i = 0; i < ADD_THREADS; i++) {
            set.add(new Callable<Integer>() {
                @Override
                public Integer call() throws ClassNotFoundException, IOException {
                    // we create all different dishes to maximize the chance of
                    // getting a concurrent modification
                    final IDish newDish = new OrderedDish(Thread.currentThread().toString(), 1, 1);
                    ctrl.getRestaurant().addOrder(1, newDish, 1);
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
        assertTrue("Read orders are more than added orders", total <= ADD_THREADS);
    }

}
