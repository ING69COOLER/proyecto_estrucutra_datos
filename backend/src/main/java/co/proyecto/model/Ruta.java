package co.proyecto.model;

import co.proyecto.logic.estructuraGrafo.Arista;
import co.proyecto.logic.estructuraGrafo.Nodo;
import jakarta.persistence.*;

@Entity
public class Ruta <T extends Comparable> implements Arista<T> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "origen_id", referencedColumnName = "id", nullable = false)
    private Ubicacion origen;   // referencia a entidad Ubicacion

    @ManyToOne
    @JoinColumn(name = "destino_id", referencedColumnName = "id", nullable = false)
    private Ubicacion destino;  // referencia a entidad Ubicacion

    private double distancia; // en km

    public Ruta() {} // Constructor vacío requerido por JPA

    public Ruta(Nodo<T> I, Nodo<T> F, double distancia){
        origen = (Ubicacion) I.getValor();
        destino = (Ubicacion) F.getValor();
        this.distancia = distancia;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    @Override
    public void setCola(Nodo<T> cola) {
        this.destino = (Ubicacion)cola.getValor();
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setCola'");
    }

    @Override
    public void setCabeza(Nodo<T> cabeza) {
        this.origen = (Ubicacion)cabeza.getValor();
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setCabeza'");
    }

    @Override
    public Nodo<T> getCola() {
        return new Nodo<T>((T)destino);
    }

    @Override
    public Nodo<T> getCabeza() {
        // TODO Auto-generated method stub
        return new Nodo<T>((T)origen);
    }

}