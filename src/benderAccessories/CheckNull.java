package benderAccessories;

public class CheckNull {
	
	public static void checkNull(Object... obj) {
		for(Object o : obj) {
			if(o==null) {
				throw new NullPointerException();
			}
		}
	}

}
