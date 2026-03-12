package cz.project_storage.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Coffee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private int quantity;
    private int price;
    private String stockStatus;
    private LocalDate orderDate;

    @ManyToOne
    @JoinColumn(name = "roastery_id")
    private Roastery roastery;

    public Coffee() {
    }

    public Coffee(Long id, String name, Roastery roastery, String type, int quantity, int price, String stockStatus, LocalDate orderDate) {
        this.id = id;
        this.name = name;
        this.roastery = roastery;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.stockStatus = stockStatus;
        this.orderDate = orderDate;
    }


    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public String getStockStatus() { return stockStatus; }
    public void setStockStatus(String stockStatus) { this.stockStatus = stockStatus; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public Roastery getRoastery() { return roastery; }
    public void setRoastery(Roastery roastery) { this.roastery = roastery; }
}