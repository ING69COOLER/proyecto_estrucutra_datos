package co.proyecto.logic.estructuraGrafo;
// clase previa, (es como una muestra)
public class Aristak <T extends Comparable> implements Arista<T>{
    private Nodo<T> cola;
    private Nodo<T> cabeza;
    private double distancia;

    public Aristak(Nodo<T> I, Nodo<T> F, double distancia){
        cabeza = I;
        cola = F;
        this.distancia = distancia;
    }

    @Override
    public void setCola(Nodo<T> cola) {
        this.cola = cola;
    }
    @Override
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
