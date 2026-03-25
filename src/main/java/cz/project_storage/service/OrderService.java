package cz.project_storage.service;

import cz.project_storage.model.*;
import cz.project_storage.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


        double totalPrice = coffee.getPrice() * quantity;
        order.setTotalPrice(totalPrice);
        order.setStatus("COMPLETED");


        OrderItem item = new OrderItem(order, coffee, quantity, (int) coffee.getPrice());
        order.getItems().add(item);

        orderRepository.save(order);
    }

    public List<Order> getOrdersByUsername(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        return orderRepository.findByUserUsername(username);
    }


}