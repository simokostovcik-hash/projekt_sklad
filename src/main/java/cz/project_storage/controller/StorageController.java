package cz.project_storage.controller;

import cz.project_storage.model.AuditLog;
import cz.project_storage.model.Coffee;
import cz.project_storage.model.Roastery;
import cz.project_storage.model.User;
import cz.project_storage.repository.AuditLogRepository;
import cz.project_storage.repository.CoffeeRepository;
import cz.project_storage.repository.RoasteryRepository;
import cz.project_storage.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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

    // --- TESTOVACÍ GENEROVÁNÍ DAT ---
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
        Roastery ds = dbRos.get(0); Roastery tb = dbRos.get(1); Roastery sm = dbRos.get(2);
        Roastery ga = dbRos.get(3); Roastery na = dbRos.get(4); Roastery fa = dbRos.get(5);
        Roastery cc = dbRos.get(6);

        List<Coffee> cfs = new ArrayList<>();
        cfs.add(new Coffee(null, "Tam Dem", ds, "Espresso Blend", 350, 320, "In Stock", null));
        cfs.add(new Coffee(null, "Ethiopia Duromina", tb, "Filter Arabica", 250, 450, "In Stock", null));
        cfs.add(new Coffee(null, "Brazil Diamond", sm, "Omni Roast", 1000, 750, "In Stock", null));
        cfs.add(new Coffee(null, "Kenya AA Karogoto", ga, "Filter Special", 250, 580, "Ordered", LocalDate.now().plusDays(3)));
        cfs.add(new Coffee(null, "Sample Pack A", na, "Microlot", 50, 120, "Out of Stock", null));
        cfs.add(new Coffee(null, "Colombia Pink Bourbon", fa, "Anaerobic", 250, 690, "In Stock", null));
        cfs.add(new Coffee(null, "Espresso No. 4", cc, "Blend", 500, 480, "In Stock", null));
        cfs.add(new Coffee(null, "Panama Geisha", ga, "Competition", 150, 2450, "Ordered", LocalDate.now().plusDays(10)));
        cfs.add(new Coffee(null, "Morning Glory", ds, "Light Roast", 350, 310, "In Stock", null));
        cfs.add(new Coffee(null, "Decaf Mexico", tb, "Decaf", 250, 380, "Out of Stock", null));
        cfs.add(new Coffee(null, "Red Brick", sm, "Signature Blend", 350, 420, "In Stock", null));
        cfs.add(new Coffee(null, "Rwanda Huye", fa, "Washed Arabica", 250, 395, "In Stock", null));
        cfs.add(new Coffee(null, "Guatemala Antigua", cc, "Filter", 250, 410, "In Stock", null));
        cfs.add(new Coffee(null, "Winter Solstice", ds, "Seasonal Blend", 500, 450, "Out of Stock", null));
        cfs.add(new Coffee(null, "Buna Boka", tb, "Natural", 250, 430, "Ordered", LocalDate.now().plusDays(2)));
        cfs.add(new Coffee(null, "Costa Rica Tarrazu", sm, "Honey Process", 250, 490, "In Stock", null));
        cfs.add(new Coffee(null, "Uganda Rwenzori", na, "Natural", 1000, 820, "In Stock", null));
        cfs.add(new Coffee(null, "Holiday Roast", fa, "Limited Edition", 250, 550, "In Stock", null));
        cfs.add(new Coffee(null, "Kenya Kii", cc, "Filter", 250, 520, "In Stock", null));
        cfs.add(new Coffee(null, "Espresso Classic", ds, "Dark Roast", 1000, 680, "In Stock", null));

        coffeeRepository.saveAll(cfs);
        return "redirect:/coffee/all";
    }


    @GetMapping("/menu")
    public String mainMenu() {
        return "storage";
    }

    private void prepareInventoryModel(Model model) {
        List<Coffee> list = coffeeRepository.findAll();
        model.addAttribute("coffeeList", list);
        model.addAttribute("count", list.size());
        model.addAttribute("total", list.stream().mapToInt(Coffee::getPrice).sum());
        model.addAttribute("allRoasteries", roasteryRepository.findAll());
    }

    @GetMapping("/coffee/all")
    public String showInventory(Model model) {
        model.addAttribute("coffee", new Coffee());
        prepareInventoryModel(model);
        return "coffee_list";
    }

    @GetMapping("/coffee/edit/{id}")
    public String editCoffee(@PathVariable Long id, Model model) {
        Coffee coffee = coffeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid coffee Id:" + id));
        model.addAttribute("coffee", coffee);
        prepareInventoryModel(model);
        return "coffee_list";
    }

    @PostMapping("/coffee/save")
    public String saveCoffee(@ModelAttribute Coffee coffee, Principal principal) {
        String author = (principal != null) ? principal.getName() : "System";
        String action = (coffee.getId() == null) ? "Added coffee: " : "Updated coffee: ";
        coffeeRepository.save(coffee);
        auditLogRepository.save(new AuditLog(author, action + coffee.getName(), LocalDateTime.now()));
        return "redirect:/coffee/all";
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

    @GetMapping("/admin/roastery/delete/{id}")
    public String deleteRoastery(@PathVariable Long id) {
        roasteryRepository.deleteById(id);
        return "redirect:/admin/roasteries";
    }

    @GetMapping("/audit")
    public String showAudit(Model model) {
        model.addAttribute("auditLogs", auditLogRepository.findAllByOrderByTimestampDesc());
        model.addAttribute("users", userRepository.findAll());
        return "admin_panel";
    }

    @GetMapping("/admin/users")
    public String showAdminPanel(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("auditLogs", auditLogRepository.findAllByOrderByTimestampDesc());
        return "admin_panel";
    }

    @PostMapping("/admin/change-role")
    public String changeRole(@RequestParam Long id, @RequestParam String newRole, Principal principal) {
        String author = (principal != null) ? principal.getName() : "System";
        userRepository.findById(id).ifPresent(u -> {
            if (!u.getUsername().equals("admin")) {
                u.setRole(newRole);
                userRepository.save(u);
                auditLogRepository.save(new AuditLog(author, "Role change for " + u.getUsername() + " to " + newRole, LocalDateTime.now()));
            }
        });
        return "redirect:/admin/users";
    }

    @GetMapping("/register")
    public String showRegistration(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute User user, Model model) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            model.addAttribute("error", "User with this username already exists!");
            return "register";
        }
        user.setRole("ROLE_USER");
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return "redirect:/login?success";
    }

    @GetMapping("/admin/roasteries")
    public String listRoasteries(Model model) {
        model.addAttribute("roastery", new Roastery());
        model.addAttribute("allRoasteries", roasteryRepository.findAll());
        return "roastery_list";
    }


    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/admin/roastery/new")
    public String newRoasteryForm(Model model) {
        return "redirect:/admin/roasteries";
    }

    @GetMapping("/admin/roastery/edit/{id}")
    public String editRoasteryForm(@PathVariable Long id, Model model) {
        Roastery r = roasteryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid roastery Id:" + id));
        model.addAttribute("roastery", r);
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
}