package cz.project_storage.service;

import cz.project_storage.model.*;
import cz.project_storage.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Transactional
    public void processOrder(Long coffeeId, String username) {

        Coffee coffee = coffeeRepository.findById(coffeeId)
                .orElseThrow(() -> new RuntimeException("Coffee with ID " + coffeeId + " not found."));

        if (coffee.getQuantity() <= 0) {
            throw new RuntimeException("Sorry, this coffee is out of stock!");
        }


        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        coffee.setQuantity(coffee.getQuantity() - 1);
        if (coffee.getQuantity() == 0) {
            coffee.setStockStatus("Out of Stock");
        }
        coffeeRepository.save(coffee);

        Order order = new Order();
        order.setUser(user);
        order.setTotalPrice((double) coffee.getPrice());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("COMPLETED");

        OrderItem item = new OrderItem(order, coffee, 1, coffee.getPrice());
        order.addOrderItem(item);

        orderRepository.save(order);

        AuditLog log = new AuditLog();
        log.setUser(username);
        log.setAction("Purchased coffee: " + coffee.getName());
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);
    }

    public List<Order> getOrdersByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return orderRepository.findByUser(user);
    }
}