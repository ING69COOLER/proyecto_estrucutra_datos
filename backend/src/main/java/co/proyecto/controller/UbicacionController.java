package co.proyecto.controller;

import co.proyecto.estructuras.ColaPrioridad;
import co.proyecto.logic.Clientes.PlanificacionService;
import co.proyecto.model.Ubicacion;
import co.proyecto.repository.RutaRepository;
import co.proyecto.repository.UbicacionRepository;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/zonas")
public class UbicacionController {

    private final UbicacionRepository ubicacionRepository;
    private final ColaPrioridad colaPrioridad;
    private final co.proyecto.logic.estructuraGrafo.Grafo grafo;
    private final PlanificacionService planificacionService;
    private final RutaRepository rutaRepository;

    public UbicacionController(UbicacionRepository ubicacionRepository, ColaPrioridad colaPrioridad,
                               co.proyecto.logic.estructuraGrafo.Grafo grafo, PlanificacionService planificacionService, RutaRepository rutaRepository) {
        this.ubicacionRepository = ubicacionRepository;
        this.colaPrioridad = colaPrioridad;
        this.grafo = grafo;
        this.planificacionService = planificacionService;
        this.rutaRepository = rutaRepository;
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
                        
                        // 1. El Grafo nos da TODOS los caminos
                        List<co.proyecto.dto.CaminoResultante> todosLosCaminos = grafo.getRutasDestino(ubicacion);

                        if (todosLosCaminos == null || todosLosCaminos.isEmpty()) {
                            return ResponseEntity.ok(Collections.emptyList());
                        }

                        // 2. Se llama al método de la INSTANCIA (del servicio inyectado)
                        // esta parte del codigo se encarga de priorizar la que tiene mas recursos
                        List<co.proyecto.dto.CaminoResultante> top10Caminos = 
                            planificacionService.priorizarYLimitarCaminos(todosLosCaminos);
            
                        // 3. El Controlador devuelve el resultado limpio
                        return ResponseEntity.ok(top10Caminos);
                    })
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error al calcular caminos: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @Transactional // Asegura que todo (borrar rutas Y ubicación) se ejecute como una sola transacción
    public ResponseEntity<?> deleteUbicacion(@PathVariable int id) {
        
        // 1. Buscar la ubicación
        Optional<Ubicacion> ubicacionOpt = ubicacionRepository.findById(id);
        if (ubicacionOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Ubicación no encontrada con id: " + id);
        }
        Ubicacion ubicacion = ubicacionOpt.get();
        String nombre = ubicacion.getNombre();

        try {
            // 2. Eliminar rutas donde esta ubicación es origen o destino
            //    (Los recursos se borran en cascada gracias a orphanRemoval=true en Ubicacion.java)
            rutaRepository.deleteByOrigen(ubicacion);
            rutaRepository.deleteByDestino(ubicacion);

            // 3. Eliminar la ubicación
            ubicacionRepository.delete(ubicacion);
            
            // 4. Actualizar el grafo singleton para que refleje el borrado
            grafo.actualizarGrafo();

            System.out.println("Ubicación eliminada: {} (ID: {})"+ nombre+ id);
            return ResponseEntity.ok().body("Ubicación '" + nombre + "' y sus rutas asociadas han sido eliminadas.");

        } catch (Exception e) {
            System.out.println("Error al eliminar ubicación con id: " + id+ e);
            // Devolver un error más detallado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Error al eliminar la ubicación: " + e.getMessage());
        }
    }
}
