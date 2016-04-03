package waitersmodel;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.danilopianini.concurrency.FastReadWriteLock;

/**
 * A simple concrete {@link IAssociations}.
 * 
 * @param <T> the type which identifies a waiter
 */
public class Associations<T> implements IAssociations<T> {

    private final Map<Integer, Set<T>> associationsMap = new HashMap<>();
    private final FastReadWriteLock lock = new FastReadWriteLock();

    @Override
    public void addAssociation(final int table, final T waiter) {
        lock.write();
        try {
            final Set<T> addresses = associationsMap.getOrDefault(table, new HashSet<>());
            addresses.add(waiter);
        } finally {
            lock.release(); 
        }
    }

    @Override
    public void deleteAssociation(final int table, final T waiter) {
        lock.write();
        try {
            final Set<T> addresses = associationsMap.get(table);
            if (addresses != null) {
                addresses.remove(waiter);
            }
        } finally {
            lock.release();
        }
    }

    @Override
    public void deleteAllAssociations(final int table) {
        lock.write();
        try {
            associationsMap.remove(table);
        } finally {
            lock.release();
        }
    }

    @Override
    public Collection<T> getAssociation(final int table) {
        lock.read();
        try {
            return associationsMap.getOrDefault(table, new HashSet<T>()); 
        } finally {
            lock.release();
        }
    }

}
