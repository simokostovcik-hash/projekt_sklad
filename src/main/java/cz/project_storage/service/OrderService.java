package cz.project_storage.service;

import cz.project_storage.model.*;
import cz.project_storage.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void createQuickOrder(Long coffeeId, Integer quantity, User user) {
        Coffee coffee = coffeeRepository.findById(coffeeId)
                .orElseThrow(() -> new RuntimeException("Coffee not found"));

        if (coffee.getQuantity() < quantity) {
            throw new RuntimeException("Nedostatek balení na skladě!");
        }

        coffee.setQuantity(coffee.getQuantity() - quantity);

        if (coffee.getQuantity() <= 0) {
            coffee.setStockStatus("Out of Stock");
        }
        coffeeRepository.save(coffee);

        Order order = new Order();
        order.setUser(user);

        double totalPrice = 0.0;
        if (coffee.getPrice() != null) {
            totalPrice = coffee.getPrice().multiply(BigDecimal.valueOf(quantity)).doubleValue();
        }
        order.setTotalPrice(totalPrice);
        order.setStatus("COMPLETED");

        int priceAtPurchase = (coffee.getPrice() != null) ? coffee.getPrice().intValue() : 0;
        OrderItem item = new OrderItem(order, coffee, quantity, priceAtPurchase);

        order.getItems().add(item);

        orderRepository.save(order);
    }

    public List<Order> getOrdersByUsername(String username) {
        return orderRepository.findByUserUsername(username);
    }
}