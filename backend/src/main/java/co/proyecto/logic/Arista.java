package co.proyecto.logic;

public class Arista <T extends Comparable> {
    private Nodo<T> cola;
    private Nodo<T> cabeza;
    private double distancia;

    public Arista(Nodo<T> I, Nodo<T> F, double distancia){
        cabeza = I;
        cola = F;
        this.distancia = distancia;
    }

    public void setCola(Nodo<T> cola) {
        this.cola = cola;
    }
    public void setCabeza(Nodo<T> cabeza) {
        this.cabeza = cabeza;
    }
    public Nodo<T> getCola() {
        return cola;
    }
    public Nodo<T> getCabeza() {
        return cabeza;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }
    
    
}
