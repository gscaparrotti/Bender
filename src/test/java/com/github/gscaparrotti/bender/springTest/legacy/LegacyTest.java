package com.github.gscaparrotti.bender.springTest.legacy;

import com.github.gscaparrotti.bender.legacy.LegacyHelper;
import com.github.gscaparrotti.bender.legacy.SpringMenuAdapter;
import com.github.gscaparrotti.bender.legacy.SpringRestaurantAdapter;
import com.github.gscaparrotti.bender.springControllers.RestaurantController;
import com.github.gscaparrotti.bendermodel.model.Dish;
import com.github.gscaparrotti.bendermodel.model.IDish;
import com.github.gscaparrotti.bendermodel.model.Pair;
import java.util.Map;
import java.util.Objects;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@AutoConfigureTestDatabase
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class LegacyTest {

    /**
     * Verify that assigning a customer to a table actually works.
     * This test is widely commented, also to understand how the other tests are built.
     */
    @Test
    public void simpleCustomerTest() {
        final SpringRestaurantAdapter ctrl = new SpringRestaurantAdapter();
        final RestaurantController springCtrl = LegacyHelper.ctrl(RestaurantController.class);
        ctrl.addTable();
        //there must be one customer associated to table 1 after its addition
        assertEquals(1, Objects.requireNonNull(springCtrl.getCustomers(1).getBody()).size());
        //its name returned by SpringRestaurantAdapter should be an empty string...
        assertEquals("", ctrl.getTableName(1));
        //...while its "real" name should be 'customer1'
        assertEquals("customer1", Objects.requireNonNull(springCtrl.getTable(1).getBody()).getCustomerTable().getCustomer().getName());
        //the customer should be the active one for table 1 (workingTable should not be null)
        assertNotNull(Objects.requireNonNull(springCtrl.getCustomer("customer1").getBody()).getWorkingTable());
        final String name = "newName";
        ctrl.setTableName(1, name);
        //after adding a customer there should be two customer associated to table 1
        assertEquals(2, Objects.requireNonNull(springCtrl.getCustomers(1).getBody()).size());
        //the name of the current customer should be 'newName'
        assertEquals(name, ctrl.getTableName(1));
        //'customer1' should no longer be the current customer for table 1...
        assertNull(Objects.requireNonNull(springCtrl.getCustomer("customer1").getBody()).getWorkingTable());
        //because it should be 'newName'
        assertNotNull(Objects.requireNonNull(springCtrl.getCustomer(name).getBody()).getWorkingTable());
    }

    /**
     * Verify that the same customer cannot be assigned to two tables at the same time
     */
    @Test
    public void sameCustomerTest() {
        final SpringRestaurantAdapter ctrl = new SpringRestaurantAdapter();
        final RestaurantController springCtrl = LegacyHelper.ctrl(RestaurantController.class);
        ctrl.addTable();
        ctrl.addTable();
        final String name = "newName";
        ctrl.setTableName(1, name);
        ctrl.setTableName(2, name);
        assertEquals(name, ctrl.getTableName(1));
        assertNotEquals(name, ctrl.getTableName(2));
        assertNotNull(Objects.requireNonNull(springCtrl.getCustomer(name).getBody()).getWorkingTable());
        assertNotNull(Objects.requireNonNull(springCtrl.getCustomer("customer2").getBody()).getWorkingTable());
        assertNull(Objects.requireNonNull(springCtrl.getCustomer("customer1").getBody()).getWorkingTable());
    }

    /**
     * Verify that a customer can be remove only if it's not the current user for a table
     */
    @Test
    public void customerRemovalTest() {
        final SpringRestaurantAdapter ctrl = new SpringRestaurantAdapter();
        final RestaurantController springCtrl = LegacyHelper.ctrl(RestaurantController.class);
        ctrl.addTable();
        ResponseEntity<Void> removalResult = springCtrl.removeCustomer("customer1", false);
        assertTrue(removalResult.getStatusCode().is4xxClientError());
        assertNotNull(Objects.requireNonNull(springCtrl.getCustomer("customer1").getBody()).getWorkingTable());
        ctrl.setTableName(1, "newName");
        removalResult = springCtrl.removeCustomer("customer1", false);
        assertTrue(removalResult.getStatusCode().is2xxSuccessful());
        assertEquals(1, Objects.requireNonNull(springCtrl.getCustomers(1).getBody()).size());
    }

    @Test
    public void simpleOrderTest() {
        final SpringRestaurantAdapter ctrl = new SpringRestaurantAdapter();
        final SpringMenuAdapter menuCtrl = new SpringMenuAdapter();
        final RestaurantController springCtrl = LegacyHelper.ctrl(RestaurantController.class);
        final IDish newDish = new Dish("myDish", 1, 0);
        menuCtrl.addItems(newDish);
        final IDish[] menu = menuCtrl.getDishesArray();
        assertEquals(1, menu.length);
        assertEquals(menu[0], newDish);
        final String name = "newName";
        ctrl.addTable();
        ctrl.setTableName(1, name);
        ctrl.addOrder(1, menu[0], 1);
        final Map<IDish, Pair<Integer, Integer>> orders = ctrl.getOrders(1);
        assertEquals(1, orders.size());
        assertTrue(orders.containsKey(menu[0]));
        ctrl.setTableName(1, "customer1");
        ResponseEntity<Void> responseEntity = springCtrl.removeCustomer(name, false);
        assertTrue(responseEntity.getStatusCode().is4xxClientError());
        springCtrl.removeOrder(menu[0].getName(), name);
        responseEntity = springCtrl.removeCustomer(name, false);
        assertTrue(responseEntity.getStatusCode().is2xxSuccessful());
        assertEquals("", ctrl.getTableName(1));
    }

}
