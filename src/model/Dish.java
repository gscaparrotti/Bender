package model;

import benderUtilities.CheckNull;

public class Dish implements IDish{

	/**
	 * 
	 */
	private static final long serialVersionUID = 562063817725787601L;
	public static final int Fields = 2;
	private String name;
	private double price;
	
	public Dish(String name, double price) {
		CheckNull.checkNull(name);
		this.name=name;
		this.price=price;
	}
	
	public String getName() {
		return this.name;
	}
	
	public double getPrice() {
		return this.price;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		long temp;
		temp = Double.doubleToLongBits(price);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Dish)) {
			return false;
		}
		Dish other = (Dish) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		if (Double.doubleToLongBits(price) != Double
				.doubleToLongBits(other.price)) {
			return false;
		}
		return true;
	}
	
	@Override
	public String toString() {
		return name.toString();
	}
}
