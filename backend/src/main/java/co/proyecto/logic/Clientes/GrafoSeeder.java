package co.proyecto.logic.Clientes;

import co.proyecto.model.Recurso;
import co.proyecto.model.Ruta;
import co.proyecto.model.TipoUbicacion;
import co.proyecto.model.Ubicacion;
import co.proyecto.repository.RecursoRepository;
import co.proyecto.repository.RutaRepository;
import co.proyecto.repository.UbicacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class GrafoSeeder {

    private final UbicacionRepository ubicacionRepository;
    private final RecursoRepository recursoRepository;
    private final RutaRepository rutaRepository;
    private final Random random = new Random();

    // 30 Ubicaciones de ciudades y zonas de Colombia
    public static final String[][] CITIES = {
        {"Bogotá", "4.7110", "-74.0721", "URBANA", "CRITICO"},
        {"Medellín", "6.2442", "-75.5812", "URBANA", "ALTO"},
        {"Cali", "3.4516", "-76.5320", "URBANA", "ALTO"},
        {"Barranquilla", "10.9685", "-74.7813", "URBANA", "MEDIO"},
        {"Cartagena", "10.3910", "-75.4794", "URBANA", "MEDIO"},
        {"Bucaramanga", "7.1193", "-73.1227", "URBANA", "ALTO"},
        {"Pereira", "4.8133", "-75.6946", "URBANA", "BAJO"},
        {"Manizales", "5.0682", "-75.5173", "URBANA", "MEDIO"},
        {"Cúcuta", "7.8939", "-72.5078", "URBANA", "BAJO"},
        {"Ibagué", "4.4389", "-75.2323", "URBANA", "BAJO"},
        {"Pasto", "1.2136", "-77.2811", "URBANA", "MEDIO"},
        {"Neiva", "2.9272", "-75.2863", "URBANA", "BAJO"},
        {"Villavicencio", "4.1420", "-73.6264", "URBANA", "ALTO"},
        {"Santa Marta", "11.2393", "-74.2045", "URBANA", "MEDIO"},
        {"Armenia", "4.5332", "-75.6811", "URBANA", "BAJO"},
        {"Montería", "8.7600", "-75.8828", "URBANA", "MEDIO"},
        {"Tunja", "5.5359", "-73.3644", "URBANA", "BAJO"},
        {"Riohacha", "11.5367", "-72.9238", "URBANA", "MEDIO"},
        {"Popayán", "2.4419", "-76.6062", "URBANA", "ALTO"},
        {"Sincelejo", "9.3040", "-75.3957", "URBANA", "BAJO"},
        {"Valledupar", "10.4632", "-73.2532", "URBANA", "MEDIO"},
        {"Quibdó", "5.6961", "-76.6575", "URBANA", "CRITICO"},
        {"San Andrés", "12.5833", "-81.7000", "URBANA", "BAJO"},
        {"Leticia", "-4.2151", "-69.9404", "RURAL", "MEDIO"},
        {"Puerto Carreño", "6.1824", "-67.4851", "RURAL", "BAJO"},
        {"Mitú", "1.1989", "-70.2372", "RURAL", "CRITICO"},
        {"Arauca", "7.0863", "-70.7597", "URBANA", "MEDIO"},
        {"Yopal", "5.3402", "-73.3533", "URBANA", "BAJO"},
        {"Florencia", "1.6156", "-75.6069", "URBANA", "ALTO"},
        {"Mocoa", "1.0483", "-76.6433", "URBANA", "CRITICO"}
    };

    private static final String[] RECURSO_TYPES = {"ALIMENTO", "MEDICINA", "EQUIPO_RESCATE", "AGUA", "COMBUSTIBLE"};


    @Autowired
    public GrafoSeeder(UbicacionRepository ubicacionRepository, RecursoRepository recursoRepository, RutaRepository rutaRepository) {
        this.ubicacionRepository = ubicacionRepository;
        this.recursoRepository = recursoRepository;
        this.rutaRepository = rutaRepository;
    }

    /**
     * Carga un grafo completo de 30 ubicaciones, rutas interconectadas
     * y múltiples recursos por ubicación.
     * @return El número total de rutas creadas.
     */
    @Transactional
    public int seedHugeGraph() {
        // 1. Limpiar datos previos
        //rutaRepository.deleteAllInBatch();
        //recursoRepository.deleteAllInBatch();
        //ubicacionRepository.deleteAllInBatch(); 
        
        List<Ubicacion> savedUbicaciones = new ArrayList<>();

        // 2. Crear y guardar Ubicaciones con Recursos
        for (String[] city : CITIES) {
            Ubicacion u = createUbicacion(city);
            
            // Asignar 3 a 6 recursos (disponibles o necesarios)
            Set<Recurso> recursosDisponibles = new HashSet<>();
            List<Recurso> recursosNecesarios = new ArrayList<>();
            
            int numResources = 3 + random.nextInt(4);
            for (int i = 0; i < numResources; i++) {
                String tipo = RECURSO_TYPES[random.nextInt(RECURSO_TYPES.length)];
                int cantidad = 100 + random.nextInt(900);
                
                Recurso r = createRecurso(u, tipo, cantidad);
                
                // Si el nivel de riesgo es CRITICO/ALTO, la mayoría de los recursos serán NECESARIOS
                if (u.getNivelRiesgo().equals("CRITICO") || u.getNivelRiesgo().equals("ALTO")) {
                    if (random.nextDouble() < 0.8) { // 80% de probabilidad de ser necesario
                        recursosNecesarios.add(r);
                    } else {
                        recursosDisponibles.add(r);
                    }
                } else { // Zonas de menor riesgo tienen más disponibles
                    if (random.nextBoolean()) {
                        recursosDisponibles.add(r);
                    } else {
                        recursosNecesarios.add(r);
                    }
                }
            }
            
            u.setRecursosDisponibles(recursosDisponibles);
            u.setRecursosNecesarios(recursosNecesarios);

            Ubicacion savedU = ubicacionRepository.save(u);
            savedUbicaciones.add(savedU);
        }
        
        // 3. Crear y guardar Rutas (conexiones)
        int totalRutas = 0;
        int n = savedUbicaciones.size();
        
        // Conexión densa: cada ciudad se conecta con 3 a 5 otras ciudades
        for (int i = 0; i < n; i++) {
            Ubicacion origen = savedUbicaciones.get(i);
            int connections = 3 + random.nextInt(3); // 3 to 5 connections
            
            for (int j = 0; j < connections; j++) {
                // Seleccionar un destino diferente
                int destinoIndex = i;
                while (destinoIndex == i) {
                    destinoIndex = random.nextInt(n);
                }
                Ubicacion destino = savedUbicaciones.get(destinoIndex);
                
                // Se crea la ruta (no implementamos una verificación estricta de duplicados aquí)
                Ruta ruta = createRuta(origen, destino);
                rutaRepository.save(ruta);
                totalRutas++;
            }
        }
        
        return totalRutas;
    }

    private Ubicacion createUbicacion(String[] data) {
        Ubicacion u = new Ubicacion();
        u.setNombre(data[0]);
        u.setLat(Double.parseDouble(data[1]));
        u.setLng(Double.parseDouble(data[2]));
        u.setTipo(TipoUbicacion.valueOf(data[3]));
        u.setNivelRiesgo(data[4]);
        u.setPersonasAfectadas(100 + random.nextInt(4900));
        u.setUpdatedAt(new Date());
        return u;
    }

    private Recurso createRecurso(Ubicacion ubicacion, String tipo, int cantidad) {
        Recurso r = new Recurso();
        r.setNombre(tipo.toLowerCase().replace('_', ' '));
        r.setTipo(tipo);
        r.setCantidad(cantidad);
        r.setDisponible(true);
        r.setUbicacion(ubicacion);
        return r;
    }

    private Ruta createRuta(Ubicacion origen, Ubicacion destino) {
        Ruta r = new Ruta();
        r.setOrigen(origen);
        r.setDestino(destino);
        
        // Distancia simulada: 100 km a 1100 km (para simular diferentes distancias)
        double distanceKm = 100.0 + random.nextDouble() * 1000.0;
        
        r.setDistancia(distanceKm);
        return r;
    }
}