package benderUtilities;

/**
 * @author Giacomo Scaparrotti
 * 
 * This is an utility class. By now it contains only one method, checkNull.
 *
 */
public class CheckNull {
	
	/**
	 * @param obj
	 * 
	 * This is an utility method used the check if the arguments are equals to null or not.
	 * Its use is widespread in most Bender's classes.
	 */
	public static void checkNull(Object... obj) {
		for(Object o : obj) {
			if(o==null) {
				throw new NullPointerException();
			}
		}
	}

}
