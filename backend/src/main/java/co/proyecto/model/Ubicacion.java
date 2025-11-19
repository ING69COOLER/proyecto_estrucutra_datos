package co.proyecto.model;

import jakarta.persistence.*;
import java.util.List;
import java.util.Set;

// 👇 IMPORTS AÑADIDOS
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Objects;
// import java.util.Set; (no usado)
import java.util.Date;


//// 👇 ANOTACIÓN AÑADIDA
//@JsonIdentityInfo(
  //  generator = ObjectIdGenerators.PropertyGenerator.class,
    //property = "id"
//)
@Entity
public class Ubicacion implements Comparable<Ubicacion> {
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

    // Lista de recursos asociados a esta ubicación (uno a muchos)
    @OneToMany(mappedBy = "ubicacion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Recurso> recursosNecesarios;

    // Equipos asignados a esta ubicación
    @OneToMany(mappedBy = "ubicacion", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<co.proyecto.model.EquipoRescate> equipos;
    

    @JsonIgnore
    @OneToMany(mappedBy = "origen")
    private List<Ruta> rutasOrigen;

    @JsonIgnore
    @OneToMany(mappedBy = "destino")
    private List<Ruta> rutasDestino;

    public Ubicacion() {
    }

    private Integer getValorRiesgo(String nivelRiesgo) {
        if (nivelRiesgo == null) {
            return 5; // Prioridad más baja si no tiene riesgo
        }
        
        switch (nivelRiesgo) {
            case "CRITICO":
                return 1;
            case "ALTO":
                return 2;
            case "MEDIO":
                return 3;
            case "BAJO":
                return 4;
            default:
                return 5; // Prioridad más baja para cualquier otro valor
        }
    }

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

    public Set<co.proyecto.model.EquipoRescate> getEquipos() {
        return equipos;
    }

    public void setEquipos(Set<co.proyecto.model.EquipoRescate> equipos) {
        this.equipos = equipos;
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


    @Override
    public String toString() {
        return "Ubicacion [id=" + id + ", nombre=" + nombre + ", tipo=" + tipo + ", personasAfectadas="
                + personasAfectadas + ", nivelRiesgo=" + nivelRiesgo + ", lat=" + lat + ", lng=" + lng + ", updatedAt="
                + updatedAt + "]";
    }

    @Override
    public int compareTo(Ubicacion arg0) {
        return getValorRiesgo(nivelRiesgo).compareTo(getValorRiesgo(arg0.getNivelRiesgo()));
    }

    @Override
    public boolean equals(Object o) {
        // Si son la misma instancia de memoria, son iguales
        if (this == o) return true;
        
        // Si el otro objeto es nulo o no es de la clase Ubicacion, no son iguales
        if (o == null || getClass() != o.getClass()) return false;
        
        // Convertimos el objeto 'o' a Ubicacion
        Ubicacion ubicacion = (Ubicacion) o;
        
        // Comparamos lo único que importa: el ID.
        // Usamos Objects.equals para manejar de forma segura si el id es 0 o nulo
        return Objects.equals(id, ubicacion.id);
    }

    @Override
    public int hashCode() {
        // Generamos un "hash" basado solamente en el ID.
        return Objects.hash(id);
    }

}