package cz.project_storage.service;

import cz.project_storage.model.Order;
import cz.project_storage.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DemoPaymentService {
    private final OrderRepository orders;
    public DemoPaymentService(OrderRepository orders) { this.orders = orders; }

    @Transactional
    public Order completeDemoPayment(Long id, String token) {
        Order order = orders.findById(id).orElseThrow(() -> new IllegalArgumentException("Objednávka neexistuje."));
        if (token == null || token.isBlank() || order.getPaymentToken() == null || !order.getPaymentToken().equals(token))
            throw new IllegalArgumentException("Neplatný odkaz na platbu.");
        if (!"PENDING".equals(order.getStatus()))
            throw new IllegalArgumentException("Objednávka již není ve stavu PENDING.");
        order.setStatus("COMPLETED");
        order.setPaymentToken(null);
        return orders.save(order);
    }

    @Transactional
    public Order adminSetStatus(Long id, String status) {
        if (!"COMPLETED".equals(status) && !"PENDING".equals(status))
            throw new IllegalArgumentException("Neplatný stav.");
        Order order = orders.findById(id).orElseThrow(() -> new IllegalArgumentException("Objednávka neexistuje."));
        order.setStatus(status);
        if ("COMPLETED".equals(status)) order.setPaymentToken(null);
        return orders.save(order);
    }
}
