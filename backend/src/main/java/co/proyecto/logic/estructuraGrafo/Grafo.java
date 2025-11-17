package co.proyecto.logic.estructuraGrafo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import co.proyecto.dto.CaminoResultante;
import co.proyecto.model.Ruta;
import co.proyecto.model.Ubicacion;
import co.proyecto.repository.RutaRepository;
import co.proyecto.repository.UbicacionRepository;
import jakarta.annotation.PostConstruct;

@Component
public class Grafo {
    //composicion
    private LinkedList<Ubicacion> nodos;
    private LinkedList<Ruta> aristas;
    //matrizAdyacencia con infinitos
    double[][] matrizAdyacencia;
    //resultado warshall
    double[][] caminoCortoWarshall;
    int[][] recorridoCortoWarshall;
    RutaRepository rutaRepository;
    UbicacionRepository ubicacionRepository;

    
    public Grafo(RutaRepository rutaRepository, UbicacionRepository ubicacionRepository){
        this.rutaRepository = rutaRepository;
        this.ubicacionRepository = ubicacionRepository;
        nodos = new LinkedList<>();
        aristas = new LinkedList<>();
    }

    @PostConstruct
    public void init(){
        actualizarGrafo();
        System.out.println("se inicializo el grafo con cosas ya creadas");
    }


    public void actualizarGrafo() {
        System.out.println("--- @PostConstruct: Cargando datos en el Grafo Singleton... ---");
        try {
            cargarTodo();
            // Opcional: Calcular Warshall de una vez
            encontrarCaminoCortoWarshall(); 
            System.out.println("--- GRAFO CARGADO. Nodos: " + nodos.size() + " | Aristas: " + aristas.size() + " ---");
            imprimirWarshall(); // Imprime la matriz al arrancar
        } catch (Exception e) {
            System.err.println(" error al inicializar grafo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    
    public void cargarTodo(){

        List<Ruta> rutas = rutaRepository.findAllWithUbicaciones();
        aristas = new LinkedList<>(rutas);
        
        List<Ubicacion> ubicaciones = ubicacionRepository.findAllWithRecursos();
        nodos = new LinkedList<>(ubicaciones); 

        actualizarMatrizAdyacencia();
       
    }

    public List<CaminoResultante> getRutasDestino(Ubicacion destinoUbicacion) {
    
    // 1. Encontrar el índice de la matriz del destino
    //    Esto funciona gracias a que implementaste correctamente .equals() y .hashCode() por ID.
        int destinoIndex = nodos.indexOf(destinoUbicacion);

        if (destinoIndex == -1) {
            throw new IllegalArgumentException("La ubicación de destino " + destinoUbicacion.getNombre() + " no está en el grafo.");
        }

        List<CaminoResultante> resultados = new ArrayList<>();
        int n = nodos.size();

        // 2. Iterar por todos los posibles orígenes (filas de la matriz)
        for (int origenIndex = 0; origenIndex < n; origenIndex++) {
            // Saltar el camino a sí mismo
            if (origenIndex == destinoIndex) continue; 

            // Obtener la distancia (el valor de la matriz)
            double distancia = caminoCortoWarshall[origenIndex][destinoIndex];

            // 3. Si existe un camino (no es infinito)
            if (distancia != Double.POSITIVE_INFINITY) {
            
                CaminoResultante resultado = new CaminoResultante();
            
            // Llenar el DTO
                resultado.setOrigen(nodos.get(origenIndex));  // El objeto Ubicacion completo
                resultado.setDestino(destinoUbicacion);      // El objeto Ubicacion destino
                resultado.setDistanciaMinima(distancia); 
            
            // 4. Reconstruir el path completo (lista de objetos Ubicacion)
                resultado.setCaminoUbicaciones(reconstruirCaminoUbicaciones(origenIndex, destinoIndex));
            
                resultados.add(resultado);
            }
        }

        return resultados;
    }

    private List<Ubicacion> reconstruirCaminoUbicaciones(int i, int j) {
        
        List<Ubicacion> path = new LinkedList<>();
        int intermedio = recorridoCortoWarshall[i][j];

        if (intermedio != 0) { // Si hay un nodo intermedio
        
        // 1. Path de I al Intermedio (k)
            path.addAll(reconstruirCaminoUbicaciones(i, intermedio));
        
        // 2. Eliminar el nodo duplicado que se agregó en la recursión anterior
        //    (Es la Ubicacion 'intermedio' que ya está al final del primer path)
            path.remove(path.size() - 1); 
        
        // 3. Path del Intermedio (k) a J
            path.addAll(reconstruirCaminoUbicaciones(intermedio, j));
    
        } else {
        // Base case: Si no hay intermedio (camino directo)
            path.add(nodos.get(i)); // Origen
            path.add(nodos.get(j)); // Destino
        }
    
        return path;
    }
    

    public void imprimirAristasGrafo(){
        imprimirAristas(aristas);
    }

    public void imprimirAristas(List<Ruta> aristasIn){
        for (Ruta arista : aristasIn) {
            System.out.println("arista :  " + arista.getOrigen().getNombre() + "  --->  " + arista.getDestino().getNombre()+ "  ; con valor de:  " +  arista.getDistancia());
        }
    }

    public void actualizarMatrizAdyacencia(){
        int n = nodos.size();  // número de nodos
        matrizAdyacencia = new double[n][n]; 
        
        // Inicializar todas las celdas como infinito, excepto la diagonal
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    matrizAdyacencia[i][j] = 0; // distancia a sí mismo
                    } else {
                    matrizAdyacencia[i][j] = Double.POSITIVE_INFINITY; // sin conexión
                }
            }
        }

        for (Ruta arista : aristas) {
            var a = nodos.indexOf(arista.getOrigen());
            var b = nodos.indexOf(arista.getDestino());
            matrizAdyacencia[a][b] = arista.getDistancia();
        }
        
    }

    public void encontrarCaminoCortoWarshall(){
        var tamaño = nodos.size();
        recorridoCortoWarshall = new int[tamaño][tamaño];
        caminoCortoWarshall = new double[tamaño][tamaño];
        // clonar la matriz original
        for (int i = 0; i < tamaño; i++) {
            caminoCortoWarshall[i] = matrizAdyacencia[i].clone();
        }
        //floyd warshall
        //fila- columna
        for(int i = 0; i<nodos.size(); i++){
            // iteracion de la matriz
            for(int j = 0; j<nodos.size(); j++){
                for(int k = 0; k<nodos.size();k++){
                    if (caminoCortoWarshall[j][k] != caminoCortoWarshall[i][k] && caminoCortoWarshall[j][k] != caminoCortoWarshall[j][i]) {
                        if (caminoCortoWarshall[j][k] > caminoCortoWarshall[i][k] + caminoCortoWarshall[j][i]) {
                            caminoCortoWarshall[j][k] = caminoCortoWarshall[i][k] + caminoCortoWarshall[j][i];
                            recorridoCortoWarshall[j][k] = i;
                        }
                    }
                }
            }
        }
    }

    public void imprimirWarshall(){
        int n = caminoCortoWarshall.length;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < caminoCortoWarshall[i].length; j++) {
                if (caminoCortoWarshall[i][j] == Double.POSITIVE_INFINITY) {
                    System.out.print("INF\t");
                } else {
                    System.out.print(caminoCortoWarshall[i][j] + "\t");
                }
            }
            System.out.println();
        }

        System.out.println("");

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < recorridoCortoWarshall[i].length; j++) {
                if (recorridoCortoWarshall[i][j] == Double.POSITIVE_INFINITY) {
                    System.out.print("INF\t");
                } else {
                    System.out.print(recorridoCortoWarshall[i][j] + "\t");
                }
            }
            System.out.println();
        }
    }

    public LinkedList<Ubicacion> getNodos() {
        return nodos;
    }

    public LinkedList<Ruta> getAristas() {
        return aristas;
    }

    public double[][] getMatrizAdyacencia() {
        return matrizAdyacencia;
    }

    public double[][] getCaminoCortoWarshall() {
        return caminoCortoWarshall;
    }

    public int[][] getRecorridoCortoWarshall() {
        return recorridoCortoWarshall;
    }
}
