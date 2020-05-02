package com.github.gscaparrotti.bender.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.util.Set;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public abstract class Dish {

    @Id
    private String name;
    private double price;
    @JsonIgnore
    @OneToMany(mappedBy = "dish")
    private Set<Order> orders;

    @Transient
    @JsonProperty("filter")
    public int getLegacyFilterValue() {
        return this instanceof Food ? 1 : 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }
}
