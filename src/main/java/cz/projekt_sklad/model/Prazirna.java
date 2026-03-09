package cz.projekt_sklad.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "prazirny")
public class Prazirna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nazev;

    private String adresa;

    private String zeme;

    private String web;

    @OneToMany(mappedBy = "prazirna", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Kava> seznamKav = new ArrayList<>();

    public Prazirna() {}

    public Prazirna(String nazev, String adresa, String zeme) {
        this.nazev = nazev;
        this.adresa = adresa;
        this.zeme = zeme;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNazev() { return nazev; }
    public void setNazev(String nazev) { this.nazev = nazev; }

    public String getAdresa() { return adresa; }
    public void setAdresa(String adresa) { this.adresa = adresa; }

    public String getZeme() { return zeme; }
    public void setZeme(String zeme) { this.zeme = zeme; }

    public String getWeb() { return web; }
    public void setWeb(String web) { this.web = web; }

    public List<Kava> getSeznamKav() { return seznamKav; }
    public void setSeznamKav(List<Kava> seznamKav) { this.seznamKav = seznamKav; }
}