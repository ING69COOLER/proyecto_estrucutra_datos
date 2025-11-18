package co.proyecto.logic.Clientes;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import co.proyecto.dto.CaminoResultante;
import co.proyecto.model.Recurso;

@Service
public class PlanificacionService {

    /**
     * Esta es la función auxiliar que pediste.
     * Recibe una lista completa de caminos y devuelve solo los 10 mejores,
     * priorizados por distancia (más corta) y luego por recursos (más recursos).
     */
    public List<CaminoResultante> priorizarYLimitarCaminos(List<CaminoResultante> caminos) {
    
        
        // 1. Definir el comparador (Como una cola de prioridad)
        //    - Criterio 1: Ordenar por MÁS recursos (descendente)
        //    - Criterio 2: Si los recursos son iguales, ordenar por MENOR distancia (ascendente)
        Comparator<CaminoResultante> comparadorPrioridad = Comparator
            .comparingInt(
                // Función para obtener la SUMA TOTAL de las cantidades de recursos
                (CaminoResultante c) -> {
                    if (c.getOrigen() == null || c.getOrigen().getRecursosDisponibles() == null) {
                        return 0;
                    }
                    // Suma la 'cantidad' de cada 'Recurso' en la lista de disponibles
                    return c.getOrigen().getRecursosDisponibles().stream()
                            .mapToInt(Recurso::getCantidad) // Obtiene la cantidad de cada recurso
                            .sum(); // Suma todas las cantidades
                }
            )
            .reversed() // .reversed() hace que sea orden descendente (más recursos primero)
            .thenComparingDouble(CaminoResultante::getDistanciaMinima); // Menor distancia primero (ascendente)


        // 2. Ordenar la lista completa, tomar los 10 primeros y devolverla
        return caminos.stream()
            .sorted(comparadorPrioridad)
            .limit(10) // Limita la lista a los 10 mejores resultados
            .collect(Collectors.toList());
    }
}