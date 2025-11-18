package co.proyecto.dto;

import co.proyecto.model.Recurso;
import co.proyecto.model.Ubicacion;

public class SugerenciaDistribucion {
    private Ubicacion origen;
    private Ubicacion destino;
    private Recurso recurso;
    private int cantidadDisponible;
    private double distancia;
    private double score; // Puntaje de prioridad

    public SugerenciaDistribucion() {}

    public SugerenciaDistribucion(Ubicacion origen, Ubicacion destino, Recurso recurso, 
                                  int cantidadDisponible, double distancia, double score) {
        this.origen = origen;
        this.destino = destino;
        this.recurso = recurso;
        this.cantidadDisponible = cantidadDisponible;
        this.distancia = distancia;
        this.score = score;
    }

    // Getters y Setters
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

    public Recurso getRecurso() {
        return recurso;
    }

    public void setRecurso(Recurso recurso) {
        this.recurso = recurso;
    }

    public int getCantidadDisponible() {
        return cantidadDisponible;
    }

    public void setCantidadDisponible(int cantidadDisponible) {
        this.cantidadDisponible = cantidadDisponible;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
