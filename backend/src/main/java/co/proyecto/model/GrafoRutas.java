package co.proyecto.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.List;

@Entity
public class GrafoRutas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idGrafo;
    private List<Ruta> rutas;
    private List<Ubicacion> ubicaciones;

    public GrafoRutas(List<Ruta> rutas, List<Ubicacion> ubicaciones) {
        this.rutas = rutas;
        this.ubicaciones = ubicaciones;
    }

    public int getIdGrafo() {
        return idGrafo;
    }

    public void setIdGrafo(int idGrafo) {
        this.idGrafo = idGrafo;
    }

    public List<Ruta> getRutas() {
        return rutas;
    }

    public void setRutas(List<Ruta> rutas) {
        this.rutas = rutas;
    }

    public List<Ubicacion> getUbicaciones() {
        return ubicaciones;
    }

    public void setUbicaciones(List<Ubicacion> ubicaciones) {
        this.ubicaciones = ubicaciones;
    }
}
