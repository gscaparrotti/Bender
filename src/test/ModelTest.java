package test;

/*import static org.junit.Assert.*;
import view.TableDialog;
import controller.RestaurantController;
import model.Dish;
import model.Menu;
import model.Restaurant;

public class ModelTest {
	
	public RestaurantController rc = new RestaurantController(Restaurant.getModel(), Menu.getDishesObject());
	
	public void create() {
		rc.attachView(new TableDialog(rc, 1));
		rc.commandAdd(1, new Dish("pizza", 5.0), 2);
		rc.commandAdd(1, new Dish("pizza", 5.0), 1);
		rc.commandAdd(1, new Dish("coca-cola", 1.5), 1);
		rc.commandAdd(3, new Dish("coca-cola", 1.5), 7);
	}
	
	@org.junit.Test
	public void testObservations(){
		this.create();
		assertEquals(16.5, Restaurant.getModel().getBill(1), 0.1);
		assertEquals(10.5, Restaurant.getModel().getBill(3), 0.1);
		assertEquals(0.0, Restaurant.getModel().getBill(2), 0.1);

		rc.commandRemove(1, new Dish("pizza", 5.0), 2);
		assertEquals(6.5, Restaurant.getModel().getBill(1), 0.1);
	}

}*/
