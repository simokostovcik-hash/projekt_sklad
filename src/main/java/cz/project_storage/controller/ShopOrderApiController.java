package cz.project_storage.controller;

import cz.project_storage.model.Coffee;
import cz.project_storage.model.Order;
import cz.project_storage.model.OrderItem;
import cz.project_storage.repository.CoffeeRepository;
import cz.project_storage.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/orders")
public class ShopOrderApiController {
    private final CoffeeRepository coffees;
    private final OrderRepository orders;
    public ShopOrderApiController(CoffeeRepository coffees, OrderRepository orders) {
        this.coffees = coffees; this.orders = orders;
    }
    public record Customer(String name, String email, String phone, String address, String city, String postalCode, String note) {}
    public record Item(Long coffeeId, Integer quantity) {}
    public record Request(Customer customer, List<Item> items) {}
    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@RequestBody Request request) {
            if (request == null || request.customer() == null || request.items() == null || request.items().isEmpty() || request.items().size() > 40)
                throw new IllegalArgumentException("Vyplňte údaje a přidejte alespoň jednu kávu.");
            Customer c = request.customer();
            if (!valid(c.name(),120) || !valid(c.email(),150) || !c.email().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$") || !valid(c.phone(),120) || !valid(c.address(),120) || !valid(c.city(),120) || !valid(c.postalCode(),120) || (c.note()!=null && c.note().length()>500))
                throw new IllegalArgumentException("Zkontrolujte kontaktní a doručovací údaje.");
            Map<Long,Integer> quantities = new TreeMap<>();
            for (Item item : request.items()) {
                if (item == null || item.coffeeId()==null || item.quantity()==null || item.quantity()<1 || item.quantity()>100)
                    throw new IllegalArgumentException("Neplatné množství produktu.");
                quantities.merge(item.coffeeId(),item.quantity(),Integer::sum);
            }
            Order order = new Order();
            order.setPaymentToken(UUID.randomUUID().toString());
            order.setCustomerName(c.name().trim()); order.setCustomerEmail(c.email().trim()); order.setCustomerPhone(c.phone().trim());
            order.setDeliveryAddress(c.address().trim()); order.setDeliveryCity(c.city().trim()); order.setDeliveryPostalCode(c.postalCode().trim()); order.setCustomerNote(c.note());
            BigDecimal total = BigDecimal.ZERO;
            for (var entry : quantities.entrySet()) {
                Coffee coffee = coffees.findForOrder(entry.getKey()).orElseThrow(()->new IllegalArgumentException("Některá káva již není dostupná."));
                int qty = entry.getValue();
                if (!coffee.isVisibleInShop() || coffee.getQuantity()<qty || qty>100)
                    throw new IllegalArgumentException("Káva " + coffee.getName() + " není v požadovaném množství dostupná.");
                BigDecimal price = coffee.getPrice();
                if (price == null || price.signum()<0 || price.compareTo(BigDecimal.valueOf(Integer.MAX_VALUE))>0)
                    throw new IllegalArgumentException("Neplatná cena produktu.");
                total = total.add(price.multiply(BigDecimal.valueOf(qty)));
                coffee.setQuantity(coffee.getQuantity()-qty);
                order.addOrderItem(new OrderItem(order,coffee,qty,price.intValue()));
            }
            order.setTotalPrice(total.doubleValue());
            // Flush within the transaction so database constraint errors surface
            // before the API reports a successful order.
            orders.saveAndFlush(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("orderId",order.getId(), "paymentToken",order.getPaymentToken()));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> invalid(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
    private boolean valid(String value,int max) { return value!=null && !value.isBlank() && value.length()<=max; }
}
