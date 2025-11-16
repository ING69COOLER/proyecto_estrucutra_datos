package co.proyecto.controller;

import co.proyecto.model.Ubicacion;
import co.proyecto.repository.UbicacionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/api/zonas")
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
            System.out.println("/*/*/*/////*/*/*/*//*/*/**/*/*///*/*/***/*/*/*/*/");
            for (Ubicacion ubicacion : ubicaciones) {
                System.out.println(ubicacion.toString());
            }
            return ResponseEntity.ok(ubicaciones);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Ubicacion ubicacion) {
    try {
        for(int i = 0; i<100; i++){
            System.out.println(i);
        }
        System.out.print(ubicacion.getNivelRiesgo());
        System.out.println("Datos recibidos: " + ubicacion);

        // Validación de campos obligatorios
        if (ubicacion.getNombre() == null || ubicacion.getNombre().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre es requerido");
        }

        ubicacion.setUpdatedAt(new java.util.Date());

        // Validación de duplicados
            boolean existeDuplicado =
                !ubicacionRepository.findByLatAndLng(ubicacion.getLat(), ubicacion.getLng()).isEmpty();

            if (existeDuplicado) {
                return ResponseEntity.status(409).body("Ya existe una ubicación con esos datos");
            }

        // Guardar
            Ubicacion savedUbicacion = ubicacionRepository.save(ubicacion);
            System.out.println("Ubicación guardada correctamente: " + savedUbicacion);

            return ResponseEntity.ok(savedUbicacion);

        } catch (Exception e) {
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
