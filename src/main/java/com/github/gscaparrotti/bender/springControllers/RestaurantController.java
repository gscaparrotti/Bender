package com.github.gscaparrotti.bender.springControllers;

import com.github.gscaparrotti.bender.entities.Customer;
import com.github.gscaparrotti.bender.entities.Order;
import com.github.gscaparrotti.bender.entities.Table;
import com.github.gscaparrotti.bender.services.RestaurantService;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping({"/api"})
public class RestaurantController {

    private final RestaurantService restaurantService;

    @Autowired
    public RestaurantController(final RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @PostMapping("/customers")
    public ResponseEntity<Customer> addCustomer(@RequestBody final Customer customer) {
        return ControllerUtils.resultToResponseEntity(this.restaurantService.addCustomer(customer));
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable final String id) {
        return ControllerUtils.resultToResponseEntity(this.restaurantService.getCustomer(id));
    }

    @GetMapping("/customers")
    public ResponseEntity<Set<Customer>> getCustomers(@RequestParam(defaultValue = "0") final long tableNumber) {
        return ControllerUtils.resultToResponseEntity(this.restaurantService.getCustomers(tableNumber));
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> removeCustomer(@PathVariable final String id, @RequestParam(defaultValue = "false") final boolean force) {
        return ControllerUtils.resultToResponseEntity(this.restaurantService.removeCustomer(id, force));
    }

    @PostMapping("/tables")
    public ResponseEntity<Table> addTable(@RequestBody final Table table) {
        return ControllerUtils.resultToResponseEntity(this.restaurantService.addTable(table));
    }

    @DeleteMapping("/tables/{id}")
    public ResponseEntity<Void> removeTable(@PathVariable final long id, @RequestParam(defaultValue = "false") final boolean reset) {
        return ControllerUtils.resultToResponseEntity(this.restaurantService.removeTable(id, reset));
    }

    @GetMapping("/tables/{id}")
    public ResponseEntity<Table> getTable(@PathVariable final long id) {
        return ControllerUtils.resultToResponseEntity(this.restaurantService.getTable(id));
    }

    @GetMapping("/tables")
    public ResponseEntity<Set<Table>> getTables() {
        return ControllerUtils.resultToResponseEntity(this.restaurantService.getTables());
    }

    @PostMapping("/orders")
    public ResponseEntity<Order> addOrder(@RequestBody final Order order, @RequestParam(defaultValue = "false") final boolean served) {
        return ControllerUtils.resultToResponseEntity(this.restaurantService.addOrder(order, served));
    }

    @DeleteMapping("/orders")
    public ResponseEntity<Void> removeOrder(@RequestParam final String dishName, @RequestParam final String customerName) {
        return ControllerUtils.resultToResponseEntity(this.restaurantService.removeOrder(dishName, customerName));
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> removeOrder(@PathVariable final long id) {
        return ControllerUtils.resultToResponseEntity(this.restaurantService.removeOrder(id));
    }

    @GetMapping("/orders")
    public ResponseEntity<Set<Order>> getOrders(@RequestParam(required = false) final Long tableNumber) {
        return ControllerUtils.resultToResponseEntity(this.restaurantService.getOrders(tableNumber));
    }

}
