package cz.project_storage.controller;

import cz.project_storage.model.Order;
import cz.project_storage.service.DemoPaymentService;
import cz.project_storage.service.OrderEmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Controller
public class DemoPaymentController {
    private static final Logger log = LoggerFactory.getLogger(DemoPaymentController.class);
    private final DemoPaymentService payments;
    private final OrderEmailService emails;
    public DemoPaymentController(DemoPaymentService payments, OrderEmailService emails) {
        this.payments = payments; this.emails = emails;
    }

    public record PaymentRequest(Long orderId, String paymentToken) {}

    @PostMapping("/api/payments/demo/complete")
    @ResponseBody
    public ResponseEntity<?> pay(@RequestBody PaymentRequest request) {
        try {
            if (request == null || request.orderId() == null) throw new IllegalArgumentException("Chybí objednávka.");
            Order order = payments.completeDemoPayment(request.orderId(), request.paymentToken());
            boolean sent = sendSafely(order);
            return ResponseEntity.ok(Map.of("status", "COMPLETED", "emailSent", sent));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @PostMapping("/orders/status/{id}")
    public String changeStatus(@PathVariable Long id, @RequestParam String status) {
        Order order = payments.adminSetStatus(id, status);
        if ("COMPLETED".equals(status)) sendSafely(order);
        return "redirect:/orders/all";
    }

    private boolean sendSafely(Order order) {
        try { return emails.sendConfirmation(order); }
        catch (Exception ex) { log.warn("E-mail objednávky {} se nepodařilo odeslat: {}", order.getId(), ex.getMessage()); return false; }
    }
}
