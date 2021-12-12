package com.github.gscaparrotti.bender.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class Customer {

    @Id
    private String name;
    @OneToOne
    private WorkingTable workingTable;
    @OneToOne
    private CustomerTable customerTable;
    @JsonIgnore
    @OneToMany(mappedBy = "customer")
    private Set<Order> orders;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WorkingTable getWorkingTable() {
        return workingTable;
    }

    public void setWorkingTable(WorkingTable workingTable) {
        this.workingTable = workingTable;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public CustomerTable getCustomerTable() {
        return customerTable;
    }

    public void setCustomerTable(CustomerTable customerTable) {
        this.customerTable = customerTable;
    }
}
