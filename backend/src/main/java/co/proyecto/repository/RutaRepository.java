package co.proyecto.repository;

import co.proyecto.model.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RutaRepository extends JpaRepository<Ruta, Integer> {
    // ...puedes agregar métodos personalizados si lo necesitas...
}
