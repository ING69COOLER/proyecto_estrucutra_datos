
package co.proyecto.logic;

import co.proyecto.dto.ReporteRecursosDTO;
import co.proyecto.estructuras.ColaPrioridad;
import co.proyecto.model.Distribucion;
import co.proyecto.model.Recurso;
import co.proyecto.model.Ubicacion;
import co.proyecto.repository.DistribucionRepository;
import co.proyecto.repository.UbicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportesService {

    private final UbicacionRepository ubicacionRepository;
    private final DistribucionRepository distribucionRepository;
    private final ColaPrioridad colaPrioridad;

    @Autowired
    public ReportesService(UbicacionRepository ubicacionRepository,
                          DistribucionRepository distribucionRepository,
                          ColaPrioridad colaPrioridad) {
        this.ubicacionRepository = ubicacionRepository;
        this.distribucionRepository = distribucionRepository;
        this.colaPrioridad = colaPrioridad;
    }

    /**
     * Genera reporte detallado de recursos por zona
     */
    public List<ReporteRecursosDTO> generarReporteRecursosPorZona() {
        List<Ubicacion> ubicaciones = ubicacionRepository.findAllWithRecursos();
        List<ReporteRecursosDTO> reporte = new ArrayList<>();
        
        for (Ubicacion ubicacion : ubicaciones) {
            // Agrupar recursos disponibles por tipo
            Map<String, Integer> disponiblesPorTipo = ubicacion.getRecursosDisponibles().stream()
                .collect(Collectors.groupingBy(
                    Recurso::getTipo,
                    Collectors.summingInt(Recurso::getCantidad)
                ));
            
            // Agrupar recursos necesarios por tipo
            Map<String, Integer> necesariosPorTipo = ubicacion.getRecursosNecesarios().stream()
                .collect(Collectors.groupingBy(
                    Recurso::getTipo,
                    Collectors.summingInt(Recurso::getCantidad)
                ));
            
            // Obtener todos los tipos de recursos
            Set<String> tiposRecursos = new HashSet<>();
            tiposRecursos.addAll(disponiblesPorTipo.keySet());
            tiposRecursos.addAll(necesariosPorTipo.keySet());
            
            // Crear un registro por cada tipo de recurso
            for (String tipo : tiposRecursos) {
                ReporteRecursosDTO dto = new ReporteRecursosDTO(
                    ubicacion.getNombre(),
                    tipo,
                    disponiblesPorTipo.getOrDefault(tipo, 0),
                    necesariosPorTipo.getOrDefault(tipo, 0),
                    ubicacion.getNivelRiesgo()
                );
                reporte.add(dto);
            }
        }
        
        return reporte;
    }

    /**
     * Genera estadísticas de zonas críticas
     */
    public Map<String, Object> generarEstadisticasZonasCriticas() {
        List<Ubicacion> ubicaciones = ubicacionRepository.findAllWithRecursos();
        
        // Agrupar por nivel de riesgo
        Map<String, Long> distribucionRiesgo = ubicaciones.stream()
            .collect(Collectors.groupingBy(
                Ubicacion::getNivelRiesgo,
                Collectors.counting()
            ));
        
        // Calcular total de personas afectadas
        int totalPersonas = ubicaciones.stream()
            .mapToInt(Ubicacion::getPersonasAfectadas)
            .sum();
        
        // Identificar zona más crítica
        Ubicacion zonaMasCritica = ubicaciones.stream()
            .max(Comparator.comparing(Ubicacion::getNivelRiesgo)
                .thenComparingInt(Ubicacion::getPersonasAfectadas))
            .orElse(null);
        
        // Top 5 zonas prioritarias (usando cola de prioridad)
        List<Ubicacion> topPrioritarias = new ArrayList<>();
        ColaPrioridad colaTemp = new ColaPrioridad(ubicacionRepository);
        colaTemp.cargarTodo();
        
        for (int i = 0; i < 5 && colaTemp.peek() != null; i++) {
            topPrioritarias.add(colaTemp.pool());
        }
        
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("distribucionRiesgo", distribucionRiesgo);
        estadisticas.put("totalPersonasAfectadas", totalPersonas);
        estadisticas.put("totalZonas", ubicaciones.size());
        estadisticas.put("zonaMasCritica", zonaMasCritica);
        estadisticas.put("top5Prioritarias", topPrioritarias);
        
        return estadisticas;
    }

    /**
     * Genera reporte de distribuciones realizadas
     */
    public Map<String, Object> generarReporteDistribuciones() {
        List<Distribucion> distribuciones = distribucionRepository.findAllOrderByFechaDesc();
        
        // Total de distribuciones
        int totalDistribuciones = distribuciones.size();
        
        // Distribuciones por estado
        Map<String, Long> distribucionesPorEstado = distribuciones.stream()
            .collect(Collectors.groupingBy(
                Distribucion::getEstado,
                Collectors.counting()
            ));
        
        // Total de recursos distribuidos
        int totalRecursosDistribuidos = distribuciones.stream()
            .mapToInt(Distribucion::getCantidad)
            .sum();
        
        // Distancia total recorrida
        double distanciaTotal = distribuciones.stream()
            .mapToDouble(Distribucion::getDistanciaRecorrida)
            .sum();
        
        // Distribuciones por tipo de recurso
        Map<String, Long> distribucionesPorTipo = distribuciones.stream()
            .collect(Collectors.groupingBy(
                d -> d.getRecurso().getTipo(),
                Collectors.counting()
            ));
        
        // Top 5 destinos más abastecidos
        Map<String, Long> destinosAbastecidos = distribuciones.stream()
            .collect(Collectors.groupingBy(
                d -> d.getDestino().getNombre(),
                Collectors.counting()
            ));
        
        List<Map.Entry<String, Long>> top5Destinos = destinosAbastecidos.entrySet().stream()
            .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
            .limit(5)
            .collect(Collectors.toList());
        
        Map<String, Object> reporte = new HashMap<>();
        reporte.put("totalDistribuciones", totalDistribuciones);
        reporte.put("distribucionesPorEstado", distribucionesPorEstado);
        reporte.put("totalRecursosDistribuidos", totalRecursosDistribuidos);
        reporte.put("distanciaTotal", distanciaTotal);
        reporte.put("distribucionesPorTipo", distribucionesPorTipo);
        reporte.put("top5Destinos", top5Destinos);
        reporte.put("historialReciente", distribuciones.stream().limit(20).collect(Collectors.toList()));
        
        return reporte;
    }

    /**
     * Genera archivo CSV con reporte de recursos
     */
    public String generarCSV() {
        List<ReporteRecursosDTO> reporte = generarReporteRecursosPorZona();
        
        StringBuilder csv = new StringBuilder();
        csv.append("Ubicacion,Tipo Recurso,Disponibles,Necesarios,Nivel Riesgo\n");
        
        for (ReporteRecursosDTO dto : reporte) {
            csv.append(String.format("%s,%s,%d,%d,%s\n",
                dto.getUbicacionNombre(),
                dto.getTipoRecurso(),
                dto.getDisponibles(),
                dto.getNecesarios(),
                dto.getNivelRiesgo()
            ));
        }
        
        return csv.toString();
    }
}
