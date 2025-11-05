package co.proyecto.repository;

import co.proyecto.model.Recurso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecursoRepository extends JpaRepository<Recurso, Integer> {
    // ...puedes agregar métodos personalizados si lo necesitas...
}
