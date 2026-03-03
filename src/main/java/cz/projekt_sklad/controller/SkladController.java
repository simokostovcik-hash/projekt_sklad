package cz.projekt_sklad.controller;

import cz.projekt_sklad.model.AuditLog;
import cz.projekt_sklad.model.Kava;
import cz.projekt_sklad.model.Uzivatel;
import cz.projekt_sklad.repository.AuditLogRepository;
import cz.projekt_sklad.repository.KavaRepository;
import cz.projekt_sklad.repository.UzivatelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class SkladController {

    @Autowired
    private KavaRepository kavaRepository;

    @Autowired
    private UzivatelRepository uzivatelRepository;

    @Autowired
    private AuditLogRepository auditLogRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // --- SKLAD ---
    @GetMapping("/kava/vse")
    public String zobrazSklad(Model model) {
        List<Kava> seznam = kavaRepository.findAll();
        model.addAttribute("seznamKav", seznam);
        model.addAttribute("pocet", seznam.size());
        int celkem = seznam.stream().mapToInt(Kava::getCena).sum();
        model.addAttribute("celkem", celkem);
        return "sklad";
    }

    @GetMapping("/kava/pridat")
    public String zobrazFormular(Model model) {
        model.addAttribute("kava", new Kava());
        return "formular";
    }

    @PostMapping("/kava/ulozit")
    public String ulozKavu(@ModelAttribute Kava kava, Principal principal) {
        String autor = (principal != null) ? principal.getName() : "System";
        kavaRepository.save(kava);
        auditLogRepository.save(new AuditLog(autor, "Přidána/Upravena káva: " + kava.getNazev(), LocalDateTime.now()));
        return "redirect:/kava/vse";
    }

    @GetMapping("/kava/smazat/{id}")
    public String smazatKavu(@PathVariable Long id, Principal principal) {
        String autor = (principal != null) ? principal.getName() : "System";
        kavaRepository.findById(id).ifPresent(k -> {
            auditLogRepository.save(new AuditLog(autor, "Smazána káva: " + k.getNazev(), LocalDateTime.now()));
            kavaRepository.delete(k);
        });
        return "redirect:/kava/vse";
    }

    @GetMapping("/admin/uzivatele")
    public String zobrazAdminPanel(Model model) {
        model.addAttribute("uzivatele", uzivatelRepository.findAll());
        model.addAttribute("auditLogy", auditLogRepository.findAll());
        return "admin_panel";
    }

    @PostMapping("/admin/zmenit-roli")
    public String zmenitRoli(@RequestParam Long id, @RequestParam String novaRole, Principal principal) {
        String autor = (principal != null) ? principal.getName() : "System";
        uzivatelRepository.findById(id).ifPresent(u -> {
            if (!u.getUsername().equals("admin")) {
                u.setRole(novaRole);
                uzivatelRepository.save(u);
                auditLogRepository.save(new AuditLog(autor, "Změna role u " + u.getUsername() + " na " + novaRole, LocalDateTime.now()));
            }
        });
        return "redirect:/admin/uzivatele";
    }

    @GetMapping("/register")
    public String ukazRegistraci(Model model) {
        model.addAttribute("uzivatel", new Uzivatel());
        return "register";
    }

    @PostMapping("/register")
    public String provedRegistraci(@ModelAttribute Uzivatel uzivatel) {
        uzivatel.setRole("ROLE_USER");
        uzivatel.setPassword(passwordEncoder.encode(uzivatel.getPassword()));
        uzivatelRepository.save(uzivatel);
        return "redirect:/login?success";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}