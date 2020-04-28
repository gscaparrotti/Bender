package com.github.gscaparrotti.bender.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.UniqueConstraint;

@Entity
@javax.persistence.Table(name = "order_t", uniqueConstraints = @UniqueConstraint(columnNames={"dish", "customer", "time"}))
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "dish", nullable = false)
    private Dish dish;
    @ManyToOne
    @JoinColumn(name = "customer", nullable = false)
    private Customer customer;
    @Column(name = "time")
    private Date time;
    private int amount;
    private boolean served;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Dish getDish() {
        return dish;
    }

    public void setDish(Dish dish) {
        this.dish = dish;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public boolean isServed() {
        return served;
    }

    public void setServed(boolean served) {
        this.served = served;
    }
}
