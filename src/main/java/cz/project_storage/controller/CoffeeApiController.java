package cz.project_storage.controller;

import cz.project_storage.repository.CoffeeRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/coffees")
public class CoffeeApiController {
    private final CoffeeRepository coffeeRepository;
    public CoffeeApiController(CoffeeRepository coffeeRepository) { this.coffeeRepository = coffeeRepository; }

    public record CoffeeResponse(Long id, String name, BigDecimal price, int quantity,
                                 String type, String roastery, String description, String imageUrl,
                                 boolean available) {}

    @GetMapping
    public List<CoffeeResponse> getAllCoffees() {
        return coffeeRepository.findAll().stream()
                .filter(coffee -> coffee.isVisibleInShop())
                .map(coffee -> new CoffeeResponse(coffee.getId(), coffee.getName(), coffee.getPrice(),
                        coffee.getQuantity(), coffee.getType(),
                        coffee.getRoastery() == null ? "" : coffee.getRoastery().getName(),
                        coffee.getDescription(), coffee.getImageUrl(), coffee.getQuantity() > 0))
                .toList();
    }
}
