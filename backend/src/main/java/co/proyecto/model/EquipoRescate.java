package co.proyecto.model;
import co.proyecto.model.enums.EstadoEquipo;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class EquipoRescate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idEquipo;

    private String tipo;
    private int miembros;

    @Enumerated(EnumType.STRING)
    private EstadoEquipo estado;

    // Relación opcional a Ubicacion: cada equipo puede pertenecer a una ubicación
    @ManyToOne
    @JoinColumn(name = "ubicacion_id")
    // Allow read and write: frontend can send {ubicacion:{id}} and responses include ubicacion
    @JsonProperty(access = JsonProperty.Access.READ_WRITE)
    private Ubicacion ubicacion;

    public int getIdEquipo() {
        return idEquipo;
    }
    

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getMiembros() {
        return miembros;
    }

    public void setMiembros(int miembros) {
        this.miembros = miembros;
    }

    public EstadoEquipo getEstado() {
        return estado;
    }

    public void setEstado(EstadoEquipo estado) {
        this.estado = estado;
    }

    public Ubicacion getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(Ubicacion ubicacion) {
        this.ubicacion = ubicacion;
    }

   
}