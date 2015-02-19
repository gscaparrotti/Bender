package model;

import java.io.Serializable;

public interface IMenu extends Serializable{
	
	public void addItems(IDish... items);
	
	public Dish[] getDishesArray();

}
