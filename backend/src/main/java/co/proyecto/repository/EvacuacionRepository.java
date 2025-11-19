package co.proyecto.repository;

import co.proyecto.model.Evacuacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EvacuacionRepository extends JpaRepository<Evacuacion, Integer> {
    List<Evacuacion> findByUbicacionId(int ubicacionId);
}
