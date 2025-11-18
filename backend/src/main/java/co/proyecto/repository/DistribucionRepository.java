package co.proyecto.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import co.proyecto.model.Distribucion;
import co.proyecto.model.Ubicacion;

public interface DistribucionRepository extends JpaRepository<Distribucion, Long> {
    
    List<Distribucion> findByDestinoOrderByFechaDistribucionDesc(Ubicacion destino);
    
    List<Distribucion> findByOrigenOrderByFechaDistribucionDesc(Ubicacion origen);
    
    @Query("SELECT d FROM Distribucion d ORDER BY d.fechaDistribucion DESC")
    List<Distribucion> findAllOrderByFechaDesc();
    
    List<Distribucion> findByEstado(String estado);
}
