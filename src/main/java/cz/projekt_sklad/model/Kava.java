package cz.projekt_sklad.model;

import jakarta.persistence.*;

@Entity
@Table(name = "kavy")
public class Kava {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nazev;

    private String druh;

    private int cena;

    private int mnozstvi;

    @ManyToOne
    @JoinColumn(name = "prazirna_id")
    private Prazirna prazirna;

    public Kava() {
    }

    public Kava(String nazev, String druh, int cena, int mnozstvi, Prazirna prazirna) {
        this.nazev = nazev;
        this.druh = druh;
        this.cena = cena;
        this.mnozstvi = mnozstvi;
        this.prazirna = prazirna;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNazev() {
        return nazev;
    }

    public void setNazev(String nazev) {
        this.nazev = nazev;
    }

    public String getDruh() {
        return druh;
    }

    public void setDruh(String druh) {
        this.druh = druh;
    }

    public int getCena() {
        return cena;
    }

    public void setCena(int cena) {
        this.cena = cena;
    }

    public int getMnozstvi() {
        return mnozstvi;
    }

    public void setMnozstvi(int mnozstvi) {
        this.mnozstvi = mnozstvi;
    }

    public Prazirna getPrazirna() {
        return prazirna;
    }

    public void setPrazirna(Prazirna prazirna) {
        this.prazirna = prazirna;
    }
}