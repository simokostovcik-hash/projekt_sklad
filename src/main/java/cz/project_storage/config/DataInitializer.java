package cz.project_storage.config;

import cz.project_storage.model.User;
import cz.project_storage.model.Coffee;
import cz.project_storage.model.Roastery;
import cz.project_storage.repository.UserRepository;
import cz.project_storage.repository.CoffeeRepository;
import cz.project_storage.repository.RoasteryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Autowired
    private RoasteryRepository roasteryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeAdmin();
        initializeStorage();
    }

    private void initializeAdmin() {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ROLE_ADMIN");
            userRepository.save(admin);
            System.out.println(">>> Database: Default admin user created (admin/admin123).");
        }
    }

    private void initializeStorage() {
        if (coffeeRepository.count() == 0) {
            Roastery defaultRoastery = new Roastery();
            defaultRoastery.setName("Default Roastery");
            defaultRoastery.setCountry("Czech Republic");
            roasteryRepository.save(defaultRoastery);

            Coffee c1 = new Coffee();
            c1.setName("Panama Geisha");
            c1.setPrice(550.0);
            c1.setQuantity(15);
            c1.setRoastery(defaultRoastery);
            c1.setStockStatus("In Stock");

            Coffee c2 = new Coffee();
            c2.setName("Ethiopia Yirgacheffe");
            c2.setPrice(320.0);
            c2.setQuantity(20);
            c2.setRoastery(defaultRoastery);
            c2.setStockStatus("In Stock");

            coffeeRepository.saveAll(List.of(c1, c2));
            System.out.println(">>> Database: Initial coffee stock loaded.");
        }
    }
}