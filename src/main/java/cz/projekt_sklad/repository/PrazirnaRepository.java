package cz.projekt_sklad.repository;

import cz.projekt_sklad.model.Prazirna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PrazirnaRepository extends JpaRepository<Prazirna, Long> {

}