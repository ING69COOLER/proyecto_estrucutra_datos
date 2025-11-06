package co.proyecto.logic;
import co.proyecto.model.Ruta;
import co.proyecto.model.Ubicacion;

import java.util.List;

public class GrafoRutas {
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
