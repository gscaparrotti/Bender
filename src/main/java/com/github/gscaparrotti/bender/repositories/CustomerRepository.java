package com.github.gscaparrotti.bender.repositories;

import com.github.gscaparrotti.bender.entities.Customer;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CustomerRepository extends JpaRepository<Customer, String> {

    @Query("select Customer as c from Customer where c.customerTable.table.tableNumber = ?1")
    Set<Customer> findAllByTableNumber(long tableNumber);

    @Query("select Customer as c from Customer where c.workingTable.table.tableNumber = ?1")
    Set<Customer> findAllByWorkingTableNumber(long workingTableNumber);

}