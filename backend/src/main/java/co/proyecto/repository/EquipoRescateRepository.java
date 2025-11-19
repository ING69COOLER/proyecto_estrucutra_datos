package co.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.proyecto.model.EquipoRescate;

@Repository
public interface EquipoRescateRepository extends JpaRepository<EquipoRescate, Integer> {

}
