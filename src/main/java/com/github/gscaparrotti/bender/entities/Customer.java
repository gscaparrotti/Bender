package com.github.gscaparrotti.bender.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.UniqueConstraint;

@Entity
@javax.persistence.Table(uniqueConstraints = @UniqueConstraint(columnNames={"workingTable"}))
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Customer {

    @Id
    private String name;
    @OneToOne
    @JoinColumn(name = "workingTable")
    private Table workingTable;
    @ManyToOne
    @JoinColumn(name = "tablec", nullable = false)
    private Table tablec;
    @JsonIgnore
    @OneToMany(mappedBy = "customer")
    private Set<Order> orders;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Table getWorkingTable() {
        return workingTable;
    }

    public void setWorkingTable(Table workingTable) {
        this.workingTable = workingTable;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public Table getTable() {
        return tablec;
    }

    public void setTable(Table table) {
        this.tablec = table;
    }
}
