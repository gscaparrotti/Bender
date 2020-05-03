package com.github.gscaparrotti.bender.repositories;

import com.github.gscaparrotti.bender.entities.Order;
import java.util.Set;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
    Set<Order> findByCustomer_workingTable_tableNumber(long tableNumber);
    Set<Order> findByDish_Name(String name);
}
