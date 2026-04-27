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
            List<String> defaultRoasteries = Arrays.asList("DoubleShot", "Nordbeans", "The Barn");
            for (String name : defaultRoasteries) {
                Roastery r = new Roastery();
                r.setName(name);
                r.setCountry("Czech Republic");
                roasteryRepository.save(r);
            }
        }
    }

    @GetMapping("/coffee/all")
    public String showInventory(Model model) {
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
            roasteryRepository.findById(coffee.getRoastery().getId()).ifPresent(coffee::setRoastery);
        }
        Set<Tag> processedTags = new HashSet<>();
        if (tagNames != null && !tagNames.trim().isEmpty()) {
            for (String name : tagNames.split(",")) {
                String trimmedName = name.trim();
                if (!trimmedName.isEmpty()) {
                    Tag tag = tagRepository.findByName(trimmedName)
                            .orElseGet(() -> tagRepository.save(new Tag(trimmedName)));
                    processedTags.add(tag);
                }
            }
        }
        coffee.setTags(processedTags);
        coffee.setStockStatus(coffee.getQuantity() > 0 ? "In Stock" : "Out of Stock");
        String author = (principal != null) ? principal.getName() : "System";
        coffeeRepository.save(coffee);
        auditLogRepository.save(new AuditLog(author, "Saved coffee: " + coffee.getName(), LocalDateTime.now()));
        return "redirect:/coffee/all";
    }

    @GetMapping("/coffee/edit/{id}")
    public String editCoffee(@PathVariable Long id, Model model) {
        coffeeRepository.findById(id).ifPresent(c -> model.addAttribute("coffee", c));
        prepareInventoryModel(model);
        return "coffee_list";
    }

    @GetMapping("/coffee/delete/{id}")
    public String deleteCoffee(@PathVariable Long id, Principal principal) {
        coffeeRepository.findById(id).ifPresent(c -> {
            coffeeRepository.delete(c);
            auditLogRepository.save(new AuditLog(principal.getName(), "Deleted coffee: " + c.getName(), LocalDateTime.now()));
        });
        return "redirect:/coffee/all";
    }

    @GetMapping("/orders/all")
    public String showMyOrders(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username).orElse(null);
        List<Order> orders;
        if (currentUser != null && "ROLE_ADMIN".equals(currentUser.getRole())) {
            orders = orderRepository.findAll();
        } else {
            orders = orderRepository.findByUserUsername(username);
        }
        model.addAttribute("orders", orders);
        return "orders_list";
    }

    @PostMapping("/orders/confirm")
    public String confirmOrder(@ModelAttribute Order orderData,
                               @RequestParam Long coffeeId,
                               @RequestParam int quantity,
                               Principal principal) {
        if (principal == null) return "redirect:/login";

        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username).orElseThrow();
        Coffee coffee = coffeeRepository.findById(coffeeId).orElseThrow();

        if (coffee.getQuantity() >= quantity && quantity > 0) {
            coffee.setQuantity(coffee.getQuantity() - quantity);
            coffee.setStockStatus(coffee.getQuantity() > 0 ? "In Stock" : "Out of Stock");
            coffeeRepository.save(coffee);

            orderData.setOrderDate(LocalDateTime.now());
            orderData.setStatus("COMPLETED");
            orderData.setUser(currentUser);
            orderData.setTotalPrice(coffee.getPrice() * quantity);

            OrderItem item = new OrderItem();
            item.setCoffee(coffee);
            item.setQuantity(quantity);
            item.setPriceAtPurchase((int) coffee.getPrice());
            item.setOrder(orderData);

            orderData.setItems(new ArrayList<>(Collections.singletonList(item)));

            orderRepository.save(orderData);
            auditLogRepository.save(new AuditLog(username, "Created order for " + quantity + "x " + coffee.getName(), LocalDateTime.now()));
        }
        return "redirect:/orders/all";
    }

    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable Long id, Principal principal) {
        if (principal == null) return "redirect:/login";
        orderRepository.findById(id).ifPresent(order -> {
            orderRepository.delete(order);
            auditLogRepository.save(new AuditLog(principal.getName(), "Deleted order ID: " + id, LocalDateTime.now()));
        });
        return "redirect:/orders/all";
    }

    @GetMapping("/menu") public String mainMenu() { return "storage"; }
    @GetMapping("/login") public String login() { return "login"; }
    @GetMapping("/register") public String showRegistration(Model model) { model.addAttribute("user", new User()); return "register"; }

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

    @GetMapping("/orders/invoice/generate/{id}")
    public void generateInvoice(@PathVariable Long id, jakarta.servlet.http.HttpServletResponse response) {
        orderRepository.findById(id).ifPresent(order -> {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=invoice.pdf");
            try { invoiceService.exportOrder(order, response); } catch (Exception e) { e.printStackTrace(); }
        });
    }

    @GetMapping("/roasteries")
    public String showRoasteries(Model model) {
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

    @PostMapping("/admin/user/update-role")
    public String updateRole(@RequestParam Long userId, @RequestParam String newRole) {
        userRepository.findById(userId).ifPresent(u -> {
            String formattedRole = newRole.toUpperCase();
            if (!formattedRole.startsWith("ROLE_")) formattedRole = "ROLE_" + formattedRole;
            u.setRole(formattedRole);
            userRepository.save(u);
        });
        return "redirect:/admin/users";
    }
}