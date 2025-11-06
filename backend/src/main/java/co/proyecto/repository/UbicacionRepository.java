package co.proyecto.repository;

import co.proyecto.model.Ubicacion;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UbicacionRepository extends JpaRepository<Ubicacion, Integer> {

    Optional<Ubicacion> findById(Integer id);
    // ...puedes agregar métodos personalizados si lo necesitas...

}
