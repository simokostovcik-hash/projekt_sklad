package cz.projekt_sklad.model;

import jakarta.persistence.*;

@Entity
public class Kava {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nazev;
    private int gramaz;
    private int cena;

    public Kava() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNazev() { return nazev; }
    public void setNazev(String nazev) { this.nazev = nazev; }
    public int getGramaz() { return gramaz; }
    public void setGramaz(int gramaz) { this.gramaz = gramaz; }
    public int getCena() { return cena; }
    public void setCena(int cena) { this.cena = cena; }
}