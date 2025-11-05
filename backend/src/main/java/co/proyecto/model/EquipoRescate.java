package co.proyecto.model;
import co.proyecto.model.enums.EstadoEquipo;
import jakarta.persistence.*;

@Entity
public class EquipoRescate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idEquipo;

    private String tipo;
    private int miembros;

    @Enumerated(EnumType.STRING)
    private EstadoEquipo estado;

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

   
}