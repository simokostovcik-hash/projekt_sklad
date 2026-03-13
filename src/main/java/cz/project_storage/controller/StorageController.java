package cz.project_storage.controller;

import cz.project_storage.model.*;
import cz.project_storage.repository.*;
import cz.project_storage.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class StorageController {

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoasteryRepository roasteryRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @PostMapping("/orders/buy/{id}")
    public String buyCoffee(@PathVariable Long id, Principal principal) {
        if (principal == null) return "redirect:/login";
        orderService.processOrder(id, principal.getName());

        return "redirect:/orders/all";
    }

    @GetMapping("/orders/all")
    public String showMyOrders(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        List<Order> orders = orderService.getOrdersByUsername(principal.getName());
        model.addAttribute("orders", orders);
        return "orders_list";
    }


    @GetMapping("/coffee/all")
    public String showInventory(Model model) {
        if (!model.containsAttribute("coffee")) {
            model.addAttribute("coffee", new Coffee());
        }
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
        auditLogRepository.save(new AuditLog(author, action + coffee.getName(), LocalDateTime.now()));

        return "redirect:/coffee/all";
    }

    @GetMapping("/coffee/edit/{id}")
    public String editCoffee(@PathVariable Long id, Model model) {
        Coffee coffee = coffeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid coffee Id:" + id));
        model.addAttribute("coffee", coffee);
        prepareInventoryModel(model);
        return "coffee_list";
    }

    @GetMapping("/coffee/delete/{id}")
    public String deleteCoffee(@PathVariable Long id, Principal principal) {
        String author = (principal != null) ? principal.getName() : "System";
        coffeeRepository.findById(id).ifPresent(c -> {
            auditLogRepository.save(new AuditLog(author, "Deleted coffee: " + c.getName(), LocalDateTime.now()));
            coffeeRepository.delete(c);
        });
        return "redirect:/coffee/all";
    }


    @GetMapping("/menu")
    public String mainMenu() {
        return "storage";
    }

    @GetMapping("/admin/users")
    public String showAdminPanel(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("auditLogs", auditLogRepository.findAllByOrderByTimestampDesc());
        return "admin_panel";
    }

    @GetMapping("/admin/roasteries")
    public String listRoasteries(Model model) {
        model.addAttribute("roastery", new Roastery());
        model.addAttribute("allRoasteries", roasteryRepository.findAll());
        return "roastery_list";
    }

    @PostMapping("/admin/roastery/save")
    public String saveRoastery(@ModelAttribute("roastery") Roastery roastery, Principal principal) {
        String author = (principal != null) ? principal.getName() : "System";
        String action = (roastery.getId() == null) ? "Added roastery: " : "Updated roastery: ";
        roasteryRepository.save(roastery);
        auditLogRepository.save(new AuditLog(author, action + roastery.getName(), LocalDateTime.now()));
        return "redirect:/admin/roasteries";
    }

    @GetMapping("/test/generate-data")
    public String generateTestData() {
        coffeeRepository.deleteAll();
        roasteryRepository.deleteAll();

        List<Roastery> ros = new ArrayList<>();
        ros.add(new Roastery(null, "Doubleshot", "Czech Republic", "Prague", "https://www.doubleshot.cz"));
        ros.add(new Roastery(null, "The Barn", "Germany", "Berlin", "https://thebarn.de"));
        ros.add(new Roastery(null, "Square Mile", "United Kingdom", "London", "https://shop.squaremilecoffee.com"));
        ros.add(new Roastery(null, "Gardelli", "Italy", "Forli", "https://gardellicoffee.com"));
        ros.add(new Roastery(null, "Nordic Approach", "Norway", "Oslo", ""));
        ros.add(new Roastery(null, "Fathers Coffee", "Czech Republic", "Ostrava", "https://fatherscoffee.cz"));
        ros.add(new Roastery(null, "Coffee Collective", "Denmark", "Copenhagen", "https://coffeecollective.dk"));

        roasteryRepository.saveAll(ros);
        List<Roastery> dbRos = roasteryRepository.findAll();

        List<Coffee> cfs = new ArrayList<>();
        cfs.add(new Coffee(null, "Tam Dem", dbRos.get(0), "Espresso Blend", 350, 320, "In Stock", null));
        cfs.add(new Coffee(null, "Ethiopia Duromina", dbRos.get(1), "Filter Arabica", 250, 450, "In Stock", null));
        cfs.add(new Coffee(null, "Brazil Diamond", dbRos.get(2), "Omni Roast", 1000, 750, "In Stock", null));
        cfs.add(new Coffee(null, "Kenya AA Karogoto", dbRos.get(3), "Filter Special", 250, 580, "Ordered", LocalDate.now().plusDays(3)));

        coffeeRepository.saveAll(cfs);
        return "redirect:/coffee/all";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

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

    private void prepareInventoryModel(Model model) {
        List<Coffee> list = coffeeRepository.findAll();
        model.addAttribute("coffeeList", list);
        model.addAttribute("count", list.size());
        model.addAttribute("total", list.stream().mapToInt(Coffee::getPrice).sum());
        model.addAttribute("allRoasteries", roasteryRepository.findAll());
    }
}