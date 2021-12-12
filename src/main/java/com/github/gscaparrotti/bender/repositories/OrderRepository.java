package com.github.gscaparrotti.bender.repositories;

import com.github.gscaparrotti.bender.entities.Order;
import java.util.Set;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {

    @Query("select Order as o from Order where o.customer.workingTable.table.tableNumber = ?1")
    Set<Order> findAllByTableNumber(long tableNumber);

    Set<Order> findAllByDishName(String name);

}
