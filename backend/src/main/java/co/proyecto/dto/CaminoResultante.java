package co.proyecto.dto;

import java.util.List;

import co.proyecto.model.Ubicacion;

public class CaminoResultante {
    // La Ubicación de Origen completa
    private Ubicacion origen; 
    
    // La Ubicación de Destino completa (será el mismo objeto que el parámetro 'u')
    private Ubicacion destino;
    
    // La distancia mínima calculada por Warshall
    private double distanciaMinima;
    
    // La lista completa de Ubicaciones que componen el camino más corto
    private List<Ubicacion> caminoUbicaciones;

    public CaminoResultante(){}

    public CaminoResultante(Ubicacion origen, Ubicacion destino, double distanciaMinima,
            List<Ubicacion> caminoUbicaciones) {
        this.origen = origen;
        this.destino = destino;
        this.distanciaMinima = distanciaMinima;
        this.caminoUbicaciones = caminoUbicaciones;
    }

    public Ubicacion getOrigen() {
        return origen;
    }

    public void setOrigen(Ubicacion origen) {
        this.origen = origen;
    }

    public Ubicacion getDestino() {
        return destino;
    }

    public void setDestino(Ubicacion destino) {
        this.destino = destino;
    }

    public double getDistanciaMinima() {
        return distanciaMinima;
    }

    public void setDistanciaMinima(double distanciaMinima) {
        this.distanciaMinima = distanciaMinima;
    }

    public List<Ubicacion> getCaminoUbicaciones() {
        return caminoUbicaciones;
    }

    public void setCaminoUbicaciones(List<Ubicacion> caminoUbicaciones) {
        this.caminoUbicaciones = caminoUbicaciones;
    } 

    
}