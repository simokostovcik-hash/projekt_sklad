package cz.project_storage.controller;

import cz.project_storage.model.Order;
import cz.project_storage.repository.OrderRepository;
import cz.project_storage.service.InvoiceService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/api/orders")
public class CustomerInvoiceController {
    private final OrderRepository orders;
    private final InvoiceService invoices;

    public CustomerInvoiceController(OrderRepository orders, InvoiceService invoices) {
        this.orders = orders;
        this.invoices = invoices;
    }

    @GetMapping("/{id}/invoice")
    public ResponseEntity<byte[]> download(@PathVariable Long id,
                                           @RequestParam String paymentToken) throws IOException {
        Order order = orders.findById(id).orElse(null);
        if (order == null || !"COMPLETED".equals(order.getStatus())
                || paymentToken == null || paymentToken.isBlank()
                || order.getPaymentToken() == null
                || !order.getPaymentToken().equals(paymentToken)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"faktura-" + id + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(invoices.generateInvoiceBytes(order));
    }
}
