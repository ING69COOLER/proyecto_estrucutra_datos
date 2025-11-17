package co.proyecto.logic.Clientes;

import org.springframework.stereotype.Component;

import co.proyecto.logic.estructuraGrafo.Grafo;
import co.proyecto.model.Ruta;
import co.proyecto.model.Ubicacion;
import co.proyecto.repository.RutaRepository;
import co.proyecto.repository.UbicacionRepository;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class PruebaGrafo {

    private final Grafo grafo;
    private final RutaRepository rutaRepository; // Hecho final (buena práctica)
    private final UbicacionRepository ubicacionRepository;
    
    // Asumo que RutaClient debe ser inyectado, pero lo mantendremos local si no lo tienes como bean
    // private final RutaClient rutaClient; 

    public PruebaGrafo(RutaRepository rutaRepository, UbicacionRepository ubicacionRepository, Grafo grafo){
        this.rutaRepository = rutaRepository;
        this.ubicacionRepository = ubicacionRepository;
        this.grafo = grafo;
        //this.rutaClient = rutaClient; // Si RutaClient fuera inyectado
    }

    // Método auxiliar para la limpieza de la base de datos
    private void limpiarTodo() {
        rutaRepository.deleteAllInBatch(); 
        ubicacionRepository.deleteAllInBatch(); 
        System.out.println("\n--- DB LIMPIADA DE DATOS ANTIGUOS ---");
    }


    public void verificarGrafo() throws InterruptedException{
        /*System.out.println("\n--- INICIANDO FASE DE PRUEBA Y CARGA ---");
        
        // 1. LIMPIAR DATOS VIEJOS
        //limpiarTodo(); 
        
        RutaClient rutaClient = new RutaClient();

        // 2. CREAR UBICACIONES Y RUTAS (Lógica descomentada)
        Ubicacion bogota = crearUbicacion("Bogotá", 4.7110, -74.0721);
        Ubicacion medellin = crearUbicacion("Medellín", 6.2442, -75.5812);
        Ubicacion cali = crearUbicacion("Cali", 3.4516, -76.5320);
        Ubicacion barranquilla = crearUbicacion("Barranquilla", 10.9685, -74.7813);
        Ubicacion cartagena = crearUbicacion("Cartagena", 10.3910, -75.4794);
        Ubicacion bucaramanga = crearUbicacion("Bucaramanga", 7.1193, -73.1227);
        Ubicacion pereira = crearUbicacion("Pereira", 4.8133, -75.6946);

        Ubicacion[] ciudades = new Ubicacion[]{bogota, medellin, cali, barranquilla, cartagena, bucaramanga, pereira};

        // Guardar las ubicaciones primero (para obtener IDs)
        for(Ubicacion u : ciudades){
            ubicacionRepository.save(u);
        }

        // Crear Rutas (Simulando la carga de distancia)
        Ruta[] rutas = new Ruta[]{
            crearRuta(bogota, medellin, rutaClient), 
            crearRuta(bogota, cali, rutaClient),     
            crearRuta(medellin, barranquilla, rutaClient), 
            crearRuta(cali, barranquilla, rutaClient), 
            crearRuta(barranquilla, cartagena, rutaClient), 
            crearRuta(cartagena, bucaramanga, rutaClient), 
            crearRuta(bucaramanga, pereira, rutaClient), 
            crearRuta(medellin, pereira, rutaClient)  
        };
        
        for(Ruta ruta: rutas){
            rutaRepository.save(ruta);
        }
        System.out.println("--- Datos de prueba guardados exitosamente ---");
 */

        // 4. RECARGA DEL SINGLETON Y EJECUCIÓN DE ALGORITMOS
        this.grafo.actualizarGrafo(); // Forzar la recarga del Singleton con los datos nuevos
        
        // 5. VERIFICACIÓN (Ejecución de los métodos que confirman el funcionamiento)
        System.out.println("\n--- RESULTADOS FLOYD-WARSHALL ---");
        this.grafo.imprimirWarshall();
        System.out.println("\n--- ARISTAS CARGADAS ---");
        this.grafo.imprimirAristasGrafo();
        
        
        // Aquí podrías agregar tu test de getRutasDestino si quisieras.
    }


    // Método auxiliar para crear ubicación (ya existente)
    private Ubicacion crearUbicacion(String nombre, double lat, double lng){
        Ubicacion u = new Ubicacion();
        u.setNombre(nombre);
        u.setLat(lat);
        u.setLng(lng);
        return u;
    }

    // Método auxiliar para crear Ruta (con el Thread.sleep necesario)
    private Ruta crearRuta(Ubicacion origen, Ubicacion destino, RutaClient rutaClient) throws InterruptedException{
        Ruta r = new Ruta();
        r.setOrigen(origen);
        r.setDestino(destino);
        
        double distancia = rutaClient.calcularDistancia(origen.getLat(), origen.getLng(),
                                                        destino.getLat(), destino.getLng());
        r.setDistancia(distancia);

        // Pausa de 10 segundos para evitar rate-limiting de la API
        Thread.sleep(10000); 
        
        return r;
    }
}