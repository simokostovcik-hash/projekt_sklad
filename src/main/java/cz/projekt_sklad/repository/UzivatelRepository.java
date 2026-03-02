package cz.projekt_sklad.repository;

import cz.projekt_sklad.model.Uzivatel;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UzivatelRepository extends JpaRepository<Uzivatel, Long> {
    Optional<Uzivatel> findByUsername(String username);
}