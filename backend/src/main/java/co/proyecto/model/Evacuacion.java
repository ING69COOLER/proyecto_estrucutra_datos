package co.proyecto.model;

import co.proyecto.model.enums.EstadoEvacuacion;
import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Evacuacion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idEvacuacion;

    private int personasEvacuadas; // La cantidad a mover
    
    private Double distancia;      // Distancia del recorrido (dato informativo)
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;            // Cuándo ocurrió el movimiento

    @Enumerated(EnumType.STRING)
    private EstadoEvacuacion estado; // Ejem: COMPLETADA, EN_CAMINO

    // RELACIONES
    @ManyToOne
    @JoinColumn(name = "origen_id", nullable = false)
    private Ubicacion origen;  // De aquí se RESTAN las personas

    @ManyToOne
    @JoinColumn(name = "destino_id", nullable = false)
    private Ubicacion destino; // Aquí se SUMAN las personas

    private int prioridad;

    public Evacuacion() {
        this.fecha = new Date(); // Se asigna la fecha actual automáticamente al crear
        this.estado = EstadoEvacuacion.EN_PROCESO;
    }

    // --- Getters y Setters ---

    public int getIdEvacuacion() { return idEvacuacion; }
    public void setIdEvacuacion(int idEvacuacion) { this.idEvacuacion = idEvacuacion; }

    public int getPersonasEvacuadas() { return personasEvacuadas; }
    public void setPersonasEvacuadas(int personasEvacuadas) { this.personasEvacuadas = personasEvacuadas; }

    public Double getDistancia() { return distancia; }
    public void setDistancia(Double distancia) { this.distancia = distancia; }

    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; }

    public EstadoEvacuacion getEstado() { return estado; }
    public void setEstado(EstadoEvacuacion estado) { this.estado = estado; }

    public Ubicacion getOrigen() { return origen; }
    public void setOrigen(Ubicacion origen) { this.origen = origen; }

    public Ubicacion getDestino() { return destino; }
    public void setDestino(Ubicacion destino) { this.destino = destino; }

    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = prioridad; }
}