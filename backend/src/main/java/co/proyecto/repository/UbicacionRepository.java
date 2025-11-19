package co.proyecto.repository;

import co.proyecto.model.Ubicacion;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {

    Optional<Ubicacion> findById(Integer id);
    // ...puedes agregar métodos personalizados si lo necesitas...

    List<Ubicacion> findByNombreAndLatAndLng(String nombre, Double lat, Double lng);

    List<Ubicacion> findByLatAndLng(Double lat, Double lng);

    List<Ubicacion> findByNombre(String nombre);

        @Query("SELECT DISTINCT u FROM Ubicacion u " +
            "LEFT JOIN FETCH u.recursosNecesarios " + // List
            "LEFT JOIN FETCH u.equipos")  // reemplazado recursosDisponibles por equipos
        List<Ubicacion> findAllWithRecursos();
}
