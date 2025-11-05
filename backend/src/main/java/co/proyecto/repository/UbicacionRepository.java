package co.proyecto.repository;

import co.proyecto.model.Ubicacion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {
    // ...puedes agregar métodos personalizados si lo necesitas...
}
