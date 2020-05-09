package com.github.gscaparrotti.bender.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
@javax.persistence.Table(name = "table_t")
public class Table {

    @Id
    private long tableNumber;
    @JsonIgnore
    @OneToOne(mappedBy = "workingTable")
    private Customer customer;
    @JsonIgnore
    @OneToMany(mappedBy = "tablec")
    private Set<Customer> allCustomers;

    public long getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(long tableNumber) {
        this.tableNumber = tableNumber;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Set<Customer> getAllCustomers() {
        return allCustomers;
    }

    public void setAllCustomers(Set<Customer> allCustomers) {
        this.allCustomers = allCustomers;
    }
}
