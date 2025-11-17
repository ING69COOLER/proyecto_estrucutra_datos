package co.proyecto.logic.Clientes;

import org.springframework.stereotype.Component;

import co.proyecto.logic.estructuraGrafo.Grafo;
import co.proyecto.model.Ruta;
import co.proyecto.model.Ubicacion;
import co.proyecto.repository.RutaRepository;
import co.proyecto.repository.UbicacionRepository;

@Component
public class PruebaGrafo {
    private RutaRepository rutaRepository;
    private UbicacionRepository ubicacionRepository;

    public PruebaGrafo(RutaRepository rutaRepository, UbicacionRepository ubicacionRepository){
        this.rutaRepository= rutaRepository;
        this.ubicacionRepository = ubicacionRepository;
    }



    public void verificarGrafo() throws InterruptedException{
    System.out.println("Inicio de carga de grafo -----------------------------------------------------------------");
    RutaClient rutaClient = new RutaClient();

    /*// 1. Crear 10 ubicaciones conocidas de Colombia
    Ubicacion[] ciudades = new Ubicacion[] {
        crearUbicacion("Bogotá", 4.7110, -74.0721),
        crearUbicacion("Medellín", 6.2442, -75.5812),
        crearUbicacion("Cali", 3.4516, -76.5320),
        crearUbicacion("Barranquilla", 10.9685, -74.7813),
        crearUbicacion("Cartagena", 10.3910, -75.4794),
        crearUbicacion("Bucaramanga", 7.1193, -73.1227),
        crearUbicacion("Pereira", 4.8133, -75.6946),
    };

    Ruta[] rutas = new Ruta[]{
        crearRuta(ciudades[0], ciudades[1], rutaClient), // Bogotá -> Medellín
        crearRuta(ciudades[0], ciudades[2], rutaClient), // Bogotá -> Cali
        crearRuta(ciudades[1], ciudades[3], rutaClient), // Medellín -> Barranquilla
        crearRuta(ciudades[2], ciudades[3], rutaClient), // Cali -> Barranquilla (otro camino a Barranquilla)
        crearRuta(ciudades[3], ciudades[4], rutaClient), // Barranquilla -> Cartagena
        crearRuta(ciudades[4], ciudades[5], rutaClient), // Cartagena -> Bucaramanga
        crearRuta(ciudades[5], ciudades[6], rutaClient), // Bucaramanga -> Pereira
        crearRuta(ciudades[1], ciudades[6], rutaClient)  // Medellín -> Pereira (otro camino a Pereira)
    };

    // Guardar todas las ubicaciones en la DB y en un array de referencia
    for(Ubicacion u : ciudades){
        ubicacionRepository.save(u);
    }

    for(Ruta ruta: rutas){
        rutaRepository.save(ruta);
    }
         */

    //System.out.println("Grafo cargado con " + ciudades.length + " ubicaciones y " + totalRutas + " rutas.");

    Grafo grafo = new Grafo(rutaRepository, ubicacionRepository);

    grafo.cargarTodo();

    grafo.encontrarCaminoCortoWarshall();

    grafo.imprimirWarshall();

    grafo.imprimirAristasGrafo();
}

// Método auxiliar para crear ubicación
private Ubicacion crearUbicacion(String nombre, double lat, double lng){
    Ubicacion u = new Ubicacion();
    u.setNombre(nombre);
    u.setLat(lat);
    u.setLng(lng);
    return u;
}

private Ruta crearRuta(Ubicacion origen, Ubicacion destino, RutaClient rutaClient) throws InterruptedException{
    Ruta r = new Ruta();
    r.setOrigen(origen);
    r.setDestino(destino);
    double distancia = rutaClient.calcularDistancia(origen.getLat(), origen.getLng(),
                                                    destino.getLat(), destino.getLng());
    r.setDistancia(distancia);

    Thread.sleep(10000);
    
    return r;
}

    
}
