package com.github.gscaparrotti.bender.services;

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
import java.util.stream.StreamSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RestaurantService {

    public static final String DEFAULT_CUSTOMER_PREFIX = "customer";

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final TableRepository tableRepository;
    private final DishRepository dishRepository;

    @Autowired
    public RestaurantService(CustomerRepository customerRepository, OrderRepository orderRepository,
                                TableRepository tableRepository, DishRepository dishRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.tableRepository = tableRepository;
        this.dishRepository = dishRepository;
    }
    
    public Result<Customer> addCustomer(final Customer customer) {
        //an already existing customer cannot be assigned to another table
        final Optional<Customer> repoCustomer = customerRepository.findById(customer.getName());
        if (repoCustomer.isPresent()) {
            if (repoCustomer.get().getTable().getTableNumber() != customer.getTable().getTableNumber()) {
                return new Result<>(Result.ResultType.CONFLICT);
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
        return new Result<>(customerRepository.save(customer), Result.ResultType.CREATED);
    }
    
    public Result<Customer> getCustomer(final String id) {
        return customerRepository.findById(id).map(customer -> new Result<>(customer, Result.ResultType.OK)).orElseGet(() -> new Result<>(Result.ResultType.BAD_REQUEST));
    }
    
    public Result<Set<Customer>> getCustomers(final long tableNumber) {
        final Set<Customer> customers = new HashSet<>();
        if (tableNumber > 0) {
            customers.addAll(customerRepository.findByTablec_TableNumber(tableNumber));
        } else {
            customers.addAll(customerRepository.findAll());
        }
        return new Result<>(customers, Result.ResultType.OK);
    }
    
    public Result<Void> removeCustomer(final String id, final boolean force) {
        if (customerRepository.findById(id).isPresent()) {
            //a customer should be deleted only if it's not currently associated to a table, or if the user wants to force the operation
            if (customerRepository.findById(id).get().getWorkingTable() == null || force) {
                customerRepository.deleteById(id);
                return new Result<>(Result.ResultType.OK);
            } else {
                return new Result<>(Result.ResultType.CONFLICT);
            }
        }
        return new Result<>(Result.ResultType.BAD_REQUEST);
    }
    
    public Result<Table> addTable(final Table table) {
        if (table.getTableNumber() == 0) {
            table.setTableNumber(tableRepository.count() + 1);
        }
        return new Result<>(tableRepository.save(table), Result.ResultType.CREATED);
    }
    
    public Result<Void> removeTable(final long id, final boolean reset) {
        if (tableRepository.findById(id).isPresent()) {
            if (reset) {
                Table table = tableRepository.findById(id).get();
                table.setCustomer(null);
                table = tableRepository.save(table);
                final Set<Customer> customers = customerRepository.findByTablec_TableNumber(id);
                customers.forEach(customer -> {
                    orderRepository.deleteAll(customer.getOrders());
                    customerRepository.save(customer);
                });
                customerRepository.deleteAll(customers);
                customerRepository.flush();
                final Customer defaultCustomer = new Customer();
                defaultCustomer.setName(DEFAULT_CUSTOMER_PREFIX + table.getTableNumber());
                defaultCustomer.setTable(table);
                defaultCustomer.setWorkingTable(table);
                customerRepository.saveAndFlush(defaultCustomer);
            } else {
                tableRepository.deleteById(id);
            }
            return new Result<>(Result.ResultType.OK);
        }
        return new Result<>(Result.ResultType.BAD_REQUEST);
    }

    public Result<Table> getTable(final long id) {
        return tableRepository.findById(id)
            .map(table -> new Result<>(table, Result.ResultType.OK))
            .orElseGet(() -> new Result<>(Result.ResultType.BAD_REQUEST));
    }
    
    public Result<Set<Table>> getTables() {
        final Set<Table> tables = new HashSet<>();
        tableRepository.findAll().forEach(tables::add);
        return new Result<>(tables, Result.ResultType.OK);
    }
    
    public Result<Order> addOrder(Order order, final boolean served) {
        order.setServed(served);
        if (!order.isServed() && order.getTime() == null) {
            order.setTime(new Date());
        }
        if (!dishRepository.existsById(order.getDish().getName())) {
            order.getDish().setTemporary(true);
            order.setDish(dishRepository.save(order.getDish()));
        }
        order = orderRepository.save(order);
        return new Result<>(order, Result.ResultType.CREATED);
    }
    
    public Result<Void> removeOrder(final String dishName, final String customerName) {
        final Optional<Order> order = StreamSupport.stream(orderRepository.findAll().spliterator(), false)
            .filter(o -> o.getDish().getName().equals(dishName) && o.getCustomer().getName().equals(customerName))
            //delete the unprocessed orders first
            .min((o1, o2) -> Boolean.compare(o1.isServed(), o2.isServed()));
        if (order.isPresent()) {
            orderRepository.delete(order.get());
            if (orderRepository.findByDish_Name(dishName).isEmpty()) {
                dishRepository.findById(dishName).ifPresent(dish -> {
                    if (dish.isTemporary()) {
                        dishRepository.deleteById(dishName);
                    }
                });
            }
            return new Result<>(Result.ResultType.NO_CONTENT);
        }
        return new Result<>(Result.ResultType.BAD_REQUEST);
    }
    
    public Result<Void> removeOrder(final long id) {
        final Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            return removeOrder(order.get().getDish().getName(), order.get().getCustomer().getName());
        }
        return new Result<>(Result.ResultType.BAD_REQUEST);
    }
    
    public Result<Set<Order>> getOrders(final Long tableNumber) {
        Set<Order> orders = new HashSet<>();
        (tableNumber == null ? orderRepository.findAll() : orderRepository.findByCustomer_workingTable_tableNumber(tableNumber)).forEach(orders::add);
        return new Result<>(orders, Result.ResultType.OK);
    }
    
}
