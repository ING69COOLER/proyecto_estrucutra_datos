package co.proyecto.model;
import co.proyecto.model.enums.EstadoEvacuacion;

import jakarta.persistence.*;

@Entity
public class Evacuacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idEvacuacion;

    private int personasEvacuadas;
    private int prioridad;

    @Enumerated(EnumType.STRING)
    private EstadoEvacuacion estado;


    @ManyToOne
    @JoinColumn(name = "ubicacion_id")
    private Ubicacion ubicacion;

    // Nueva: zona destino de reubicación
    @ManyToOne
    @JoinColumn(name = "destino_reubicacion_id")
    private Ubicacion destinoReubicacion;
    public Ubicacion getDestinoReubicacion() {
        return destinoReubicacion;
    }

    public void setDestinoReubicacion(Ubicacion destinoReubicacion) {
        this.destinoReubicacion = destinoReubicacion;
    }

    // Constructor vacío
    public Evacuacion() {}

    public int getIdEvacuacion() {
        return idEvacuacion;
    }

    public void setIdEvacuacion(int idEvacuacion) {
        this.idEvacuacion = idEvacuacion;
    }

    public int getPersonasEvacuadas() {
        return personasEvacuadas;
    }

    public void setPersonasEvacuadas(int personasEvacuadas) {
        this.personasEvacuadas = personasEvacuadas;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public EstadoEvacuacion getEstado() {
        return estado;
    }

    public void setEstado(EstadoEvacuacion estado) {
        this.estado = estado;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

    
}