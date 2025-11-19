package co.proyecto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import co.proyecto.model.EquipoRescate;
import java.util.List;

public interface EquipoRescateRepository extends JpaRepository<EquipoRescate, Integer> {

	// Buscar equipos asignados a una ubicación (por id)
	List<EquipoRescate> findByUbicacionId(Integer ubicacionId);

}
