package co.proyecto.controller;

import co.proyecto.dto.DistribucionRequest;
import co.proyecto.dto.SugerenciaDistribucion;
import co.proyecto.logic.DistribucionService;
import co.proyecto.model.Distribucion;
import co.proyecto.model.Usuario;
import co.proyecto.model.enums.Rol;
import co.proyecto.repository.DistribucionRepository;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/distribucion")
public class DistribucionController {

    private static final Logger logger = LoggerFactory.getLogger(DistribucionController.class);
    
    private final DistribucionService distribucionService;
    private final DistribucionRepository distribucionRepository;

    @Autowired
    public DistribucionController(DistribucionService distribucionService,
                                 DistribucionRepository distribucionRepository) {
        this.distribucionService = distribucionService;
        this.distribucionRepository = distribucionRepository;
    }

    /**
     * Ejecuta una distribución manual de recursos
     */
    @PostMapping("/ejecutar")
    public ResponseEntity<?> ejecutarDistribucion(@RequestBody DistribucionRequest request,
                                                   HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuario == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        if (usuario.getRol() != Rol.ADMINISTRADOR) {
            logger.warn("Usuario {} intentó ejecutar distribución sin permisos", usuario.getNombre());
            return ResponseEntity.status(403).body("Acceso denegado: Solo administradores");
        }

        try {
            logger.info("Usuario {} ejecutando distribución: Origen={}, Destino={}, Recurso={}, Cantidad={}",
                       usuario.getNombre(), request.getOrigenId(), request.getDestinoId(), 
                       request.getRecursoId(), request.getCantidad());
            
            Distribucion distribucion = distribucionService.ejecutarDistribucion(request, usuario);
            
            logger.info("Distribución ejecutada exitosamente: ID={}", distribucion.getId());
            
            return ResponseEntity.ok(distribucion);
            
        } catch (IllegalArgumentException e) {
            logger.error("Error de validación en distribución: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error ejecutando distribución", e);
            return ResponseEntity.status(500).body("Error al ejecutar distribución: " + e.getMessage());
        }
    }

    /**
     * Obtiene sugerencias automáticas de distribución para un destino
     */
    @GetMapping("/sugerencias/{destinoId}")
    public ResponseEntity<?> obtenerSugerencias(@PathVariable int destinoId, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuario == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        try {
            logger.info("Usuario {} solicitó sugerencias para destino ID={}", usuario.getNombre(), destinoId);
            
            List<SugerenciaDistribucion> sugerencias = distribucionService.calcularSugerencias(destinoId);
            
            logger.info("Se generaron {} sugerencias para destino ID={}", sugerencias.size(), destinoId);
            
            return ResponseEntity.ok(sugerencias);
            
        } catch (Exception e) {
            logger.error("Error generando sugerencias", e);
            return ResponseEntity.status(500).body("Error al generar sugerencias: " + e.getMessage());
        }
    }

    /**
     * Obtiene el historial completo de distribuciones
     */
    @GetMapping("/historial")
    public ResponseEntity<?> obtenerHistorial(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuario == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        try {
            List<Distribucion> historial = distribucionRepository.findAllOrderByFechaDesc();
            logger.info("Historial de distribuciones consultado por {}: {} registros", 
                       usuario.getNombre(), historial.size());
            return ResponseEntity.ok(historial);
        } catch (Exception e) {
            logger.error("Error obteniendo historial", e);
            return ResponseEntity.status(500).body("Error al obtener historial: " + e.getMessage());
        }
    }

    /**
     * Actualiza el estado de una distribución
     */
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, 
                                              @RequestParam String nuevoEstado,
                                              HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuario == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        try {
            Distribucion distribucion = distribucionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Distribución no encontrada"));
            
            distribucion.setEstado(nuevoEstado);
            distribucionRepository.save(distribucion);
            
            logger.info("Estado de distribución {} actualizado a {} por {}", 
                       id, nuevoEstado, usuario.getNombre());
            
            return ResponseEntity.ok(distribucion);
        } catch (Exception e) {
            logger.error("Error actualizando estado", e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}