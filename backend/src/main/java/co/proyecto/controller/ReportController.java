package co.proyecto.controller;

import co.proyecto.dto.ZonaReporte;
import co.proyecto.model.Ubicacion;
import co.proyecto.model.Recurso;
import co.proyecto.model.EquipoRescate;
import co.proyecto.model.enums.EstadoEquipo;
import co.proyecto.repository.UbicacionRepository;
import co.proyecto.model.Usuario;
import co.proyecto.model.enums.Rol;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/admin/report")
public class ReportController {

    private final UbicacionRepository ubicacionRepository;

    public ReportController(UbicacionRepository ubicacionRepository) {
        this.ubicacionRepository = ubicacionRepository;
    }

    @GetMapping("/zonas")
    public ResponseEntity<?> reporteZonas(HttpSession session) {
        try {
            Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
            if (usuario == null) return ResponseEntity.status(401).body("No autenticado");
            if (usuario.getRol() != Rol.ADMINISTRADOR) return ResponseEntity.status(403).body("Acceso denegado");

            List<Ubicacion> zonas = ubicacionRepository.findAllWithRecursos();

            ZonaReporte report = new ZonaReporte();
            report.setTotalZonas(zonas.size());

            int totalRecursos = 0; int recursosAsignados = 0; int recursosDisponibles = 0;
            int equiposTotales = 0; int equiposEnMision = 0; int personasAfectadasTotal = 0;

            for (Ubicacion z : zonas) {
                // personas afectadas
                personasAfectadasTotal += z.getPersonasAfectadas();

                // tipo counts
                String tipo = (z.getTipo() != null) ? z.getTipo().name() : "UNKNOWN";
                Map<String,Integer> mapTipo = report.getZonasPorTipo();
                mapTipo.put(tipo, mapTipo.getOrDefault(tipo, 0) + 1);

                String nivel = (z.getNivelRiesgo() != null) ? z.getNivelRiesgo() : "SIN_DATO";
                Map<String,Integer> mapNivel = report.getZonasPorNivelRiesgo();
                mapNivel.put(nivel, mapNivel.getOrDefault(nivel, 0) + 1);

                List<Recurso> recursos = z.getRecursosNecesarios();
                if (recursos != null) {
                    totalRecursos += recursos.size();
                    for (Recurso r : recursos) {
                        if (r.isDisponible()) recursosDisponibles += r.getCantidad() > 0 ? r.getCantidad() : 1;
                        else recursosAsignados += r.getCantidad() > 0 ? r.getCantidad() : 1;
                    }
                }

                Set<EquipoRescate> equipos = z.getEquipos();
                if (equipos != null) {
                    equiposTotales += equipos.size();
                    for (EquipoRescate e : equipos) {
                        if (e.getEstado() == EstadoEquipo.EN_MISION) equiposEnMision++;
                    }
                }
            }

            report.setTotalRecursos(totalRecursos);
            report.setRecursosAsignados(recursosAsignados);
            report.setRecursosDisponibles(recursosDisponibles);
            report.setEquiposTotales(equiposTotales);
            report.setEquiposEnMision(equiposEnMision);
            report.setPersonasAfectadasTotal(personasAfectadasTotal);

            return ResponseEntity.ok(report);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error generando reporte: " + e.getMessage());
        }
    }
}
