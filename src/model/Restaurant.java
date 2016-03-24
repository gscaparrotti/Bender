package model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 */
public class Restaurant implements IRestaurant {

    /**
     * 
     */
    private static final long serialVersionUID = 6813103235280390095L;
    private final Map<Integer, Map<IDish, Pair<Integer, Integer>>> tables = new HashMap<>();
    private int tablesAmount;
    private static final String ERROR_MESSAGE = "Dati inseriti non corretti. Controllare.";

    @Override
    public int addTable() {
        this.tablesAmount++;
        return this.tablesAmount;
    }

    @Override
    public int removeTable() {
        if (tablesAmount > 0 && !tables.containsKey(tablesAmount)) {
            tablesAmount--;
            return tablesAmount;
        } else {
            throw new IllegalStateException("Il tavolo ha ancora piatti da servire");
        }
    }

    @Override
    public void addOrder(final int table, final IDish item, final int quantity) {
        Objects.requireNonNull(item);
        if (!checkIfCorrect(table, item, quantity)) {
            throw new IllegalArgumentException(ERROR_MESSAGE);
        }
        final Map<IDish, Pair<Integer, Integer>> temp = tables.getOrDefault(table, new HashMap<>());
        if (temp.containsKey(item)) {
            temp.get(item).setX(temp.get(item).getX() + quantity);
        } else {
            temp.put(item, new Pair<Integer, Integer>(quantity, 0));
        }
        tables.put(table, temp);
    }

    @Override
    public void removeOrder(final int table, final IDish item, final int quantity) {
        Objects.requireNonNull(item);
        if (!checkIfCorrect(table, item, quantity) || !tables.containsKey(table) || !tables.get(table).containsKey(item)
                || tables.get(table).get(item).getX() - quantity < 0) {
            throw new IllegalArgumentException(ERROR_MESSAGE);
        }
        if (tables.get(table).get(item).getX() - quantity == 0) {
            tables.get(table).remove(item);
        } else {
            tables.get(table).get(item).setX(tables.get(table).get(item).getX() - quantity);
            if (tables.get(table).get(item).getY() > tables.get(table).get(item).getX()) {
                setOrderAsProcessed(table, item);
            }
        }
        if (tables.get(table).isEmpty()) {
            resetTable(table);
        }
    }

    @Override
    public void setOrderAsProcessed(final int table, final IDish item) {
        Objects.requireNonNull(item);
        if (!checkIfCorrect(table, item, 1) || !tables.containsKey(table) || tables.get(table).get(item) == null) {
            throw new IllegalArgumentException(ERROR_MESSAGE);
        }
        tables.get(table).get(item).setY(tables.get(table).get(item).getX());
    }

    @Override
    public void resetTable(final int table) {
        if (table <= 0) {
            throw new IllegalArgumentException(ERROR_MESSAGE);
        }
        tables.remove(table);
    }

    /*
     * public double getBill(int table){ if(table<=0 ||
     * !tables.containsKey(table)) { return 0.0; } else { double total = 0;
     * Map<IDish, Pair<Integer, Integer>> m = tables.get(table); for(IDish i :
     * m.keySet()) { total += i.getPrice() * m.get(i).getX(); } return total; }
     * }
     */

    @Override
    public int getTablesAmount() {
        return this.tablesAmount;
    }

    @Override
    public Map<IDish, Pair<Integer, Integer>> getOrders(final int table) {
        if (table > 0 && tables.containsKey(table)) {
            return tables.get(table);
        } else {
            return new HashMap<>();
        }
    }

    private boolean checkIfCorrect(final int table, final IDish item, final int quantity) {
        if (table <= 0 || table > tablesAmount || item == null || quantity <= 0) {
            return false;
        }
        if (item.getName().length() <= 0 || item.getPrice() <= 0) { // NOPMD
            return false;
        }
        return true;
    }

}
