package co.proyecto.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.Date;

@Entity
public class Ubicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Cambiado de idUbicacion a id

    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoUbicacion tipo;

    private int personasAfectadas;
    private String nivelRiesgo; // Cambia a String para coincidir con el frontend

    // NUEVOS CAMPOS para el mockup
    private Double lat;
    private Double lng;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    // Cambia ListaDobleSimple por List<Recurso>
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Recurso> recursosNecesarios;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Recurso> recursosDisponibles;

    @OneToMany(mappedBy = "origen")
    private List<Ruta> rutasOrigen;

    @OneToMany(mappedBy = "destino")
    private List<Ruta> rutasDestino;

    public Ubicacion() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public TipoUbicacion getTipo() {
        return tipo;
    }

    public void setTipo(TipoUbicacion tipo) {
        this.tipo = tipo;
    }

    public int getPersonasAfectadas() {
        return personasAfectadas;
    }

    public void setPersonasAfectadas(int personasAfectadas) {
        this.personasAfectadas = personasAfectadas;
    }

    public String getNivelRiesgo() {
        return nivelRiesgo;
    }

    public void setNivelRiesgo(String nivelRiesgo) {
        this.nivelRiesgo = nivelRiesgo;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Recurso> getRecursosNecesarios() {
        return recursosNecesarios;
    }

    public void setRecursosNecesarios(List<Recurso> recursosNecesarios) {
        this.recursosNecesarios = recursosNecesarios;
    }

    public List<Recurso> getRecursosDisponibles() {
        return recursosDisponibles;
    }

    public void setRecursosDisponibles(List<Recurso> recursosDisponibles) {
        this.recursosDisponibles = recursosDisponibles;
    }

    public List<Ruta> getRutasOrigen() {
        return rutasOrigen;
    }

    public void setRutasOrigen(List<Ruta> rutasOrigen) {
        this.rutasOrigen = rutasOrigen;
    }

    public List<Ruta> getRutasDestino() {
        return rutasDestino;
    }

    public void setRutasDestino(List<Ruta> rutasDestino) {
        this.rutasDestino = rutasDestino;
    }

    
}