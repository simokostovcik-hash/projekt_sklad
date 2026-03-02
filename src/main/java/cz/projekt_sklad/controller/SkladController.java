package cz.projekt_sklad.controller;

import cz.projekt_sklad.model.Kava;
import cz.projekt_sklad.repository.KavaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SkladController {

    @Autowired
    private KavaRepository kavaRepository; // Propojení s databází

    @GetMapping("/")
    public String uvodniStranka() {
        return "<h1>Vítejte v systému Sklad Kávy</h1>" +
                "<ul>" +
                "<li><a href='/kava/vse'>Zobrazit všechny kávy</a></li>" +
                "<li><a href='/kava/pridat-test'>Přidat testovací kávu</a></li>" +
                "</ul>";
    }

    @GetMapping("/kava/vse")
    public Iterable<Kava> getVsechnyKavy() {
        // Tohle vrátí data z databáze ve formátu JSON
        return kavaRepository.findAll();
    }

    @GetMapping("/kava/pridat-test")
    public String pridatTest() {
        Kava testKava = new Kava();
        testKava.setNazev("Testovací Arabica");
        testKava.setCena(250);
        testKava.setGramaz(500);

        kavaRepository.save(testKava); // Uloží kávu do SQLite

        return "Káva byla uložena! <a href='/'>Zpět na hlavní stránku</a>";
    }
}