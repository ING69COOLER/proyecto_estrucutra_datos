package co.proyecto.repository;

import co.proyecto.model.Ruta;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RutaRepository extends JpaRepository<Ruta, Integer> {
    // ...puedes agregar métodos personalizados si lo necesitas...

    @Query("SELECT r FROM Ruta r JOIN FETCH r.origen JOIN FETCH r.destino")
    List<Ruta> findAllWithUbicaciones();
}
