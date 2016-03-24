package utilities;

/**
 * 
 *         This is an utility class. By now it contains only one method,
 *         checkNull.
 *
 */
public final class CheckNull {
    private CheckNull() { };

    /**
     * @param obj
     * 
     *            This is an utility method used the check if the arguments are
     *            equals to null or not. This method should only be used when a null
     *            object is not a possible options, but is the result of a PROGRAMMING ERROR.
     *            This method will halt the execution of the program if a parameter is null.
     */
    public static void checkNull(final Object... obj) {
        for (final Object o : obj) {
            assert o != null : "Lascia ogni speranza, è andato tutto a puttane";
        }
    }

}
