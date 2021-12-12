package com.github.gscaparrotti.bender.entities;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class WorkingTable {

    @Id
    @GeneratedValue
    private long id;
    @OneToOne
    @JoinColumn(name = "_table")
    private Table table;
    @OneToOne
    private Customer customer;

    public WorkingTable() {

    }

    public WorkingTable(final Table table, final Customer customer) {
        this.table = table;
        this.customer = customer;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Table getTable() {
        return table;
    }

    public void setTable(Table table) {
        this.table = table;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
