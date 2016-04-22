package waitersmodel;

import java.util.Collection;

/**
 * This interface models a group of methods which permit to create 
 * an association between a table and the waiter (or the waiters) 
 * who served it. This is useful, for example, when you want to notify
 * a waiter that something ordered from a table he served is ready.
 * 
 * @param <T> the type which identifies a waiter
 */
public interface IAssociations<T> {

    /**
     * @param table the table
     * @param waiter the waiter who served that table
     */
    void addAssociation(int table, T waiter);

    /**
     * @param table the table
     * @param waiter the waiter you want to delete from the collection of waiter of that table
     */
    void deleteAssociation(int table, T waiter);

    /**
     * @param table the table you want to delete from the associations collection
     */
    void deleteAllAssociations(int table);

    /**
     * @param table the table served by the returned waiters
     * @return a collection containing all the waiters who served that table
     */
    Collection<T> getAssociation(int table);

}
