package model;

import java.io.Serializable;

/**
 *
 * @param <X>
 *            the first element's type
 * @param <Y>
 *            the second element's type
 * 
 *            A standard generic Pair<X,Y>, with getters, hashCode, equals, and
 *            toString well implemented. X and Y are modifiable after creating
 *            the object.
 */
public class Pair<X, Y> implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 4773401598597156326L;
    private X x;
    private Y y;

    /**
     * @param newx
     *            the X value
     * @param newy
     *            the Y value
     */
    public Pair(final X newx, final Y newy) {
        super();
        this.x = newx;
        this.y = newy;
    }

    /**
     * @return the X
     */
    public X getX() {
        return x;
    }

    /**
     * @return the Y
     */
    public Y getY() {
        return y;
    }

    /**
     * @param newx
     *            The new X
     */
    public void setX(final X newx) {
        this.x = newx;
    }

    /**
     * @param newy
     *            The new Y
     */
    public void setY(final Y newy) {
        this.y = newy;
    }

    @Override
    public int hashCode() {
        final int prime = 31; // NOPMD
        int result = 1;
        result = prime * result + ((x == null) ? 0 : x.hashCode());
        result = prime * result + ((y == null) ? 0 : y.hashCode());
        return result;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Pair other = (Pair) obj;
        if (x == null) {
            if (other.x != null) {
                return false;
            }
        } else if (!x.equals(other.x)) {
            return false;
        }
        if (y == null) {
            if (other.y != null) {
                return false;
            }
        } else if (!y.equals(other.y)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "x=" + x + ", y=" + y;
    }

}
