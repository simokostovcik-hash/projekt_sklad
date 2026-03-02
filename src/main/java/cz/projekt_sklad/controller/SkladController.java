package cz.projekt_sklad.controller;

import cz.projekt_sklad.model.Kava;
import cz.projekt_sklad.repository.KavaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller; // ZMĚNA: importujeme Controller
import org.springframework.ui.Model; // Pro předávání dat do HTML
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller // ZMĚNA: už ne @RestController, aby fungovalo HTML
public class SkladController {

    @Autowired
    private KavaRepository kavaRepository;

    @GetMapping("/")
    public String uvodniStranka() {
        return "sklad"; // Spring hledá soubor sklad.html v templates
    }

    @GetMapping("/kava/vse")
    public String zobrazSklad(Model model) {
        model.addAttribute("seznamKav", kavaRepository.findAll());
        return "sklad";
    }

    // Metoda pro zobrazení formuláře
    @GetMapping("/kava/pridat")
    public String ukazFormular() {
        return "formular"; // Hledá soubor formular.html
    }

    // Metoda pro uložení dat z formuláře
    @PostMapping("/kava/ulozit")
    public String ulozitKavu(@RequestParam String nazev,
                             @RequestParam int gramaz,
                             @RequestParam int cena) {
        Kava novaKava = new Kava();
        novaKava.setNazev(nazev);
        novaKava.setGramaz(gramaz);
        novaKava.setCena(cena);

        kavaRepository.save(novaKava);

        return "redirect:/kava/vse"; // Po uložení nás pošle zpět na seznam
    }

    @GetMapping("/kava/smazat/{id}")
    public String smazatKavu(@PathVariable Integer id) {
        kavaRepository.deleteById(id);
        return "redirect:/kava/vse";
    }
}