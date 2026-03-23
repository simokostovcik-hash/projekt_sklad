package cz.project_storage.controller;

import cz.project_storage.model.*;
import cz.project_storage.repository.*;
import cz.project_storage.service.InvoiceService;
import cz.project_storage.service.OrderService;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

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

    @PostConstruct
    public void initData() {
        if (roasteryRepository.count() == 0) {
            List<String> defaultRoasteries = Arrays.asList(
                    "DoubleShot",
                    "Nordbeans",
                    "The Barn",
                    "Father's Coffee Roastery",
                    "Mazlab",
                    "Rebelbean"
            );

            for (String name : defaultRoasteries) {
                Roastery r = new Roastery();
                r.setName(name);
                roasteryRepository.save(r);
            }
            System.out.println(">>> The roaster database has been initialized with basic data.");
        }
    }
    @GetMapping("/coffee/all")
    public String showInventory(Model model) {
        prepareInventoryModel(model);
        return "coffee_list";
    }

    @PostMapping("/orders/sell")
    public String sellCoffee(@RequestParam Long id, @RequestParam int quantity, Principal principal) {
        coffeeRepository.findById(id).ifPresent(coffee -> {
            if (coffee.getQuantity() >= quantity && quantity > 0) {
                coffee.setQuantity(coffee.getQuantity() - quantity);
                if (coffee.getQuantity() == 0) coffee.setStockStatus("Out of Stock");
                coffeeRepository.save(coffee);

                String username = (principal != null) ? principal.getName() : "Customer";

                Order newOrder = new Order();
                newOrder.setOrderDate(LocalDateTime.now());
                newOrder.setStatus("COMPLETED");
                newOrder.setTotalPrice(coffee.getPrice() * quantity);

                userRepository.findByUsername(username).ifPresent(newOrder::setUser);

                OrderItem item = new OrderItem();
                item.setCoffee(coffee);
                item.setQuantity(quantity);
                item.setPriceAtPurchase((int) coffee.getPrice());
                item.setOrder(newOrder);

                List<OrderItem> items = new ArrayList<>();
                items.add(item);
                newOrder.setItems(items);

                orderRepository.save(newOrder);
                auditLogRepository.save(new AuditLog(username, "Sold " + quantity + "x: " + coffee.getName(), LocalDateTime.now()));
            }
        });
        return "redirect:/coffee/all";
    }

    @GetMapping("/buy/{id}")
    public String buyCoffee(@PathVariable Long id, Principal principal) {
        return sellCoffee(id, 1, principal);
    }

    @GetMapping("/orders/all")
    public String showMyOrders(Model model) {
        List<Order> orders = orderRepository.findAll();
        model.addAttribute("orders", (orders != null) ? orders : new ArrayList<>());
        return "orders_list";
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
                             @RequestParam(value = "tagNames", required = false) String tagNames,
                             Model model, Principal principal) {

        if (result.hasErrors()) {
            prepareInventoryModel(model);
            return "coffee_list";
        }

        if (coffee.getRoastery() != null && coffee.getRoastery().getId() != null) {
            roasteryRepository.findById(coffee.getRoastery().getId())
                    .ifPresent(coffee::setRoastery);
        }

        Set<Tag> processedTags = new HashSet<>();
        if (tagNames != null && !tagNames.trim().isEmpty()) {
            String[] names = tagNames.split(",");
            for (String name : names) {
                String trimmedName = name.trim();
                if (!trimmedName.isEmpty()) {
                    Tag tag = tagRepository.findByName(trimmedName)
                            .orElseGet(() -> tagRepository.save(new Tag(trimmedName)));
                    processedTags.add(tag);
                }
            }
        }
        coffee.setTags(processedTags);

        if (coffee.getQuantity() > 0) {
            coffee.setStockStatus("In Stock");
        } else {
            coffee.setStockStatus("Out of Stock");
        }

        String author = (principal != null) ? principal.getName() : "System";
        String action = (coffee.getId() == null) ? "Added coffee: " : "Updated coffee: ";

        coffeeRepository.save(coffee);
        auditLogRepository.save(new AuditLog(author, action + coffee.getName(), LocalDateTime.now()));

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

    @GetMapping("/admin/users")
    public String showAdminPanel(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("auditLogs", auditLogRepository.findAll());
        return "admin_panel";
    }

    @PostMapping("/admin/user/update-role")
    public String updateRole(@RequestParam Long userId, @RequestParam String newRole) {
        userRepository.findById(userId).ifPresent(u -> {
            u.setRole(newRole);
            userRepository.save(u);
        });
        return "redirect:/admin/users";
    }



    @GetMapping("/orders/invoice/generate/{id}")
    public void generateInvoice(@PathVariable Long id, jakarta.servlet.http.HttpServletResponse response) {
        try {
            Order order = orderRepository.findById(id).orElse(null);
            if (order == null) return;

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=invoice_" + id + ".pdf");

            invoiceService.exportOrder(order, response);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable Long id) {
        orderRepository.findById(id).ifPresent(order -> {
            if (order.getItems() != null) {
                for (OrderItem item : order.getItems()) {
                    Coffee coffee = item.getCoffee();
                    if (coffee != null) {
                        coffee.setQuantity(coffee.getQuantity() + item.getQuantity());
                        if (coffee.getQuantity() > 0 && "Out of Stock".equals(coffee.getStockStatus())) {
                            coffee.setStockStatus("In Stock");
                        }
                        coffeeRepository.save(coffee);
                    }
                }
            }
            orderRepository.delete(order);
        });
        return "redirect:/orders/all";
    }

    @GetMapping("/roasteries")
    public String showRoasteries(Model model) {
        // DŮLEŽITÉ: Šablona používá název "roasteries", nikoliv "allRoasteries"
        model.addAttribute("roasteries", roasteryRepository.findAll());
        if (!model.containsAttribute("roastery")) {
            model.addAttribute("roastery", new Roastery());
        }
        return "roastery_list";
    }

    @PostMapping("/roastery/save")
    public String saveRoastery(@ModelAttribute Roastery roastery) {
        roasteryRepository.save(roastery);
        return "redirect:/roasteries";
    }

    @GetMapping("/roastery/edit/{id}")
    public String editRoastery(@PathVariable Long id, Model model) {
        roasteryRepository.findById(id).ifPresent(r -> model.addAttribute("roastery", r));
        model.addAttribute("roasteries", roasteryRepository.findAll());
        return "roastery_list";
    }

    @GetMapping("/roastery/delete/{id}")
    public String deleteRoastery(@PathVariable Long id) {
        roasteryRepository.deleteById(id);
        return "redirect:/roasteries";
    }
}