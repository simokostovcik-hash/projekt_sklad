package cz.project_storage.model;

import jakarta.persistence.*;

@Entity
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coffee_id")
    private Coffee coffee;

    private Integer quantity;
    private Integer priceAtPurchase;

    public OrderItem() {}

    public OrderItem(Order order, Coffee coffee, Integer quantity, Integer priceAtPurchase) {
        this.order = order;
        this.coffee = coffee;
        this.quantity = quantity;
        this.priceAtPurchase = priceAtPurchase;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public Coffee getCoffee() { return coffee; }
    public void setCoffee(Coffee coffee) { this.coffee = coffee; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public Integer getPriceAtPurchase() { return priceAtPurchase; }
    public void setPriceAtPurchase(Integer priceAtPurchase) { this.priceAtPurchase = priceAtPurchase; }
}