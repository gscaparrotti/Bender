package model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Giacomo Scaparrotti
 *
 */
public class Restaurant implements IRestaurant {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6813103235280390095L;
	private final Map<Integer, Map<IDish, Pair<Integer, Integer>>> tables = new HashMap<>();
	private int tablesAmount = 0;
	private final static String errorMessage = "Dati inseriti non corretti. Controllare.";
	
	/**
	 * Creates a new empty {@link Restaurant}
	 */
	public Restaurant() {
	}
	
	@Override
	public int addTable() {
		this.tablesAmount++;
		return this.tablesAmount;
	}
	
	@Override
	public int removeTable() {
		if(tablesAmount>0 && !tables.containsKey(tablesAmount)) {
			tablesAmount--;
			return tablesAmount;
		} else {
			throw new IllegalStateException();
		}
	}

	@Override
	public void addOrder(int table, IDish item, int quantity) {
		Objects.requireNonNull(item);
		if (!checkIfCorrect(table, item, quantity)) {
			throw new IllegalArgumentException(errorMessage);
		}
		Map<IDish, Pair<Integer, Integer>> temp = tables.getOrDefault(table, new HashMap<>());
		if (temp.containsKey(item)) {
			temp.get(item).setX(temp.get(item).getX() + quantity);
		} else {
			temp.put(item, new Pair<Integer, Integer>(quantity, 0));
		}
		tables.put(table, temp);
	}

	@Override
	public void removeOrder(int table, IDish item, int quantity) {
		Objects.requireNonNull(item);
		if (!checkIfCorrect(table, item, quantity) || !tables.containsKey(table) 
			|| !tables.get(table).containsKey(item) || tables.get(table).get(item).getX() - quantity < 0) {
			throw new IllegalArgumentException(errorMessage);
		}
		if (tables.get(table).get(item).getX() - quantity == 0) {
			tables.get(table).remove(item);
		} else {
			tables.get(table).get(item).setX(tables.get(table).get(item).getX() - quantity);
			if(tables.get(table).get(item).getY() > tables.get(table).get(item).getX()) {
				setOrderAsProcessed(table, item);
			}
		}
		if(tables.get(table).isEmpty()) {
			resetTable(table);
		}
	}
	
	@Override
	public void setOrderAsProcessed(int table, IDish item) {
		Objects.requireNonNull(item);
		if (!checkIfCorrect(table, item, 1) || !tables.containsKey(table) || tables.get(table).get(item) == null) {
			throw new IllegalArgumentException(errorMessage);
		}
		tables.get(table).get(item).setY(tables.get(table).get(item).getX());
		
	}
	
	@Override
	public void resetTable(int table) {
		if(table<=0) {
			throw new IllegalArgumentException(errorMessage);
		}
		tables.remove(table);	
	}

	/*public double getBill(int table){
		if(table<=0 || !tables.containsKey(table)) {
			return 0.0;
		} else {
			double total = 0;
			Map<IDish, Pair<Integer, Integer>> m = tables.get(table);
				for(IDish i : m.keySet()) {
					total += i.getPrice() * m.get(i).getX();
				}
			return total;
		}
	}*/
	
	@Override
	public int getTablesAmount() {
		return this.tablesAmount;
	}
	
	@Override
	public Map<IDish, Pair<Integer, Integer>> getOrders(int table) {
		if(table>0 && tables.containsKey(table)) {
			return tables.get(table);
		} else {
			return new HashMap<>();
		}
	}
	
	private boolean checkIfCorrect(int table, IDish item, int quantity) {
		if(table<=0 || table>tablesAmount || item==null || quantity<=0) {
			return false;
		}
		if(item.getName().length()<=0 || item.getPrice()<=0) {
			return false;
		}
		return true;
	}

}
