package cz.projekt_sklad.config;

import cz.projekt_sklad.model.Uzivatel;
import cz.projekt_sklad.repository.UzivatelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UzivatelRepository uzivatelRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (uzivatelRepository.findByUsername("admin").isEmpty()) {
            Uzivatel admin = new Uzivatel();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole("ROLE_ADMIN");
            uzivatelRepository.save(admin);
            System.out.println("--- Admin účet byl vytvořen (admin / admin123) ---");
        }
    }
}