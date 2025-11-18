package co.proyecto.dto;

public class ReporteRecursosDTO {
    private String ubicacionNombre;
    private String tipoRecurso;
    private int disponibles;
    private int necesarios;
    private String nivelRiesgo;

    public ReporteRecursosDTO() {}

    public ReporteRecursosDTO(String ubicacionNombre, String tipoRecurso, 
                             int disponibles, int necesarios, String nivelRiesgo) {
        this.ubicacionNombre = ubicacionNombre;
        this.tipoRecurso = tipoRecurso;
        this.disponibles = disponibles;
        this.necesarios = necesarios;
        this.nivelRiesgo = nivelRiesgo;
    }

    // Getters y Setters
    public String getUbicacionNombre() {
        return ubicacionNombre;
    }

    public void setUbicacionNombre(String ubicacionNombre) {
        this.ubicacionNombre = ubicacionNombre;
    }

    public String getTipoRecurso() {
        return tipoRecurso;
    }

    public void setTipoRecurso(String tipoRecurso) {
        this.tipoRecurso = tipoRecurso;
    }

    public int getDisponibles() {
        return disponibles;
    }

    public void setDisponibles(int disponibles) {
        this.disponibles = disponibles;
    }

    public int getNecesarios() {
        return necesarios;
    }

    public void setNecesarios(int necesarios) {
        this.necesarios = necesarios;
    }

    public String getNivelRiesgo() {
        return nivelRiesgo;
    }

    public void setNivelRiesgo(String nivelRiesgo) {
        this.nivelRiesgo = nivelRiesgo;
    }
}
