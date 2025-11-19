package co.proyecto.controller;

import co.proyecto.logic.Clientes.RutaClient;
import co.proyecto.model.Evacuacion;
import co.proyecto.model.Ubicacion;
import co.proyecto.repository.EvacuacionRepository;
import co.proyecto.repository.UbicacionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/evacuacion")
public class EvacuacionController {

    private final EvacuacionRepository evacuacionRepository;
    private final UbicacionRepository ubicacionRepository;

    public EvacuacionController(EvacuacionRepository evacuacionRepository, UbicacionRepository ubicacionRepository) {
        this.evacuacionRepository = evacuacionRepository;
        this.ubicacionRepository = ubicacionRepository;
    }

    @GetMapping
    @Transactional
    public ResponseEntity<List<Evacuacion>> getAll() {
        try {
            List<Evacuacion> evacuaciones = evacuacionRepository.findAll();
            return ResponseEntity.ok(evacuaciones);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    

    @GetMapping("/{id}")
    public ResponseEntity<?> getEvacuacionById(@PathVariable int id) {
        try {
            return evacuacionRepository.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    // ... imports

    // En EvacuacionController.java

    @GetMapping("/ubicacion/{ubicacionId}")
    public ResponseEntity<List<Evacuacion>> getEvacuacionesByUbicacion(@PathVariable int ubicacionId) {
        try {
            // CORRECCIÓN: Llamar al método actualizado findByOrigen_Id
            List<Evacuacion> evacuaciones = evacuacionRepository.findByOrigen_Id(ubicacionId);
            return ResponseEntity.ok(evacuaciones);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    // ... resto del controlador

    @GetMapping("/historial")
    @Transactional
    public ResponseEntity<List<Evacuacion>> getHistorialReciente() {
        try {
            List<Evacuacion> evacuaciones = evacuacionRepository.findRecentEvacuaciones();
            return ResponseEntity.ok(evacuaciones);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<?> updateEvacuacion(@PathVariable int id, @RequestBody Evacuacion evacuacion) {
        try {
            Optional<Evacuacion> existente = evacuacionRepository.findById(id);
            if (existente.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Evacuacion e = existente.get();
            e.setPersonasEvacuadas(evacuacion.getPersonasEvacuadas());
            e.setPrioridad(evacuacion.getPrioridad());
            e.setEstado(evacuacion.getEstado());

            Evacuacion updated = evacuacionRepository.save(e);
            return ResponseEntity.ok(updated);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al actualizar: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> deleteEvacuacion(@PathVariable int id) {
        try {
            if (!evacuacionRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            evacuacionRepository.deleteById(id);
            return ResponseEntity.ok("Evacuación eliminada correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al eliminar: " + e.getMessage());
        }
    }

    @PostMapping
    @Transactional // Importante: Si falla algo, se revierten la resta y la suma
    public ResponseEntity<?> create(@RequestBody Evacuacion evacuacion) {
        try {
            // 1. Validar datos básicos
            if (evacuacion.getPersonasEvacuadas() <= 0) {
                return ResponseEntity.badRequest().body("La cantidad debe ser mayor a 0");
            }
            if (evacuacion.getOrigen() == null || evacuacion.getDestino() == null) {
                return ResponseEntity.badRequest().body("Se requiere origen y destino");
            }

            // 2. Obtener las ubicaciones reales de la BD
            Ubicacion origen = ubicacionRepository.findById(evacuacion.getOrigen().getId())
                    .orElseThrow(() -> new RuntimeException("Origen no encontrado"));
            
            Ubicacion destino = ubicacionRepository.findById(evacuacion.getDestino().getId())
                    .orElseThrow(() -> new RuntimeException("Destino no encontrado"));

            // 3. Validar que haya gente suficiente para restar
            if (origen.getPersonasAfectadas() < evacuacion.getPersonasEvacuadas()) {
                return ResponseEntity.badRequest()
                    .body("No hay suficientes personas en el origen. Disponibles: " + origen.getPersonasAfectadas());
            }

            // 4. LÓGICA DE MOVIMIENTO (Resta y Suma)
            // Restamos del origen (porque ya no están en riesgo allí)
            origen.setPersonasAfectadas(origen.getPersonasAfectadas() - evacuacion.getPersonasEvacuadas());
            
            // Sumamos al destino (ahora están seguros allí)
            destino.setPersonasAfectadas(destino.getPersonasAfectadas() + evacuacion.getPersonasEvacuadas());

            // Guardamos los cambios en las ubicaciones
            ubicacionRepository.save(origen);
            ubicacionRepository.save(destino);

            // 5. Preparar el registro de Evacuación (El "recibo")
            // Calcular distancia (opcional, si tienes el cliente de rutas)
            try {
                RutaClient cliente = new RutaClient();
                Double dist = cliente.calcularDistancia(origen.getLat(), origen.getLng(), destino.getLat(), destino.getLng());
                evacuacion.setDistancia(dist);
            } catch (Exception e) {
                evacuacion.setDistancia(0.0); // Si falla el cálculo, ponemos 0
            }

            evacuacion.setOrigen(origen);
            evacuacion.setDestino(destino);
            if (evacuacion.getFecha() == null) evacuacion.setFecha(new Date());
            
            // Guardamos el registro
            Evacuacion savedEvacuacion = evacuacionRepository.save(evacuacion);

            return ResponseEntity.ok(savedEvacuacion);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error en la evacuación: " + e.getMessage());
        }
    }
}
