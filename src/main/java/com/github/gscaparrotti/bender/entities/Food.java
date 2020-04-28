package com.github.gscaparrotti.bender.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import javax.persistence.Entity;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.StringIdGenerator.class)
public class Food extends Dish {
}
