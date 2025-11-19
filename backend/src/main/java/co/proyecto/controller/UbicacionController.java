package co.proyecto.controller;

import co.proyecto.estructuras.ColaPrioridad;
import co.proyecto.model.Ubicacion;
import co.proyecto.repository.UbicacionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import jakarta.servlet.http.HttpSession;
import co.proyecto.model.Usuario;
import co.proyecto.model.enums.Rol;


@RestController
@RequestMapping("/api/zonas")
public class UbicacionController {

    private final UbicacionRepository ubicacionRepository;
    private final ColaPrioridad colaPrioridad;
    private final co.proyecto.logic.estructuraGrafo.Grafo grafo;

    public UbicacionController(UbicacionRepository ubicacionRepository, ColaPrioridad colaPrioridad,
                               co.proyecto.logic.estructuraGrafo.Grafo grafo) {
        this.ubicacionRepository = ubicacionRepository;
        this.colaPrioridad = colaPrioridad;
        this.grafo = grafo;
    }
    //obtiene todas las ubicaciones
    @GetMapping
    @Transactional
    public ResponseEntity<List<Ubicacion>> getAll() {
        try {
            List<Ubicacion> ubicaciones = ubicacionRepository.findAllWithRecursos();
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
    @Transactional
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUbicacion(@PathVariable int id, HttpSession session) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            if (usuario == null) return ResponseEntity.status(401).body("No autenticado");
            if (usuario.getRol() != Rol.ADMINISTRADOR) return ResponseEntity.status(403).body("Acceso denegado");

            return ubicacionRepository.findById(id).map(u -> {
                try {
                    ubicacionRepository.delete(u);
                    return ResponseEntity.ok().body("Ubicacion eliminada");
                } catch (Exception ex) {
                    return ResponseEntity.status(500).body("Error al eliminar: " + ex.getMessage());
                }
            }).orElse(ResponseEntity.status(404).body("Ubicacion no encontrada"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/prioridad")
    @Transactional
    public ResponseEntity<List<Ubicacion>> getPriority(){
        List<Ubicacion> ubicacionesP = new ArrayList<>();
        while (colaPrioridad.peek() != null) {
            ubicacionesP.add(colaPrioridad.pool());
        }
        colaPrioridad.cargarTodo();
        return ResponseEntity.ok(ubicacionesP);
    }

    // Nuevo: obtener caminos desde todos los orígenes hacia una ubicación destino (usa el Grafo singleton)
    @GetMapping("/caminos/{id}")
    public ResponseEntity<?> getCaminosHaciaDestino(@PathVariable int id) {
        try {
            return ubicacionRepository.findById(id)
                    .map(ubicacion -> {
                        List<co.proyecto.dto.CaminoResultante> resultados = grafo.getRutasDestino(ubicacion);

                        // Devolver todos los caminos (posibles orígenes). Si no hay, devolver arreglo vacío.
                        if (resultados == null || resultados.isEmpty()) {
                            return ResponseEntity.ok(Collections.emptyList());
                        }

                        return ResponseEntity.ok(resultados);
                    })
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al calcular caminos: " + e.getMessage());
        }
    }

    // Nuevo endpoint: sugerir el mejor destino de bajo riesgo desde un origen
    @GetMapping("/sugerir-destino/{origenId}")
    public ResponseEntity<?> sugerirDestino(@PathVariable int origenId, @RequestParam(required = false) String nivel) {
        try {
            return ubicacionRepository.findById(origenId)
                    .map(origen -> {
                        String nivelBuscado = (nivel == null) ? "BAJO" : nivel;
                        co.proyecto.dto.CaminoResultante mejor = grafo.getMejorDestinoDesde(origen, nivelBuscado);
                        if (mejor == null) return ResponseEntity.ok().body(Collections.emptyMap());
                        return ResponseEntity.ok(mejor);
                    })
                    .orElseGet(() -> ResponseEntity.status(404).body("Origen no encontrado"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al sugerir destino: " + e.getMessage());
        }
    }
}
