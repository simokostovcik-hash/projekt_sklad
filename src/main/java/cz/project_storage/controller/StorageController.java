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
    @Autowired
    private TagRepository tagRepository;

    @PostMapping("/orders/sell")
    public String processPurchase(@RequestParam Long coffeeId,
                                  @RequestParam Integer quantity,
                                  Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        orderService.createQuickOrder(coffeeId, quantity, user);
        return "redirect:/orders/all";
    }

    @GetMapping("/orders/all")
    public String showMyOrders(Model model, Principal principal) {
        if (principal == null) return "redirect:/login";
        User user = userRepository.findByUsername(principal.getName()).orElse(null);
        model.addAttribute("orders", orderRepository.findByUser(user));
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
    public String mainMenu() { return "storage"; }

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
        roasteryRepository.save(roastery);
        return "redirect:/admin/roasteries";
    }

    @GetMapping("/test/generate-data")
    public String generateTestData() {

        orderRepository.deleteAll();
        coffeeRepository.deleteAll();
        roasteryRepository.deleteAll();
        tagRepository.deleteAll();

        Tag acidic = tagRepository.save(new Tag("Acidic"));
        Tag chocolate = tagRepository.save(new Tag("Chocolatey"));
        Tag fruity = tagRepository.save(new Tag("Fruity"));
        Tag nutty = tagRepository.save(new Tag("Nutty"));
        Tag bio = tagRepository.save(new Tag("Bio"));
        Tag spicy = tagRepository.save(new Tag("Spicy"));
        Tag decaf = tagRepository.save(new Tag("Decaf"));

        List<Roastery> ros = List.of(
                new Roastery(null, "Doubleshot", "Czech Republic", "Prague", "https://doubleshot.cz"),
                new Roastery(null, "The Barn", "Germany", "Berlin", "https://thebarn.de"),
                new Roastery(null, "Fathers Coffee", "Czech Republic", "Ostrava", "https://fatherscoffee.cz"),
                new Roastery(null, "Square Mile", "United Kingdom", "London", "https://squaremilecoffee.com"),
                new Roastery(null, "Coffee Collective", "Denmark", "Copenhagen", "https://coffeecollective.dk"),
                new Roastery(null, "Gardelli", "Italy", "Forli", "https://gardellicoffee.com"),
                new Roastery(null, "Koppi", "Sweden", "Helsingborg", "https://koppi.se")
        );
        roasteryRepository.saveAll(ros);
        List<Roastery> dbRos = roasteryRepository.findAll();

        List<Coffee> cfs = new ArrayList<>();

        Coffee c1 = new Coffee(null, "Tam Dem", dbRos.get(0), "Espresso Blend", 50, 85, "In Stock", null);
        c1.setTags(Set.of(chocolate, nutty));
        cfs.add(c1);

        Coffee c2 = new Coffee(null, "Ethiopia Duromina", dbRos.get(0), "Filter", 30, 115, "In Stock", null);
        c2.setTags(Set.of(acidic, fruity));
        cfs.add(c2);

        Coffee c3 = new Coffee(null, "Kenya AA", dbRos.get(1), "Light Roast", 20, 145, "In Stock", null);
        c3.setTags(Set.of(acidic, fruity, bio));
        cfs.add(c3);

        Coffee c4 = new Coffee(null, "Brazil Cerrado", dbRos.get(2), "Omni Roast", 60, 75, "In Stock", null);
        c4.setTags(Set.of(chocolate, nutty));
        cfs.add(c4);

        Coffee c5 = new Coffee(null, "Colombia Huila", dbRos.get(2), "Filter", 40, 95, "In Stock", null);
        c5.setTags(Set.of(fruity, acidic));
        cfs.add(c5);

        Coffee c6 = new Coffee(null, "Red Brick", dbRos.get(3), "Seasonal Blend", 100, 80, "In Stock", null);
        c6.setTags(Set.of(chocolate, fruity));
        cfs.add(c6);

        Coffee c7 = new Coffee(null, "Kieni", dbRos.get(4), "Kenya Wash", 15, 160, "In Stock", null);
        c7.setTags(Set.of(acidic, bio));
        cfs.add(c7);

        Coffee c8 = new Coffee(null, "Uganda Mzungu", dbRos.get(5), "Natural Special", 10, 250, "In Stock", null);
        c8.setTags(Set.of(fruity, spicy));
        cfs.add(c8);


        Coffee c9 = new Coffee(null, "Costa Rica", dbRos.get(6), "White Honey", 25, 130, "In Stock", null);
        c9.setTags(Set.of(fruity, nutty));
        cfs.add(c9);


        Coffee c10 = new Coffee(null, "Nightcap Decaf", dbRos.get(0), "Swiss Water", 20, 95, "In Stock", null);
        c10.setTags(Set.of(decaf, chocolate));
        cfs.add(c10);

        Coffee c11 = new Coffee(null, "Panama Geisha", dbRos.get(5), "Ultra Premium", 0, 550, "Ordered", LocalDate.now().plusDays(10));
        c11.setTags(Set.of(fruity, acidic, bio));
        cfs.add(c11);

        coffeeRepository.saveAll(cfs);

        return "redirect:/coffee/all";
    }
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


    private void prepareInventoryModel(Model model) {
        List<Coffee> list = coffeeRepository.findAll();
        model.addAttribute("coffeeList", list);
        model.addAttribute("count", list.size());
        model.addAttribute("total", list.stream().mapToInt(c -> c.getPrice() * c.getQuantity()).sum());
        model.addAttribute("allRoasteries", roasteryRepository.findAll());
        model.addAttribute("allTags", tagRepository.findAll());
    }
    @Autowired
    private InvoiceService invoiceService;

    @GetMapping("/orders/invoice/{id}")
    public void generateInvoice(HttpServletResponse response, String customerName, String coffeeName, double price) throws IOException {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found"));

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=invoice_" + id + ".pdf");

        String customer = order.getUser().getUsername();

        String product = order.getItems().isEmpty() ? "Coffee" : order.getItems().get(0).getCoffee().getName();
        Double totalPrice = order.getTotalPrice();

        invoiceService.generateInvoice(response, customer, product, totalPrice);
    }

}