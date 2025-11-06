package co.proyecto.logic;

public interface Arista <T extends Comparable> {
    // esta es la estructura que debe de cumplir la arista

    //private Nodo<T> cola;
    //private Nodo<T> cabeza;
    //private double distancia;

    //public Arista(Nodo<T> I, Nodo<T> F, double distancia);

    public abstract void setCola(Nodo<T> cola);
    public abstract void setCabeza(Nodo<T> cabeza); 
    public abstract Nodo<T> getCola();
    public abstract Nodo<T> getCabeza();
    public abstract double getDistancia();
    public abstract void setDistancia(double distancia);
    
}
