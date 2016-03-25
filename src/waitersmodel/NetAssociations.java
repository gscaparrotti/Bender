package waitersmodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A simple concrete {@link IAssociations}.
 * 
 * @param <T> the type which identifies a waiter
 */
public class NetAssociations<T> implements IAssociations<T> {

    private final Map<Integer, Set<T>> associations = new HashMap<>();

    @Override
    public void addAssociation(final int table, final T waiter) {
        final Set<T> addresses = associations.getOrDefault(table, new HashSet<>());
        addresses.add(waiter);
    }

    @Override
    public void deleteAssociation(final int table, final T waiter) {
        final Set<T> addresses = associations.get(table);
        if (addresses != null) {
            addresses.remove(waiter);
        }
    }

    @Override
    public void deleteAllAssociations(final int table) {
        associations.remove(table);
    }

    @Override
    public Collection<T> getAssociation(final int table) {
        return associations.getOrDefault(table, new HashSet<T>());
    }

}
