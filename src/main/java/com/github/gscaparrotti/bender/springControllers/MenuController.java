package com.github.gscaparrotti.bender.springControllers;

import com.github.gscaparrotti.bender.entities.Dish;
import com.github.gscaparrotti.bender.repositories.DishRepository;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping({"/api"})
public class MenuController {

    private DishRepository dishRepository;

    @Autowired
    public MenuController(DishRepository dishRepository) {
        this.dishRepository = dishRepository;
    }

    @PostMapping("/menu")
    public ResponseEntity<Dish> addToMenu(@RequestBody Dish dish) {
        dish = dishRepository.save(dish);
        return new ResponseEntity<>(dish, HttpStatus.CREATED);
    }

    @GetMapping("/menu")
    public ResponseEntity<Set<Dish>> getMenu() {
        final Set<Dish> menu = new HashSet<>();
        for (final Dish dish : dishRepository.findAll()) {
            menu.add(dish);
        }
        return new ResponseEntity<>(menu, HttpStatus.OK);
    }

    @GetMapping("/menu/{id}")
    public ResponseEntity<Dish> getDish(@PathVariable String id) {
        return dishRepository.findById(id).map(dish -> new ResponseEntity<>(dish, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }
}
