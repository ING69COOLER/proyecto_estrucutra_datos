package co.proyecto.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.proyecto.model.Recurso;
import co.proyecto.model.Ubicacion;
import co.proyecto.repository.RecursoRepository;
import co.proyecto.repository.UbicacionRepository;


@RestController
@RequestMapping("/api/recursos")
public class RecursoController {

    private final RecursoRepository recursoRepository;
    private final UbicacionRepository ubicacionRepository;

    public RecursoController(RecursoRepository recursoRepository, UbicacionRepository ubicacionRepository) {
        this.recursoRepository = recursoRepository;
        this.ubicacionRepository = ubicacionRepository;
    }

    @GetMapping
    public List<Recurso> getAll() {
        return recursoRepository.findAll();
    }

    @PostMapping
    public Recurso create(@RequestBody Recurso recurso) {
        // Si el payload trae { "ubicacion": { "id": N } }, reemplazamos por la entidad gestionada
        if (recurso.getUbicacion() != null && recurso.getUbicacion().getId() != 0) {
            int id = recurso.getUbicacion().getId();
            ubicacionRepository.findById(id).ifPresentOrElse(
                u -> recurso.setUbicacion(u),
                () -> { /* si no existe, dejamos como estaba (posible FK inválida) */ }
            );
        }

        return recursoRepository.save(recurso);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        recursoRepository.deleteById(id);
    }

    // ...puedes agregar PUT, etc. según lo necesite el frontend...
}
