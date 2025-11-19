package co.proyecto.controller;

import co.proyecto.model.Evacuacion;
import co.proyecto.model.Ubicacion;
import co.proyecto.model.enums.EstadoEvacuacion;
import co.proyecto.repository.EvacuacionRepository;
import co.proyecto.repository.UbicacionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/evacuaciones")
public class EvacuacionController {
    private final EvacuacionRepository evacuacionRepository;
    private final UbicacionRepository ubicacionRepository;

    public EvacuacionController(EvacuacionRepository evacuacionRepository, UbicacionRepository ubicacionRepository) {
        this.evacuacionRepository = evacuacionRepository;
        this.ubicacionRepository = ubicacionRepository;
    }

    // Registrar una evacuación
    @PostMapping
    @Transactional
    public ResponseEntity<?> registrarEvacuacion(@RequestBody EvacuacionRequest req) {
        Optional<Ubicacion> ubicacionOpt = ubicacionRepository.findById(req.ubicacionId);
        if (ubicacionOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Ubicación no encontrada");
        }
        Ubicacion ubicacion = ubicacionOpt.get();
        if (req.personasEvacuadas < 1 || req.personasEvacuadas > ubicacion.getPersonasAfectadas()) {
            return ResponseEntity.badRequest().body("Cantidad inválida");
        }
        // Actualizar personas afectadas
        ubicacion.setPersonasAfectadas(ubicacion.getPersonasAfectadas() - req.personasEvacuadas);
        ubicacion.setUpdatedAt(new Date());
        ubicacionRepository.save(ubicacion);

        // Buscar destino de reubicación si se especifica
        Ubicacion destino = null;
        if (req.destinoReubicacionId != null) {
            destino = ubicacionRepository.findById(req.destinoReubicacionId).orElse(null);
        }

        // Registrar evacuación
        Evacuacion ev = new Evacuacion();
        ev.setUbicacion(ubicacion);
        ev.setPersonasEvacuadas(req.personasEvacuadas);
        ev.setEstado(EstadoEvacuacion.COMPLETADA);
        ev.setPrioridad(0); // O ajustar según lógica
        ev.setDestinoReubicacion(destino);
        evacuacionRepository.save(ev);
        return ResponseEntity.ok(ev);
    }

    // Listar todas las evacuaciones
    @GetMapping
    public List<Evacuacion> listarEvacuaciones() {
        return evacuacionRepository.findAll();
    }

    // Listar evacuaciones por ubicación
    @GetMapping("/ubicacion/{id}")
    public List<Evacuacion> listarPorUbicacion(@PathVariable int id) {
        return evacuacionRepository.findByUbicacionId(id);
    }

    // DTO para request
    public static class EvacuacionRequest {
        public int ubicacionId;
        public int personasEvacuadas;
        public Integer destinoReubicacionId; // Nuevo: id de la zona destino de reubicación (opcional)
    }
}
