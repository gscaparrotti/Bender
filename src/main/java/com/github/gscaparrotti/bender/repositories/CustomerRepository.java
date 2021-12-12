package com.github.gscaparrotti.bender.repositories;

import com.github.gscaparrotti.bender.entities.Customer;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {

    Set<Customer> findAllByTablecTableNumber(long tableNumber);

    Set<Customer> findAllByWorkingTableTableNumber(long workingTableNumber);

}