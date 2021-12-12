package com.github.gscaparrotti.bender.repositories;

import com.github.gscaparrotti.bender.entities.Dish;
import org.springframework.data.repository.CrudRepository;

public interface DishRepository extends CrudRepository<Dish, String> {
}
