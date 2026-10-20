package cz.project_storage.model;

import jakarta.persistence.*;

/** Read-only reference to historical products in the old COFFEE table. */
@Entity
@Table(name = "coffee")
public class LegacyCoffee {
    @Id
    private Long id;
    private String name;

    protected LegacyCoffee() {}
    public Long getId() { return id; }
    public String getName() { return name; }
}
