package co.proyecto.model;

//import co.proyecto.logic.estructuraGrafo.GrafoRutas;
import jakarta.persistence.*;

@Entity
public class SimuladorTransporte {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSimulador;
    //@Transient
    //private GrafoRutas grafo;

    public int getIdSimulador() {
        return idSimulador;
    }

    public void setIdSimulador(int idSimulador) {
        this.idSimulador = idSimulador;
    }

    //public GrafoRutas getGrafo() {
        //return grafo;
    //}

    //public void setGrafo(GrafoRutas grafo) {
        //this.grafo = grafo;
    //}

    
}