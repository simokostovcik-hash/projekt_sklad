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

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;

@Controller
public class StorageController {

    @Autowired private CoffeeRepository coffeeRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private AuditLogRepository auditLogRepository;
    @Autowired private RoasteryRepository roasteryRepository;
    @Autowired private OrderService orderService;
    @Autowired private OrderRepository orderRepository;
    @Autowired private TagRepository tagRepository;
    @Autowired private InvoiceService invoiceService;
    @Autowired private PasswordEncoder passwordEncoder;

    private void prepareInventoryModel(Model model) {
        List<Coffee> list = coffeeRepository.findAll();
        model.addAttribute("coffeeList", (list != null) ? list : new ArrayList<>());

        double total = (list != null) ? list.stream()
                .mapToDouble(c -> {
                    if (c.getPrice() != null) {
                        return c.getPrice().multiply(BigDecimal.valueOf(c.getQuantity())).doubleValue();
                    }
                    return 0.0;
                })
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
                    "DoubleShot", "Nordbeans", "The Barn",
                    "Father's Coffee Roastery", "Mazlab", "Rebelbean"
            );
            for (String name : defaultRoasteries) {
                Roastery r = new Roastery();
                r.setName(name);
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
        // Update the existing entity instead of replacing it: retain associations and historical data.
        Coffee savedCoffee = coffee;
        if (coffee.getId() != null) {
            savedCoffee = coffeeRepository.findById(coffee.getId()).orElseThrow(() ->
                    new org.springframework.web.server.ResponseStatusException(
                            org.springframework.http.HttpStatus.NOT_FOUND, "Coffee not found"));
            savedCoffee.setName(coffee.getName());
            savedCoffee.setType(coffee.getType());
            savedCoffee.setPrice(coffee.getPrice());
            savedCoffee.setQuantity(coffee.getQuantity());
            savedCoffee.setRoastDate(coffee.getRoastDate());
            savedCoffee.setRoastery(coffee.getRoastery());
            savedCoffee.setDescription(coffee.getDescription());
            savedCoffee.setImageUrl(coffee.getImageUrl());
            savedCoffee.setVisibleInShop(coffee.isVisibleInShop());
        }
        savedCoffee.setTags(processedTags);
        savedCoffee.setStockStatus(savedCoffee.getQuantity() > 0 ? "In Stock" : "Out of Stock");
        String author = (principal != null) ? principal.getName() : "System";
        coffeeRepository.save(savedCoffee);
        auditLogRepository.save(new AuditLog(author, "Saved coffee: " + coffee.getName(), LocalDateTime.now()));
        return "redirect:/coffee/all";
    }

    @GetMapping("/coffee/edit/{id}")
    public String editCoffee(@PathVariable Long id, Model model) {
        coffeeRepository.findById(id).ifPresent(c -> model.addAttribute("coffee", c));
        prepareInventoryModel(model);
        return "coffee_list";
    }

    @PostMapping("/coffee/delete/{id}")
    public String deleteCoffee(@PathVariable Long id, Principal principal) {
        coffeeRepository.findById(id).ifPresent(c -> {
            String author = (principal != null) ? principal.getName() : "System";
            auditLogRepository.save(new AuditLog(author, "Hidden coffee: " + c.getName(), LocalDateTime.now()));
            // Keep product identity for historical order items; remove from storefront instead.
            c.setVisibleInShop(false);
            coffeeRepository.save(c);
        });
        return "redirect:/coffee/all";
    }

    @PostMapping("/coffee/visibility/{id}")
    public String changeVisibility(@PathVariable Long id, Principal principal) {
        Coffee coffee = coffeeRepository.findById(id).orElseThrow(() ->
                new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Coffee not found"));
        coffee.setVisibleInShop(!coffee.isVisibleInShop());
        coffeeRepository.save(coffee);
        String author = principal != null ? principal.getName() : "System";
        auditLogRepository.save(new AuditLog(author,
                (coffee.isVisibleInShop() ? "Published coffee: " : "Unpublished coffee: ") + coffee.getName(),
                LocalDateTime.now()));
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

    @PostMapping("/orders/sell")
    public String sellCoffee(@RequestParam Long id, @RequestParam int quantity, Principal principal) {
        if (principal == null) return "redirect:/login";
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username).orElseThrow();

        coffeeRepository.findById(id).ifPresent(coffee -> {
            if (coffee.getQuantity() >= quantity && quantity > 0) {
                coffee.setQuantity(coffee.getQuantity() - quantity);
                coffee.setStockStatus(coffee.getQuantity() == 0 ? "Out of Stock" : "In Stock");
                coffeeRepository.save(coffee);

                Order newOrder = new Order();
                newOrder.setOrderDate(LocalDateTime.now());
                newOrder.setStatus("COMPLETED");

                if (coffee.getPrice() != null) {
                    BigDecimal totalOrderPrice = coffee.getPrice().multiply(BigDecimal.valueOf(quantity));
                    newOrder.setTotalPrice(totalOrderPrice.doubleValue());
                } else {
                    newOrder.setTotalPrice(0.0);
                }

                newOrder.setUser(currentUser);

                OrderItem item = new OrderItem();
                item.setCoffee(coffee);
                item.setQuantity(quantity);
                item.setPriceAtPurchase(coffee.getPrice() != null ? coffee.getPrice().intValue() : 0);
                item.setOrder(newOrder);

                newOrder.setItems(Collections.singletonList(item));
                orderRepository.save(newOrder);
                auditLogRepository.save(new AuditLog(username, "Bought " + quantity + "x " + coffee.getName(), LocalDateTime.now()));
            }
        });
        return "redirect:/coffee/all";
    }

    @PostMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable Long id, Principal principal) {
        if (principal == null) return "redirect:/login";
        String username = principal.getName();
        User currentUser = userRepository.findByUsername(username).orElseThrow();

        orderRepository.findById(id).ifPresent(order -> {
            boolean isAdmin = "ROLE_ADMIN".equals(currentUser.getRole());
            boolean isOwner = order.getUser() != null && order.getUser().getUsername().equals(username);

            if (isAdmin || isOwner) {
                if (order.getItems() != null) {
                    for (OrderItem item : order.getItems()) {
                        Coffee coffee = item.getCoffee();
                        if (coffee != null) {
                            coffee.setQuantity(coffee.getQuantity() + item.getQuantity());
                            coffee.setStockStatus("In Stock");
                            coffeeRepository.save(coffee);
                        }
                    }
                }
                orderRepository.delete(order);
                auditLogRepository.save(new AuditLog(username, "Deleted order ID: " + id, LocalDateTime.now()));
            }
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
    public void generateInvoice(@PathVariable Long id, Principal principal, jakarta.servlet.http.HttpServletResponse response) {
        try {
            Order order = orderRepository.findById(id).orElse(null);
            if (principal == null || order == null) {
                response.sendError(404);
                return;
            }
            User requester = userRepository.findByUsername(principal.getName()).orElse(null);
            boolean admin = requester != null && "ROLE_ADMIN".equals(requester.getRole());
            boolean owner = order.getUser() != null && principal.getName().equals(order.getUser().getUsername());
            if (!admin && !owner) {
                response.sendError(403);
                return;
            }
            {
                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=invoice.pdf");
                try { invoiceService.exportOrder(order, response); } catch (Exception e) { e.printStackTrace(); }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    @PostMapping("/roastery/save")
    public String saveRoastery(@ModelAttribute Roastery roastery) {
        if (roastery.getName() == null || roastery.getName().isBlank()) return "redirect:/roasteries";
        if (roastery.getId() != null && !roasteryRepository.existsById(roastery.getId())) return "redirect:/roasteries";
        roasteryRepository.save(roastery);
        return "redirect:/roasteries";
    }

    @GetMapping("/roastery/edit/{id}")
    public String editRoastery(@PathVariable Long id, Model model) {
        model.addAttribute("roasteries", roasteryRepository.findAll());
        model.addAttribute("roastery", roasteryRepository.findById(id).orElse(new Roastery()));
        return "roastery_list";
    }

    @PostMapping("/roastery/delete/{id}")
    public String deleteRoastery(@PathVariable Long id) {
        roasteryRepository.findById(id).ifPresent(r -> {
            if (r.getCoffees() == null || r.getCoffees().isEmpty()) roasteryRepository.delete(r);
        });
        return "redirect:/roasteries";
    }

    @GetMapping("/roasteries")
    public String showRoasteries(Model model) {
        model.addAttribute("roasteries", roasteryRepository.findAll());
        model.addAttribute("roastery", new Roastery());
        return "roastery_list";
    }

    @PostMapping("/admin/user/update-role")
    public String updateRole(@RequestParam Long userId, @RequestParam String newRole) {
        if (!"ROLE_USER".equals(newRole) && !"ROLE_ADMIN".equals(newRole)) return "redirect:/admin/users";
        userRepository.findById(userId).ifPresent(u -> {
            String formattedRole = newRole.toUpperCase();
            if (!formattedRole.startsWith("ROLE_")) {
                formattedRole = "ROLE_" + formattedRole;
            }

            u.setRole(formattedRole);
            userRepository.save(u);
        });
        return "redirect:/admin/users";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        List<Coffee> allCoffee = coffeeRepository.findAll();

        double totalValue = allCoffee.stream()
                .mapToDouble(c -> c.getPrice() != null ? c.getPrice().doubleValue() * c.getQuantity() : 0.0)
                .sum();
        int totalPacks = allCoffee.stream().mapToInt(Coffee::getQuantity).sum();
        long lowStockCount = allCoffee.stream().filter(c -> c.getQuantity() < 5).count();

        Map<String, Integer> roasteryStats = new HashMap<>();
        for (Coffee c : allCoffee) {
            String name = (c.getRoastery() != null) ? c.getRoastery().getName() : "Unknown";
            roasteryStats.put(name, roasteryStats.getOrDefault(name, 0) + c.getQuantity());
        }

        List<AuditLog> recentLogs = auditLogRepository.findAll();
        Collections.reverse(recentLogs);
        recentLogs = recentLogs.stream().limit(5).toList();

        model.addAttribute("totalValue", totalValue);
        model.addAttribute("totalPacks", totalPacks);
        model.addAttribute("lowStockCount", lowStockCount);
        model.addAttribute("roasteryLabels", roasteryStats.keySet());
        model.addAttribute("roasteryCounts", roasteryStats.values());
        model.addAttribute("recentLogs", recentLogs);

        return "dashboard";
    }
}