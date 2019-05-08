package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.anarsoft.vmlens.concurrent.junit.TestUtil;
import controller.IMainController;
import controller.MainController;
import model.IDish;
import model.Menu;
import model.Order;
import model.OrderedDish;
import model.Pair;
import model.Restaurant;

public class ConcurrencyTest {
    
    private static final int THREAD_COUNT = 1000;
    private IMainController mainController;

    @Before
    public void init() {
        this.mainController = MainController.getInstance();
        mainController.setModel(new Restaurant(), new Menu());
        if (mainController.getMenu().getDishesArray().length == 0) {
            fail("menu not present");
        }
        mainController.getRestaurant().addTable();
        this.addOrder();
    }
    
    @Test
    public void addTestWithRestaurantMethod() throws InterruptedException { //NOPMD
        TestUtil.runMultithreaded(() -> {
            this.addOrder();
        }, THREAD_COUNT);
    }
    
    @Test
    public void addTestWithSynchronizedBlock() throws InterruptedException { //NOPMD
        TestUtil.runMultithreaded(() -> {
            synchronized (mainController.getRestaurant()) {
                for (final Map.Entry<IDish, Pair<Integer, Integer>> entry : mainController.getRestaurant().getOrders(1).entrySet()) {
                    entry.getValue().setX(entry.getValue().getX() + 1);
                }
            }
        }, THREAD_COUNT);
    }
    
    private void addOrder() {
        final Order order = new Order(1, new OrderedDish(mainController.getMenu().getDishesArray()[0]), new Pair<>(1, 0));
        mainController.getRestaurant().addOrder(order.getTable(), order.getDish(), order.getAmounts().getX());
    }
    
    @After
    public void check() {
        assertEquals("Orders amount is wrong", 
                mainController.getRestaurant().getOrders(1).get(this.mainController.getMenu().getDishesArray()[0]).getX(),
                Integer.valueOf(THREAD_COUNT + 1)); //a dish has been added in init
    }

}
