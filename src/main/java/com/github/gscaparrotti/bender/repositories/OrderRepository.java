package com.github.gscaparrotti.bender.repositories;

import com.github.gscaparrotti.bender.entities.Order;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long> {
    List<Order> findByCustomer_workingTable_tableNumber(long tableNumber);
}
