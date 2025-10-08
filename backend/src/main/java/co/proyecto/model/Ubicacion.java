package co.proyecto.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Ubicacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUbicacion;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private TipoUbicacion tipo;

    private int personasAfectadas;
    private int nivelRiesgo;

    @OneToMany
    private List<Recurso> recursosNecesarios;

    @OneToMany
    private List<Recurso> recursosDisponibles;

    public int getIdUbicacion() {
        return idUbicacion;
    }

    public void setIdUbicacion(int idUbicacion) {
        this.idUbicacion = idUbicacion;
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

    public int getNivelRiesgo() {
        return nivelRiesgo;
    }

    public void setNivelRiesgo(int nivelRiesgo) {
        this.nivelRiesgo = nivelRiesgo;
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

    
}