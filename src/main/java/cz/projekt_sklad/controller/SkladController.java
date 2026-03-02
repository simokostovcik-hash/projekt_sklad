package cz.projekt_sklad.controller;

import cz.projekt_sklad.model.Kava;
import cz.projekt_sklad.model.Uzivatel;
import cz.projekt_sklad.repository.KavaRepository;
import cz.projekt_sklad.repository.UzivatelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class SkladController {

    @Autowired
    private KavaRepository kavaRepository;

    @Autowired
    private UzivatelRepository uzivatelRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/register")
    public String ukazFormularRegistrace() {
        return "register";
    }

    @PostMapping("/register")
    public String provedRegistraci(@RequestParam String username, @RequestParam String password) {
        Uzivatel novy = new Uzivatel();
        novy.setUsername(username);
        novy.setPassword(passwordEncoder.encode(password));
        novy.setRole("ROLE_ADMIN");
        uzivatelRepository.save(novy);
        return "redirect:/login";
    }

    @GetMapping("/kava/vse")
    public String zobrazSklad(Model model) {
        Iterable<Kava> vsechnyKavy = kavaRepository.findAll();
        model.addAttribute("seznamKav", vsechnyKavy);

        long pocetDruhu = kavaRepository.count();
        int celkovaCena = 0;
        for (Kava k : vsechnyKavy) {
            celkovaCena += k.getCena();
        }

        model.addAttribute("pocet", pocetDruhu);
        model.addAttribute("celkem", celkovaCena);
        return "sklad";
    }

    @GetMapping("/kava/pridat")
    public String formularPridat(Model model) {
        model.addAttribute("kava", new Kava());
        model.addAttribute("typyKav", new String[]{"Arabica", "Robusta", "Espresso Blend", "Bezkofeinová", "Liberecká směs"});
        return "formular";
    }

    @PostMapping("/kava/ulozit")
    public String ulozitKavu(@ModelAttribute Kava kava) {
        if (kava.getNazev() == null || kava.getNazev().isEmpty() || kava.getCena() <= 0 || kava.getGramaz() <= 0) {
            return "redirect:/kava/pridat";
        }
        kavaRepository.save(kava);
        return "redirect:/kava/vse";
    }

    @GetMapping("/kava/smazat/{id}")
    public String smazatKavu(@PathVariable Integer id) {
        kavaRepository.deleteById(id);
        return "redirect:/kava/vse";
    }

    @GetMapping("/kava/upravit/{id}")
    public String upravitKavu(@PathVariable Integer id, Model model) {
        Optional<Kava> kava = kavaRepository.findById(id);
        if (kava.isPresent()) {
            model.addAttribute("kava", kava.get());
            model.addAttribute("typyKav", new String[]{"Arabica", "Robusta", "Espresso Blend", "Bezkofeinová", "Liberecká směs"});
            return "formular";
        }
        return "redirect:/kava/vse";
    }
}