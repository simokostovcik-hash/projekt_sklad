package cz.project_storage.config;

import cz.project_storage.model.Coffee;
import cz.project_storage.model.Roastery;
import cz.project_storage.repository.CoffeeRepository;
import cz.project_storage.repository.RoasteryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.context.annotation.Profile;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Profile("!mysql")
@Component
@Order(100)
public class TestCatalogSeeder implements CommandLineRunner {
    private final CoffeeRepository coffees;
    private final RoasteryRepository roasteries;

    public TestCatalogSeeder(CoffeeRepository coffees, RoasteryRepository roasteries) {
        this.coffees = coffees;
        this.roasteries = roasteries;
    }

    @Override
    public void run(String... args) {
        Map<String, Roastery> byName = roasteries.findAll().stream()
                .collect(Collectors.toMap(Roastery::getName, r -> r, (a, b) -> a));
        addRoastery(byName, "Nordbeans", "Czech Republic", "Liberec, Czech Republic", "https://www.nordbeans.cz");
        addRoastery(byName, "Doubleshot", "Czech Republic", "Prague, Czech Republic", "https://www.doubleshot.cz");
        addRoastery(byName, "Father's Coffee", "Czech Republic", "Ostrava, Czech Republic", "https://fatherscoffee.cz");
        addRoastery(byName, "Candycane Coffee", "Czech Republic", "Prague, Czech Republic", "https://www.candycane.coffee");
        addRoastery(byName, "The Barn", "Germany", "Berlin, Germany", "https://thebarn.de");
        addRoastery(byName, "Square Mile Coffee", "United Kingdom", "London, United Kingdom", "https://shop.squaremilecoffee.com");

        Set<String> existingNames = coffees.findAll().stream().map(Coffee::getName).collect(Collectors.toSet());
        int added = 0;
        // Product name, roastery, type, quantity, price CZK, stock status.
        Object[][] catalog = {
            {"Ethiopia Guji Natural", "Nordbeans", "Arabica", 42, "349.00", "In Stock"},
            {"Colombia Huila Washed", "Nordbeans", "Arabica", 25, "329.00", "In Stock"},
            {"Brazil Cerrado Espresso", "Nordbeans", "Espresso Blend", 68, "279.00", "In Stock"},
            {"Kenya Kirinyaga AA", "Nordbeans", "Arabica", 0, "389.00", "Out of Stock"},
            {"Ethiopia Sidama", "Doubleshot", "Arabica", 36, "359.00", "In Stock"},
            {"Guatemala Huehuetenango", "Doubleshot", "Arabica", 18, "339.00", "In Stock"},
            {"House Espresso Blend", "Doubleshot", "Espresso Blend", 85, "289.00", "In Stock"},
            {"Rwanda Nyamasheke", "Doubleshot", "Arabica", 0, "369.00", "Out of Stock"},
            {"Costa Rica Tarrazu", "Father's Coffee", "Arabica", 32, "345.00", "In Stock"},
            {"Peru Cajamarca", "Father's Coffee", "Arabica", 12, "305.00", "In Stock"},
            {"Ostrava Espresso", "Father's Coffee", "Espresso Blend", 74, "275.00", "In Stock"},
            {"Burundi Kayanza", "Father's Coffee", "Arabica", 0, "375.00", "Out of Stock"},
            {"Colombia Pink Bourbon", "Candycane Coffee", "Arabica", 9, "449.00", "In Stock"},
            {"Brazil Fazenda Primavera", "Candycane Coffee", "Arabica", 55, "299.00", "In Stock"},
            {"Decaf Colombia Sugarcane", "Candycane Coffee", "Decaf", 21, "335.00", "In Stock"},
            {"El Salvador Santa Ana", "Candycane Coffee", "Arabica", 0, "355.00", "Out of Stock"},
            {"Ethiopia Halo Hartume", "The Barn", "Arabica", 17, "465.00", "In Stock"},
            {"Panama Boquete", "The Barn", "Arabica", 6, "595.00", "In Stock"},
            {"Colombia El Paraiso", "The Barn", "Arabica", 0, "625.00", "Out of Stock"},
            {"Brazil Espresso Reserve", "The Barn", "Espresso Blend", 29, "395.00", "In Stock"},
            {"Red Brick Espresso", "Square Mile Coffee", "Espresso Blend", 48, "365.00", "In Stock"},
            {"Ethiopia Worka", "Square Mile Coffee", "Arabica", 14, "425.00", "In Stock"},
            {"Honduras Marcala", "Square Mile Coffee", "Arabica", 0, "325.00", "Out of Stock"},
            {"Swiss Water Decaf", "Square Mile Coffee", "Decaf", 23, "375.00", "In Stock"}
        };

        for (Object[] item : catalog) {
            String name = (String) item[0];
            if (!existingNames.add(name)) continue;
            Coffee coffee = new Coffee();
            coffee.setName(name);
            coffee.setRoastery(byName.get((String) item[1]));
            coffee.setType((String) item[2]);
            coffee.setQuantity((Integer) item[3]);
            coffee.setPrice(new BigDecimal((String) item[4]));
            coffee.setStockStatus((String) item[5]);
            coffee.setVisibleInShop(false); // Demo products do not automatically appear in the public shop.
            coffees.save(coffee);
            added++;
        }
        System.out.println(">>> Demo catalog: added " + added + " coffees (existing records unchanged).");
    }

    private void addRoastery(Map<String, Roastery> byName, String name, String country, String address, String website) {
        if (byName.containsKey(name)) return;
        Roastery roastery = new Roastery();
        roastery.setName(name);
        roastery.setCountry(country);
        roastery.setAddress(address);
        roastery.setWebsite(website);
        byName.put(name, roasteries.save(roastery));
    }
}
