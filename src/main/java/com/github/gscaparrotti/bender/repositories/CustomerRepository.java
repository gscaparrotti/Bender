package com.github.gscaparrotti.bender.repositories;

import com.github.gscaparrotti.bender.entities.Customer;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, String> {
    Set<Customer> findByTablec_TableNumber(long tablec_tableNumber);
    Set<Customer> findByWorkingTable_TableNumber(long table_tableNumber);
}