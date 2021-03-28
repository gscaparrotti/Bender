package com.github.gscaparrotti.bender.services;

import com.github.gscaparrotti.bender.entities.Dish;
import com.github.gscaparrotti.bender.repositories.DishRepository;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MenuService {

    private final DishRepository dishRepository;

    @Autowired
    public MenuService(final DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }
    
    public Result<Dish> addToMenu(Dish dish) {
        dish = dishRepository.save(dish);
        return new Result<>(dish, Result.ResultType.CREATED);
    }
    
    public Result<Set<Dish>> getMenu() {
        final Set<Dish> menu = new HashSet<>();
        for (final Dish dish : dishRepository.findAll()) {
            menu.add(dish);
        }
        return new Result<>(menu, Result.ResultType.OK);
    }
    
    public Result<Dish> getDish(final String id) {
        return dishRepository.findById(id)
            .map(dish -> new Result<>(dish, Result.ResultType.OK))
            .orElseGet(() -> new Result<>(Result.ResultType.BAD_REQUEST));
    }
    
}
