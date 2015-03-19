package model;

/**
 * @author Giacomo Scaparrotti
 * 
 * This class is a decorator for a {@link IDish} object.
 * You will likely use it if want to add a little variation to an existing {@link IDish}.
 *
 */
public class Variation implements IDish {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3826515854104382947L;
	private String variationName;
	private double cost;
	private IDish decoratedDish;

	/**
	 * @param variationName The name of the variation
	 * @param cost the cost of the variation
	 * @param item the {@link IDish} you want to decorate
	 * 
	 * Creates a new {@link IDish}, which is the decoration of another {@link IDish}.
	 */
	public Variation(String variationName, double cost, IDish item) {
		if(item == null || variationName==null) {
			throw new NullPointerException();
		}
		this.decoratedDish = item;
		this.variationName = variationName;
		this.cost = cost;
	}

	@Override
	public String getName() {
		return decoratedDish.getName().concat(this.variationName);
	}

	@Override
	public double getPrice() {
		return decoratedDish.getPrice() + this.cost;
	}


}
