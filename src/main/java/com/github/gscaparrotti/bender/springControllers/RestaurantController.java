package com.github.gscaparrotti.bender.springControllers;

import com.github.gscaparrotti.bender.entities.Customer;
import com.github.gscaparrotti.bender.entities.Order;
import com.github.gscaparrotti.bender.entities.Table;
import com.github.gscaparrotti.bender.repositories.CustomerRepository;
import com.github.gscaparrotti.bender.repositories.DishRepository;
import com.github.gscaparrotti.bender.repositories.OrderRepository;
import com.github.gscaparrotti.bender.repositories.TableRepository;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
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

    private CustomerRepository customerRepository;
    private OrderRepository orderRepository;
    private TableRepository tableRepository;
    private DishRepository dishRepository;
    private PlatformTransactionManager transactionManager;

    @Autowired
    public RestaurantController(CustomerRepository customerRepository, OrderRepository orderRepository,
                                TableRepository tableRepository, DishRepository dishRepository,
                                PlatformTransactionManager transactionManager) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.tableRepository = tableRepository;
        this.dishRepository = dishRepository;
        this.transactionManager = transactionManager;
    }

    @PostMapping("/customers")
    public ResponseEntity<Customer> addCustomer(@RequestBody Customer customer) {
        //the transaction is managed manually, so an exception can be caught by CustomExceptionHandler
        //(using @Transactional the exception is not thrown inside this object, but inside the proxy)
        return transactional(() -> {
            //an already existing customer cannot be assigned to another table
            if (customerRepository.findById(customer.getName()).isPresent()) {
                if (customerRepository.findById(customer.getName()).get().getTable().getTableNumber() != customer.getTable().getTableNumber()) {
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
            }
            //if we're changing the customer of a certain table we must remove the previous customer from the table
            if (customer.getWorkingTable() != null) {
                customerRepository.findByWorkingTable_TableNumber(customer.getTable().getTableNumber()).forEach(c -> {
                    c.setWorkingTable(null);
                    //flush is needed in order to be certain to execute this save before the next one in the transaction (it's not needed if we're not in a transaction)
                    customerRepository.saveAndFlush(c);
                });
            }
            final Date date = new Date();
            customerRepository.findById(customer.getName()).ifPresent(reloadedCustomer -> reloadedCustomer.getOrders().forEach(order -> {
                order.setTime(new Date(date.getTime()));
                //two orders for the same customer and the same dish cannot have the same time
                date.setTime(date.getTime() + 1);
            }));
            return new ResponseEntity<>(customerRepository.save(customer), HttpStatus.CREATED);
        });
    }

    @GetMapping("/customers/{id}")
    public ResponseEntity<Customer> getCustomer(@PathVariable String id) {
        return customerRepository.findById(id).map(customer -> new ResponseEntity<>(customer, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @GetMapping("/customers")
    public ResponseEntity<Set<Customer>> getCustomers(@RequestParam(defaultValue = "0") long tableNumber) {
        final Set<Customer> customers = new HashSet<>();
        if (tableNumber > 0) {
            customers.addAll(customerRepository.findByTablec_TableNumber(tableNumber));
        } else {
            customers.addAll(customerRepository.findAll());
        }
        return new ResponseEntity<>(customers, HttpStatus.OK);
    }

    @DeleteMapping("/customers/{id}")
    public ResponseEntity<Void> removeCustomer(@PathVariable String id, @RequestParam(defaultValue = "false") boolean force) {
        if (customerRepository.findById(id).isPresent()) {
            //a customer should be deleted only if it's not currently associated to a table, or if the user wants to force the operation
            if (customerRepository.findById(id).get().getWorkingTable() == null || force) {
                customerRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.CONFLICT);
            }
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/tables")
    public ResponseEntity<Table> addTable(@RequestBody Table table) {
        if (table.getTableNumber() == 0) {
            table.setTableNumber(tableRepository.count() + 1);
        }
        return new ResponseEntity<>(tableRepository.save(table), HttpStatus.CREATED);
    }

    @DeleteMapping("/tables/{id}")
    public ResponseEntity<Void> removeTable(@PathVariable long id) {
        if (tableRepository.findById(id).isPresent()) {
            tableRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/tables/{id}")
    public ResponseEntity<Table> getTable(@PathVariable long id) {
        return tableRepository.findById(id).map(table -> new ResponseEntity<>(table, HttpStatus.OK)).orElseGet(() -> new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @GetMapping("/tables")
    public ResponseEntity<Set<Table>> getTables() {
        final Set<Table> tables = new HashSet<>();
        tableRepository.findAll().forEach(tables::add);
        return new ResponseEntity<>(tables, HttpStatus.OK);
    }

    @PostMapping("/orders")
    public ResponseEntity<Order> addOrder(@RequestBody Order order, @RequestParam(defaultValue = "false") boolean served) {
        order.setServed(served);
        if (!order.isServed() && order.getTime() == null) {
            order.setTime(new Date());
        }
        if (!dishRepository.existsById(order.getDish().getName())) {
            order.getDish().setTemporary(true);
            order.setDish(dishRepository.save(order.getDish()));
        }
        order = orderRepository.save(order);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @DeleteMapping("/orders")
    public ResponseEntity<Void> removeOrder(@RequestParam String dishName, @RequestParam String customerName) {
        final Optional<Order> order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
            .filter(o -> o.getDish().getName().equals(dishName) && o.getCustomer().getName().equals(customerName))
            //delete the unprocessed orders first
            .min((o1, o2) -> Boolean.compare(o1.isServed(), o2.isServed()))
            .or(Optional::empty);
        if (order.isPresent()) {
            orderRepository.delete(order.get());
            if (orderRepository.findByDish_Name(dishName).isEmpty()) {
                dishRepository.findById(dishName).ifPresent(dish -> {
                    if (dish.isTemporary()) {
                        dishRepository.deleteById(dishName);
                    }
                });
            }
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/orders/{id}")
    public ResponseEntity<Void> removeOrder(@PathVariable long id) {
        final Optional<Order> order = orderRepository.findById(id).or(Optional::empty);
        if (order.isPresent()) {
            return removeOrder(order.get().getDish().getName(), order.get().getCustomer().getName());
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/orders")
    public ResponseEntity<Set<Order>> getOrders(@RequestParam(required = false) Long tableNumber) {
        Set<Order> orders = new HashSet<>();
        (tableNumber == null ? orderRepository.findAll() : orderRepository.findByCustomer_workingTable_tableNumber(tableNumber)).forEach(orders::add);
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    private <T> T transactional(final Supplier<T> supplier) {
        return new TransactionTemplate(transactionManager).execute(status -> supplier.get());
    }

}
