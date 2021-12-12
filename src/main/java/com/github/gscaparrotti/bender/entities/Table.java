package com.github.gscaparrotti.bender.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
@javax.persistence.Table(name = "table_t")
public class Table {

    @Id
    private long tableNumber;
    @JsonIgnore
    @OneToOne
    private CustomerTable customerTable;
    @JsonIgnore
    @OneToOne
    private WorkingTable workingTable;

    public long getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(long tableNumber) {
        this.tableNumber = tableNumber;
    }

    public CustomerTable getCustomerTable() {
        return customerTable;
    }

    public void setCustomerTable(CustomerTable customer) {
        this.customerTable = customer;
    }

    public WorkingTable getWorkingTable() {
        return workingTable;
    }

    public void setWorkingTable(WorkingTable workingTable) {
        this.workingTable = workingTable;
    }
}
