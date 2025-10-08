package co.proyecto.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class GrafoRutas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idGrafo;

    @OneToMany
    private List<Ubicacion> nodos;

    @OneToMany
    private List<Ruta> aristas;

    public int getIdGrafo() {
        return idGrafo;
    }

    public void setIdGrafo(int idGrafo) {
        this.idGrafo = idGrafo;
    }

    public List<Ubicacion> getNodos() {
        return nodos;
    }

    public void setNodos(List<Ubicacion> nodos) {
        this.nodos = nodos;
    }

    public List<Ruta> getAristas() {
        return aristas;
    }

    public void setAristas(List<Ruta> aristas) {
        this.aristas = aristas;
    }

    
}
