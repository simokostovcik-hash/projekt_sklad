package cz.projekt_sklad.model;

import jakarta.persistence.*;
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

    private String zemePuvodu;

    @OneToMany(mappedBy = "prazirna", cascade = CascadeType.ALL)
    private List<Kava> seznamKav;

    public Prazirna() {
    }

    public Prazirna(String nazev, String adresa, String zemePuvodu) {
        this.nazev = nazev;
        this.adresa = adresa;
        this.zemePuvodu = zemePuvodu;
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

    public String getAdresa() {
        return adresa;
    }

    public void setAdresa(String adresa) {
        this.adresa = adresa;
    }

    public String getZemePuvodu() {
        return zemePuvodu;
    }

    public void setZemePuvodu(String zemePuvodu) {
        this.zemePuvodu = zemePuvodu;
    }

    public List<Kava> getSeznamKav() {
        return seznamKav;
    }

    public void setSeznamKav(List<Kava> seznamKav) {
        this.seznamKav = seznamKav;
    }
}