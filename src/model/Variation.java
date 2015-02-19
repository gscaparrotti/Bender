package model;

public class Variation implements IDish {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3826515854104382947L;
	private String variationName;
	private double cost;
	private IDish decoratedDish;

	public Variation(String variationName, double cost, IDish item) {
		if(item == null) {
			throw new NullPointerException();
		}
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
