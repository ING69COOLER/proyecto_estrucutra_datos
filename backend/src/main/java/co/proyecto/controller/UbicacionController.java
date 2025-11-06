package co.proyecto.controller;

import co.proyecto.model.Ubicacion;
import co.proyecto.repository.UbicacionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/zonas")
@CrossOrigin(origins = "*")
public class UbicacionController {

    private final UbicacionRepository ubicacionRepository;

    public UbicacionController(UbicacionRepository ubicacionRepository) {
        this.ubicacionRepository = ubicacionRepository;
    }
    //obtiene todas las ubicaciones
    @GetMapping
    public ResponseEntity<List<Ubicacion>> getAll() {
        try {
            List<Ubicacion> ubicaciones = ubicacionRepository.findAll();
            return ResponseEntity.ok(ubicaciones);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    //guarda una ubicacion
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Ubicacion ubicacion) {
        try {
            System.out.println("Datos recibidos: " + ubicacion);
            
            // Validaciones
            if (ubicacion.getNombre() == null || ubicacion.getNombre().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("El nombre es requerido");
            }

            ubicacion.setUpdatedAt(new java.util.Date());
            
            // Intentar guardar
            Ubicacion savedUbicacion = ubicacionRepository.save(ubicacion);
            System.out.println("Ubicación guardada correctamente: " + savedUbicacion);
            
            return ResponseEntity.ok(savedUbicacion);
        } catch (Exception e) {
            System.err.println("Error al guardar ubicación: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al guardar: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUbicacionById(@PathVariable int id) {
        try {
            return ubicacionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
