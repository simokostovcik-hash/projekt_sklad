package cz.project_storage.controller;

import cz.project_storage.model.*;
import cz.project_storage.repository.*;
import cz.project_storage.service.InvoiceService;
import cz.project_storage.service.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Controller
public class StorageController {

    @Autowired private CoffeeRepository coffeeRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private RoasteryRepository roasteryRepository;
    @Autowired private OrderService orderService;
    @Autowired private OrderRepository orderRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private InvoiceService invoiceService;

    private void prepareInventoryModel(Model model) {
        List<Coffee> list = coffeeRepository.findAll();
        model.addAttribute("coffeeList", (list != null) ? list : new ArrayList<>());

        double total = (list != null) ? list.stream()
                .mapToDouble(c -> c.getPrice() * c.getQuantity())
                .sum() : 0.0;

        model.addAttribute("total", total);
        model.addAttribute("allRoasteries", roasteryRepository.findAll());
        model.addAttribute("allTags", tagRepository.findAll());

        if (!model.containsAttribute("coffee")) {
            model.addAttribute("coffee", new Coffee());
        }
    }

    @GetMapping("/coffee/all")
    public String showInventory(Model model) {
        prepareInventoryModel(model);
        return "coffee_list";
    }

    @GetMapping("/coffee/edit/{id}")
    public String editCoffee(@PathVariable Long id, Model model) {
        Coffee coffee = coffeeRepository.findById(id).orElse(null);
        if (coffee == null) return "redirect:/coffee/all";

        model.addAttribute("coffee", coffee);
        prepareInventoryModel(model);
        return "coffee_list";
    }

    @PostMapping("/coffee/save")
    public String saveCoffee(@Valid @ModelAttribute("coffee") Coffee coffee,
                             BindingResult result,
                             Model model,
                             Principal principal) {
        if (result.hasErrors()) {
            prepareInventoryModel(model);
            return "coffee_list";
        }

        String author = (principal != null) ? principal.getName() : "System";
        String action = (coffee.getId() == null) ? "Added coffee: " : "Updated coffee: ";

        coffeeRepository.save(coffee);

        try {
            auditLogRepository.save(new AuditLog(author, action + coffee.getName(), LocalDateTime.now()));
        } catch (Exception e) {
            System.err.println("Audit log failed, but coffee saved.");
        }

        return "redirect:/coffee/all";
    }

    @GetMapping("/admin/users")
    public String showAdminPanel(Model model) {
        try {
            model.addAttribute("users", userRepository.findAll());
            List<AuditLog> logs = auditLogRepository.findAllByOrderByTimestampDesc();
            model.addAttribute("auditLogs", (logs != null) ? logs : new ArrayList<>());
        } catch (Exception e) {
            model.addAttribute("users", new ArrayList<>());
            model.addAttribute("auditLogs", new ArrayList<>());
            System.err.println("Admin Panel Error: " + e.getMessage());
        }
        return "admin_panel";
    }

    @GetMapping("/buy/{id}")
    public String buyCoffee(@PathVariable Long id, Principal principal) {
        coffeeRepository.findById(id).ifPresent(coffee -> {
            if (coffee.getQuantity() > 0) {
                coffee.setQuantity(coffee.getQuantity() - 1);
                if (coffee.getQuantity() == 0) coffee.setStockStatus("Out of Stock");
                coffeeRepository.save(coffee);
                String author = (principal != null) ? principal.getName() : "Customer";
                auditLogRepository.save(new AuditLog(author, "Purchased: " + coffee.getName(), LocalDateTime.now()));
            }
        });
        return "redirect:/coffee/all";
    }

    @GetMapping("/coffee/delete/{id}")
    public String deleteCoffee(@PathVariable Long id, Principal principal) {
        coffeeRepository.findById(id).ifPresent(c -> {
            String author = (principal != null) ? principal.getName() : "System";
            auditLogRepository.save(new AuditLog(author, "Deleted coffee: " + c.getName(), LocalDateTime.now()));
            coffeeRepository.delete(c);
        });
        return "redirect:/coffee/all";
    }

    @GetMapping("/menu")
    public String mainMenu() { return "storage"; }

    @GetMapping("/login")
    public String login() { return "login"; }

    @GetMapping("/register")
    public String showRegistration(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute User user, Model model) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("error", "User already exists!");
            return "register";
        }
        user.setRole("ROLE_USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/login?success";
    }
}