package cz.projekt_sklad.repository;

import cz.projekt_sklad.model.Kava;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KavaRepository extends JpaRepository<Kava, Long> {
    List<Kava> findByNazevContainingIgnoreCase(String nazev);
}