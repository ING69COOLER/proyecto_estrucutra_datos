package co.proyecto.logic.estructuraGrafo;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import co.proyecto.model.Ruta;

public class Grafo <T extends Comparable>{
    //composicion
    private LinkedList<Nodo> nodos;
    private LinkedList<Arista> aristas;
    //matrizAdyacencia con infinitos
    double[][] matrizAdyacencia;
    //resultado warshall
    double[][] caminoCortoWarshall;
    int[][] recorridoCortoWarshall;


    public Grafo(){
        nodos = new LinkedList<>();
        aristas = new LinkedList<>();
    }

    public void agregarNodo(T valor){
        Nodo<T> nodo = new Nodo<>(valor);
	    nodos.add(nodo);
    }
    public void agregarRelacion(T valorI, T valorF, double distancia){
        try {
            Nodo<T> nodoI = encontrarNodo(valorI);
            Nodo<T> nodoF = encontrarNodo(valorF);
            if (nodoF != null && nodoI != null) {
            Arista arista = new Ruta<>(nodoI,nodoF, distancia);
            aristas.add(arista);
            actualizarMatrizAdyacencia();
        }
        } catch (Exception e) {
            System.err.print(e + "no hay relacion");
        }
    }
    private Nodo<T> encontrarNodo(T valor){
	    for (Nodo<T> nodo: nodos) {
	        if (nodo.getValor().equals(valor)) {
	    	    return nodo;
	        }
	    }
        return null;
    }

    public void imprimirAristasGrafo(){
        imprimirAristas(aristas);
    }

    public void imprimirAristas(List<Arista> aristasIn){
        for (Arista arista : aristasIn) {
            System.out.println("arista :  " + (Integer)arista.getCabeza().getValor() + "  --->  " + (Integer)arista.getCola().getValor()+ "  ; con valor de:  " +  arista.getDistancia());
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

        for (Arista arista : aristas) {
            var a = nodos.indexOf(arista.getCabeza());
            var b = nodos.indexOf(arista.getCola());
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

    public LinkedList<Nodo> getNodos() {
        return nodos;
    }

    public LinkedList<Arista> getAristas() {
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
