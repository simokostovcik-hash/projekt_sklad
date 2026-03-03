package cz.projekt_sklad.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String uzivatel;
    private String akce;

    @Column(name = "cas")
    private LocalDateTime cas;

    public AuditLog() {
    }

    public AuditLog(String uzivatel, String akce, LocalDateTime cas) {
        this.uzivatel = uzivatel;
        this.akce = akce;
        this.cas = cas;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUzivatel() { return uzivatel; }
    public void setUzivatel(String uzivatel) { this.uzivatel = uzivatel; }

    public String getAkce() { return akce; }
    public void setAkce(String akce) { this.akce = akce; }

    public LocalDateTime getCas() { return cas; }
    public void setCas(LocalDateTime cas) { this.cas = cas; }
}