package co.proyecto.logic;

import co.proyecto.dto.CaminoResultante;
import co.proyecto.dto.DistribucionRequest;
import co.proyecto.dto.SugerenciaDistribucion;
import co.proyecto.logic.estructuraGrafo.Grafo;
import co.proyecto.model.Distribucion;
import co.proyecto.model.Recurso;
import co.proyecto.model.Ubicacion;
import co.proyecto.model.Usuario;
import co.proyecto.repository.DistribucionRepository;
import co.proyecto.repository.RecursoRepository;
import co.proyecto.repository.UbicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DistribucionService {

    private final UbicacionRepository ubicacionRepository;
    private final RecursoRepository recursoRepository;
    private final DistribucionRepository distribucionRepository;
    private final Grafo grafo;

    @Autowired
    public DistribucionService(UbicacionRepository ubicacionRepository,
                              RecursoRepository recursoRepository,
                              DistribucionRepository distribucionRepository,
                              Grafo grafo) {
        this.ubicacionRepository = ubicacionRepository;
        this.recursoRepository = recursoRepository;
        this.distribucionRepository = distribucionRepository;
        this.grafo = grafo;
    }

    /**
     * Ejecuta una distribución de recursos desde origen a destino
     */
    @Transactional
    public Distribucion ejecutarDistribucion(DistribucionRequest request, Usuario responsable) {
        // 1. Validar que existan origen y destino
        Ubicacion origen = ubicacionRepository.findById(request.getOrigenId())
            .orElseThrow(() -> new IllegalArgumentException("Ubicación origen no encontrada"));
        
        Ubicacion destino = ubicacionRepository.findById(request.getDestinoId())
            .orElseThrow(() -> new IllegalArgumentException("Ubicación destino no encontrada"));
        
        // 2. Validar que exista el recurso
        Recurso recurso = recursoRepository.findById(request.getRecursoId())
            .orElseThrow(() -> new IllegalArgumentException("Recurso no encontrado"));
        
        // 3. Validar que el recurso pertenezca al origen
        if (recurso.getUbicacion() == null || recurso.getUbicacion().getId() != origen.getId()) {
            throw new IllegalArgumentException("El recurso no pertenece a la ubicación origen");
        }
        
        // 4. Validar que haya cantidad suficiente
        if (recurso.getCantidad() < request.getCantidad()) {
            throw new IllegalArgumentException(
                String.format("Cantidad insuficiente. Disponible: %d, Solicitado: %d", 
                             recurso.getCantidad(), request.getCantidad())
            );
        }
        
        // 5. Calcular distancia usando el grafo
        double distancia = calcularDistanciaEntreUbicaciones(origen, destino);
        
        // 6. Actualizar inventarios
        recurso.setCantidad(recurso.getCantidad() - request.getCantidad());
        recursoRepository.save(recurso);
        
        // 7. Crear nuevo recurso en destino (o actualizar si ya existe)
        Recurso recursoDestino = destino.getRecursosDisponibles().stream()
            .filter(r -> r.getTipo().equals(recurso.getTipo()))
            .findFirst()
            .orElseGet(() -> {
                Recurso nuevo = new Recurso();
                nuevo.setNombre(recurso.getNombre());
                nuevo.setTipo(recurso.getTipo());
                nuevo.setCantidad(0);
                nuevo.setDisponible(true);
                nuevo.setUbicacion(destino);
                return nuevo;
            });
        
        recursoDestino.setCantidad(recursoDestino.getCantidad() + request.getCantidad());
        recursoRepository.save(recursoDestino);
        
        // 8. Crear registro de distribución
        Distribucion distribucion = new Distribucion();
        distribucion.setOrigen(origen);
        distribucion.setDestino(destino);
        distribucion.setRecurso(recurso);
        distribucion.setCantidad(request.getCantidad());
        distribucion.setDistanciaRecorrida(distancia);
        distribucion.setResponsable(responsable);
        distribucion.setEstado("COMPLETADA");
        
        return distribucionRepository.save(distribucion);
    }

    /**
     * Calcula sugerencias automáticas de distribución para un destino
     */
    public List<SugerenciaDistribucion> calcularSugerencias(int destinoId) {
        // 1. Obtener ubicación destino
        Ubicacion destino = ubicacionRepository.findById(destinoId)
            .orElseThrow(() -> new IllegalArgumentException("Ubicación no encontrada"));
        
        // 2. Obtener recursos necesarios
        List<Recurso> necesarios = destino.getRecursosNecesarios();
        
        if (necesarios == null || necesarios.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 3. Obtener todos los caminos posibles hacia el destino
        List<CaminoResultante> caminos = grafo.getRutasDestino(destino);
        
        // 4. Generar sugerencias para cada tipo de recurso necesario
        List<SugerenciaDistribucion> sugerencias = new ArrayList<>();
        
        for (Recurso recursoNecesario : necesarios) {
            String tipoNecesario = recursoNecesario.getTipo();
            int cantidadNecesaria = recursoNecesario.getCantidad();
            
            // Buscar orígenes con este tipo de recurso disponible
            for (CaminoResultante camino : caminos) {
                Ubicacion origen = camino.getOrigen();
                
                // Buscar recurso del tipo correcto en el origen
                Recurso recursoDisponible = origen.getRecursosDisponibles().stream()
                    .filter(r -> r.getTipo().equals(tipoNecesario) && r.getCantidad() > 0)
                    .findFirst()
                    .orElse(null);
                
                if (recursoDisponible != null) {
                    // Calcular score de prioridad
                    double score = calcularScorePrioridad(
                        recursoDisponible.getCantidad(),
                        camino.getDistanciaMinima(),
                        destino.getNivelRiesgo()
                    );
                    
                    SugerenciaDistribucion sugerencia = new SugerenciaDistribucion(
                        origen,
                        destino,
                        recursoDisponible,
                        recursoDisponible.getCantidad(),
                        camino.getDistanciaMinima(),
                        score
                    );
                    
                    sugerencias.add(sugerencia);
                }
            }
        }
        
        // 5. Ordenar por score (mayor a menor) y retornar top 10
        return sugerencias.stream()
            .sorted(Comparator.comparingDouble(SugerenciaDistribucion::getScore).reversed())
            .limit(10)
            .collect(Collectors.toList());
    }

    /**
     * Calcula la distancia entre dos ubicaciones usando el grafo
     */
    private double calcularDistanciaEntreUbicaciones(Ubicacion origen, Ubicacion destino) {
        try {
            List<CaminoResultante> caminos = grafo.getRutasDestino(destino);
            
            return caminos.stream()
                .filter(c -> c.getOrigen().getId() == origen.getId())
                .findFirst()
                .map(CaminoResultante::getDistanciaMinima)
                .orElse(0.0);
        } catch (Exception e) {
            return 0.0;
        }
    }

    /**
     * Calcula un score de prioridad para sugerencias
     * Formula: (Cantidad / Distancia) * Peso(Riesgo)
     */
    private double calcularScorePrioridad(int cantidad, double distancia, String nivelRiesgo) {
        double pesoRiesgo = switch (nivelRiesgo) {
            case "CRITICO" -> 3.0;
            case "ALTO" -> 2.0;
            case "MEDIO" -> 1.5;
            default -> 1.0;
        };
        
        // Evitar división por cero
        if (distancia == 0) distancia = 1.0;
        
        return (cantidad / distancia) * pesoRiesgo;
    }
}