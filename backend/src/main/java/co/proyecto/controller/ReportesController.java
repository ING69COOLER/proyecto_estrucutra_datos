package co.proyecto.controller;

import co.proyecto.dto.ReporteRecursosDTO;
import co.proyecto.logic.ReportesService;
import co.proyecto.model.Usuario;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
public class ReportesController {

    private static final Logger logger = LoggerFactory.getLogger(ReportesController.class);
    
    private final ReportesService reportesService;

    @Autowired
    public ReportesController(ReportesService reportesService) {
        this.reportesService = reportesService;
    }

    /**
     * Genera reporte de recursos por zona
     */
    @GetMapping("/recursos-por-zona")
    public ResponseEntity<?> reporteRecursosPorZona(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuario == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        try {
            logger.info("Usuario {} solicitó reporte de recursos por zona", usuario.getNombre());
            
            List<ReporteRecursosDTO> reporte = reportesService.generarReporteRecursosPorZona();
            
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            logger.error("Error generando reporte de recursos", e);
            return ResponseEntity.status(500).body("Error al generar reporte: " + e.getMessage());
        }
    }

    /**
     * Genera estadísticas de zonas críticas
     */
    @GetMapping("/zonas-criticas")
    public ResponseEntity<?> reporteZonasCriticas(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuario == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        try {
            logger.info("Usuario {} solicitó reporte de zonas críticas", usuario.getNombre());
            
            Map<String, Object> reporte = reportesService.generarEstadisticasZonasCriticas();
            
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            logger.error("Error generando estadísticas de zonas críticas", e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * Genera reporte de distribuciones realizadas
     */
    @GetMapping("/distribuciones")
    public ResponseEntity<?> reporteDistribuciones(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuario == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        try {
            logger.info("Usuario {} solicitó reporte de distribuciones", usuario.getNombre());
            
            Map<String, Object> reporte = reportesService.generarReporteDistribuciones();
            
            return ResponseEntity.ok(reporte);
        } catch (Exception e) {
            logger.error("Error generando reporte de distribuciones", e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * Exporta reporte en formato CSV
     */
    @GetMapping("/exportar-csv")
    public ResponseEntity<?> exportarCSV(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuario == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        try {
            logger.info("Usuario {} exportando reporte CSV", usuario.getNombre());
            
            String csv = reportesService.generarCSV();
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment", "reporte_recursos.csv");
            
            return ResponseEntity.ok()
                .headers(headers)
                .body(csv);
                
        } catch (Exception e) {
            logger.error("Error exportando CSV", e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
