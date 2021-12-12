package com.github.gscaparrotti.bender.springControllers;

import com.github.gscaparrotti.bender.entities.Dish;
import com.github.gscaparrotti.bender.services.MenuService;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final MenuService menuService;

    @Autowired
    public MenuController(final MenuService menuService) {
        this.menuService = menuService;
    }

    @PostMapping("/menu")
    public ResponseEntity<Dish> addToMenu(@RequestBody final Dish dish) {
        return ControllerUtils.resultToResponseEntity(this.menuService.addToMenu(dish));
    }

    @GetMapping("/menu")
    public ResponseEntity<Set<Dish>> getMenu() {
        return ControllerUtils.resultToResponseEntity(this.menuService.getMenu());
    }

    @GetMapping("/menu/{id}")
    public ResponseEntity<Dish> getDish(@PathVariable final String id) {
        return ControllerUtils.resultToResponseEntity(this.menuService.getDish(id));
    }
}
