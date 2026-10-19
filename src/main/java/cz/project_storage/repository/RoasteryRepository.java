package cz.project_storage.repository;

import cz.project_storage.model.Roastery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoasteryRepository extends JpaRepository<Roastery, Long> {
}