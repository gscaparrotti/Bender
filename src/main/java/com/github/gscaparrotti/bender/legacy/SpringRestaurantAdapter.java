package com.github.gscaparrotti.bender.legacy;

import com.github.gscaparrotti.bender.entities.Customer;
import com.github.gscaparrotti.bender.entities.Dish;
import com.github.gscaparrotti.bender.entities.Drink;
import com.github.gscaparrotti.bender.entities.Food;
import com.github.gscaparrotti.bender.entities.Order;
import com.github.gscaparrotti.bender.entities.Table;
import com.github.gscaparrotti.bender.services.MenuService;
import com.github.gscaparrotti.bender.services.RestaurantService;
import com.github.gscaparrotti.bendermodel.model.IDish;
import com.github.gscaparrotti.bendermodel.model.IRestaurant;
import com.github.gscaparrotti.bendermodel.model.OrderedDish;
import com.github.gscaparrotti.bendermodel.model.Pair;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.github.gscaparrotti.bender.legacy.LegacyHelper.ctrl;
import static com.github.gscaparrotti.bender.legacy.LegacyHelper.ifBodyNotNull;
import static com.github.gscaparrotti.bender.services.RestaurantService.DEFAULT_CUSTOMER_PREFIX;

public class SpringRestaurantAdapter implements IRestaurant {
    
    @Override
    public int addTable() {
        return ifBodyNotNull(getController().addTable(new Table()), table -> {
            final Customer defaultCustomer = new Customer();
            defaultCustomer.setName(DEFAULT_CUSTOMER_PREFIX + table.getTableNumber());
            defaultCustomer.setTable(table);
            defaultCustomer.setWorkingTable(table);
            getController().addCustomer(defaultCustomer);
            return (int) table.getTableNumber();
        }, () -> -1);
    }

    @Override
    public int removeTable() {
        final int lastTableNumber = getTablesAmount();
        return ifBodyNotNull(getController().getCustomers(lastTableNumber), customers -> {
            customers.forEach(customer -> getController().removeCustomer(customer.getName(), true));
            getController().removeTable(lastTableNumber, false);
            return getTablesAmount();
        }, this::getTablesAmount);
    }

    @Override
    public void addOrder(int table, IDish item, int quantity) {
        ifBodyNotNull(getController().getTable(table), foundTable -> {
            final Order order = new Order();
            order.setCustomer(foundTable.getCustomer());
            final Dish dish = ifBodyNotNull(ctrl(MenuService.class).getDish(item.getName()), d -> d, () -> {
                final Dish temp = item.getFilterValue() == 0 ? new Drink() : new Food();
                temp.setName(item.getName());
                temp.setPrice(item.getPrice());
                temp.setTemporary(true);
                return temp;
            });
            order.setDish(dish);
            order.setAmount(quantity);
            if (item instanceof OrderedDish) {
                order.setTime(((OrderedDish) item).getTime());
            }
            getController().addOrder(order, false);
            return null;
        }, LegacyHelper.nullSupplier());
    }

    @Override
    public void removeOrder(int table, IDish item, int quantity) {
        ifBodyNotNull(getController().getTable(table), foundTable -> {
            getController().removeOrder(item.getName(), foundTable.getCustomer().getName());
            return null;
        }, LegacyHelper.nullSupplier());
    }

    @Override
    public void setOrderAsProcessed(int table, IDish item) {
        final Set<Order> orders = ifBodyNotNull(getController().getOrders((long) table), o -> o, Collections::emptySet);
        for (final Order order : orders) {
            if (order.getDish().getName().equals(item.getName())) {
                getController().addOrder(order, true);
            }
        }
    }

    @Override
    public Map<IDish, Pair<Integer, Integer>> getOrders(int table) {
        return ifBodyNotNull(getController().getOrders((long) table), orders -> {
            final Map<IDish, Pair<Integer, Integer>> result = new HashMap<>();
            for (final Order order : orders) {
                final Dish newDish = order.getDish();
                final IDish dish = new CustomOrderedDish(newDish.getName(), newDish.getPrice(), newDish instanceof Drink ? 0 : 1, order.getTime());
                if (result.containsKey(dish)) {
                    final Pair<Integer, Integer> orderDetail = result.get(dish);
                    orderDetail.setX(orderDetail.getX() + order.getAmount());
                    if (order.isServed()) {
                        orderDetail.setY(orderDetail.getY() + order.getAmount());
                    }
                } else {
                    result.put(dish, new Pair<>(order.getAmount(), order.isServed() ? order.getAmount() : 0));
                }
            }
            return result;
        }, Collections::emptyMap);
    }

    @Override
    public void resetTable(int table) {
        ifBodyNotNull(getController().getOrders((long) table), orders -> {
            for (final Order order : orders) {
                getController().removeOrder(order.getId());
            }
            return null;
        }, LegacyHelper.nullSupplier());
    }

    @Override
    public int getTablesAmount() {
        return ifBodyNotNull(getController().getTables(), Set::size, () -> -1);
    }

    @Override
    public void setTableName(int tableNumber, String name) {
        final Customer customer = new Customer();
        customer.setName(name != null ? name : DEFAULT_CUSTOMER_PREFIX + tableNumber);
        ifBodyNotNull(getController().getTable(tableNumber), table -> {
            customer.setTable(table);
            customer.setWorkingTable(table);
            getController().addCustomer(customer);
            return null;
        }, LegacyHelper.nullSupplier());

    }

    @Override
    public String getTableName(int tableNumber) {
        return ifBodyNotNull(getController().getTable(tableNumber), table ->
                table.getCustomer() != null && !table.getCustomer().getName().equals(DEFAULT_CUSTOMER_PREFIX + table.getTableNumber()) ? table.getCustomer().getName() : "", () -> "");
    }

    @Deprecated
    @Override
    public Map<Integer, String> getAllNames() {
        return ifBodyNotNull(getController().getTables(), tables -> {
            final Map<Integer, String> names = new HashMap<>();
            tables.forEach(table -> names.put((int) table.getTableNumber(),
                    !table.getCustomer().getName().equals(DEFAULT_CUSTOMER_PREFIX + table.getTableNumber()) ? table.getCustomer().getName() : null));
            return names;
        }, Collections::emptyMap);
    }

    private static RestaurantService getController() {
        return ctrl(RestaurantService.class);
    }

    private static class CustomOrderedDish extends OrderedDish {

        private final Date time;

        public CustomOrderedDish(final String newName, final double newPrice, final int filter, final Date time) {
            super(newName, newPrice, filter);
            this.time = time != null ? new Date(time.getTime()) : new Date(0);
        }

        @Override
        public Date getTime() {
            return this.time;
        }
    }

}
