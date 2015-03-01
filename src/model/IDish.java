package model;

import java.io.Serializable;

/**
 * @author Giacomo Scaparrotti
 * 
 * An interface which models the basic method for managing a dish.
 *
 */
public interface IDish extends Serializable{
	
	/**
	 * @return The name of this dish.
	 */
	public String getName();
	
	/**
	 * @return The price of this dish.
	 */
	public double getPrice();

}
