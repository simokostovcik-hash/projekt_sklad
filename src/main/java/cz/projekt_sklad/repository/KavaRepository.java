package cz.projekt_sklad.repository;

import cz.projekt_sklad.model.Kava;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KavaRepository extends CrudRepository<Kava, Integer> {
}