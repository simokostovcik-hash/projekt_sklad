package cz.project_storage.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "coffees")
public class Coffee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Coffee name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    private String type;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private int quantity;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price cannot be negative")
    private BigDecimal price;

    private String stockStatus;

    @Column(nullable = false)
    private boolean visibleInShop = false;

    @Column(length = 2000)
    private String description;

    private String imageUrl;


    private LocalDate roastDate;

    @NotNull(message = "Please select a roastery")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "roastery_id")
    private Roastery roastery;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "coffees_tags",
            joinColumns = @JoinColumn(
                    name = "coffee_id",
                    referencedColumnName = "id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "tag_id",
                    referencedColumnName = "id"
            )
    )
    private Set<Tag> tags = new HashSet<>();

    public boolean isVisibleInShop() { return visibleInShop; }
    public void setVisibleInShop(boolean visibleInShop) { this.visibleInShop = visibleInShop; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Coffee() {
    }

    public Coffee(String name, Roastery roastery, String type, int quantity, BigDecimal price, String stockStatus, LocalDate roastDate) {
        this.name = name;
        this.roastery = roastery;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        this.stockStatus = stockStatus;
        this.roastDate = roastDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStockStatus() {
        return stockStatus;
    }

    public void setStockStatus(String stockStatus) {
        this.stockStatus = stockStatus;
    }

    public LocalDate getRoastDate() {
        return roastDate;
    }

    public void setRoastDate(LocalDate roastDate) {
        this.roastDate = roastDate;
    }

    public Roastery getRoastery() {
        return roastery;
    }

    public void setRoastery(Roastery roastery) {
        this.roastery = roastery;
    }

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}