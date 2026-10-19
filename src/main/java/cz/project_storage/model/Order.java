package cz.project_storage.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime orderDate;
    private String status;
    private Double totalPrice;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private String deliveryAddress;
    private String deliveryCity;
    private String deliveryPostalCode;
    @Column(length = 500)
    private String customerNote;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    public Order() {
        this.orderDate = LocalDateTime.now();
        this.status = "PENDING";
        this.totalPrice = 0.0;
    }

    public void setCustomerName(String v) { customerName = v; }
    public String getCustomerName() { return customerName; }
    public void setCustomerEmail(String v) { customerEmail = v; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerPhone(String v) { customerPhone = v; }
    public String getCustomerPhone() { return customerPhone; }
    public void setDeliveryAddress(String v) { deliveryAddress = v; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryCity(String v) { deliveryCity = v; }
    public String getDeliveryCity() { return deliveryCity; }
    public void setDeliveryPostalCode(String v) { deliveryPostalCode = v; }
    public String getDeliveryPostalCode() { return deliveryPostalCode; }
    public void setCustomerNote(String v) { customerNote = v; }
    public String getCustomerNote() { return customerNote; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(Double totalPrice) { this.totalPrice = totalPrice; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public void addOrderItem(OrderItem item) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(item);
        item.setOrder(this);
    }
}