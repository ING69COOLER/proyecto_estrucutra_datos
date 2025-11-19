package co.proyecto.repository;

import co.proyecto.model.Evacuacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface EvacuacionRepository extends JpaRepository<Evacuacion, Integer> {

    @Query("SELECT e FROM Evacuacion e ORDER BY e.fecha DESC")
    List<Evacuacion> findRecentEvacuaciones();

    // CORRECCIÓN: Cambiamos 'Ubicacion' por 'Origen'
    List<Evacuacion> findByOrigen_Id(int id);
}