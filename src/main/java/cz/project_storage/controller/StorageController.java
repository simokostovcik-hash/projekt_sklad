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
import java.time.LocalDateTime;
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

    @GetMapping("/menu")
    public String mainMenu() {
        return "storage";
    }

    @GetMapping("/coffee/all")
    public String showInventory(Model model) {
        model.addAttribute("coffee", new Coffee());
        List<Coffee> list = coffeeRepository.findAll();
        model.addAttribute("coffeeList", list);
        model.addAttribute("count", list.size());
        int total = list.stream().mapToInt(c -> c.getPrice()).sum();
        model.addAttribute("total", total);

        model.addAttribute("allRoasteries", roasteryRepository.findAll());

        return "formular";
    }

    @PostMapping("/coffee/save")
    public String saveCoffee(@ModelAttribute Coffee coffee, Principal principal) {
        String author = (principal != null) ? principal.getName() : "System";
        coffeeRepository.save(coffee);
        auditLogRepository.save(new AuditLog(author, "Added/Updated coffee: " + coffee.getName(), LocalDateTime.now()));
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

    @GetMapping("/admin/roasteries")
    public String listRoasteries(Model model) {
        model.addAttribute("allRoasteries", roasteryRepository.findAll());
        return "roastery_list";
    }

    @GetMapping("/admin/roastery/new")
    public String newRoasteryForm(Model model) {
        model.addAttribute("roastery", new Roastery());
        return "roastery_form";
    }

    @PostMapping("/admin/roastery/save")
    public String saveRoastery(@ModelAttribute("roastery") Roastery roastery) {
        roasteryRepository.save(roastery);
        return "redirect:/admin/roasteries";
    }

    @GetMapping("/admin/roastery/delete/{id}")
    public String deleteRoastery(@PathVariable Long id) {
        roasteryRepository.deleteById(id);
        return "redirect:/admin/roasteries";
    }

    @GetMapping("/admin/roastery/edit/{id}")
    public String editRoasteryForm(@PathVariable Long id, Model model) {
        roasteryRepository.findById(id).ifPresent(r -> model.addAttribute("roastery", r));
        return "roastery_form";
    }

    @GetMapping("/audit")
    public String showAudit(Model model) {
        model.addAttribute("auditLogs", auditLogRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        return "admin_panel";
    }

    @GetMapping("/admin/users")
    public String showAdminPanel(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("auditLogs", auditLogRepository.findAll());
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

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}