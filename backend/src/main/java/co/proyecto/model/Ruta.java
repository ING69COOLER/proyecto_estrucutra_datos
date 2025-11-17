package co.proyecto.model;

import co.proyecto.logic.estructuraGrafo.Arista;
import co.proyecto.logic.estructuraGrafo.Nodo;
import jakarta.persistence.*;
// 👇 IMPORTS AÑADIDOS
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;




// 👇 ANOTACIÓN AÑADIDA
@JsonIdentityInfo(
    generator = ObjectIdGenerators.PropertyGenerator.class,
    property = "id"
)
@Entity
public class Ruta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "origen_id", referencedColumnName = "id", nullable = false)
    private Ubicacion origen;   // referencia a entidad Ubicacion

    
    @ManyToOne(fetch = FetchType.EAGER) 
    @JoinColumn(name = "destino_id", referencedColumnName = "id", nullable = false)
    private Ubicacion destino;  // referencia a entidad Ubicacion

    private double distancia; // en km

    public Ruta() {} // Constructor vacío requerido por JPA


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Ubicacion getOrigen() {
        return origen;
    }

    public void setOrigen(Ubicacion origen) {
        this.origen = origen;
    }

    public Ubicacion getDestino() {
        return destino;
    }

    public void setDestino(Ubicacion destino) {
        this.destino = destino;
    }

    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

}