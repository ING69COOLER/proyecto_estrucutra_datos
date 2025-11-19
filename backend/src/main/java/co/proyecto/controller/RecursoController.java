package co.proyecto.controller;

import co.proyecto.model.Recurso;
import co.proyecto.model.Ubicacion;
import co.proyecto.repository.RecursoRepository;
import co.proyecto.repository.UbicacionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.web.bind.annotation.GetMapping;


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
        return recursoRepository.save(recurso);
    }

    @PutMapping("/{id}/asignar")
    public ResponseEntity<?> asignarUbicacion(@PathVariable int id, @RequestBody Map<String, Integer> body) {
        try {
            Optional<Recurso> rOpt = recursoRepository.findById(id);
            if (!rOpt.isPresent()) return ResponseEntity.status(404).body("Recurso no encontrado");

            Integer ubicacionId = body.get("ubicacionId");
            if (ubicacionId == null) return ResponseEntity.badRequest().body("ubicacionId requerido");

            if (ubicacionRepository == null) return ResponseEntity.status(500).body("UbicacionRepository no disponible");

            Optional<Ubicacion> uOpt = ubicacionRepository.findById(ubicacionId);
            if (!uOpt.isPresent()) return ResponseEntity.status(404).body("Ubicacion no encontrada");

            Recurso recurso = rOpt.get();
            // Si ya está asignado a una ubicación distinta, no permitir reasignar
            if (recurso.getUbicacion() != null && recurso.getUbicacion().getId() != ubicacionId) {
                return ResponseEntity.status(409).body("Recurso ya asignado a otra ubicación");
            }

            recurso.setUbicacion(uOpt.get());
            // Marcar como no disponible cuando esté asignado
            recurso.setDisponible(false);
            recursoRepository.save(recurso);
            return ResponseEntity.ok(recurso);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("/{id}/desasignar")
    public ResponseEntity<?> desasignarUbicacion(@PathVariable int id) {
        try {
            Optional<Recurso> rOpt = recursoRepository.findById(id);
            if (!rOpt.isPresent()) return ResponseEntity.status(404).body("Recurso no encontrado");

            Recurso recurso = rOpt.get();
            recurso.setUbicacion(null);
            // Marcar como disponible al desasignar
            recurso.setDisponible(true);
            recursoRepository.save(recurso);
            return ResponseEntity.ok(recurso);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

   

    // ...puedes agregar PUT, DELETE, etc. según lo necesite el frontend...
}
