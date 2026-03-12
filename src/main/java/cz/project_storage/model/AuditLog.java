package cz.project_storage.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_log")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String user;      // Přejmenováno z uzivatel
    private String action;    // Přejmenováno z akce

    @Column(name = "cas")     // V databázi sloupec zůstane "cas", pokud tam už máš data
    private LocalDateTime timestamp; // Přejmenováno z cas

    public AuditLog() {
    }

    public AuditLog(String user, String action, LocalDateTime timestamp) {
        this.user = user;
        this.action = action;
        this.timestamp = timestamp;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}