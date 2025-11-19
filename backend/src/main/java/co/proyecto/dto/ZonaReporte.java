package co.proyecto.dto;

import java.util.HashMap;
import java.util.Map;

public class ZonaReporte {
    private int totalZonas;
    private Map<String, Integer> zonasPorTipo = new HashMap<>();
    private Map<String, Integer> zonasPorNivelRiesgo = new HashMap<>();

    private int totalRecursos;
    private int recursosAsignados;
    private int recursosDisponibles;

    private int equiposTotales;
    private int equiposEnMision;

    private int personasAfectadasTotal;

    public ZonaReporte() {}

    public int getTotalZonas() { return totalZonas; }
    public void setTotalZonas(int totalZonas) { this.totalZonas = totalZonas; }

    public Map<String, Integer> getZonasPorTipo() { return zonasPorTipo; }
    public void setZonasPorTipo(Map<String, Integer> zonasPorTipo) { this.zonasPorTipo = zonasPorTipo; }

    public Map<String, Integer> getZonasPorNivelRiesgo() { return zonasPorNivelRiesgo; }
    public void setZonasPorNivelRiesgo(Map<String, Integer> zonasPorNivelRiesgo) { this.zonasPorNivelRiesgo = zonasPorNivelRiesgo; }

    public int getTotalRecursos() { return totalRecursos; }
    public void setTotalRecursos(int totalRecursos) { this.totalRecursos = totalRecursos; }

    public int getRecursosAsignados() { return recursosAsignados; }
    public void setRecursosAsignados(int recursosAsignados) { this.recursosAsignados = recursosAsignados; }

    public int getRecursosDisponibles() { return recursosDisponibles; }
    public void setRecursosDisponibles(int recursosDisponibles) { this.recursosDisponibles = recursosDisponibles; }

    public int getEquiposTotales() { return equiposTotales; }
    public void setEquiposTotales(int equiposTotales) { this.equiposTotales = equiposTotales; }

    public int getEquiposEnMision() { return equiposEnMision; }
    public void setEquiposEnMision(int equiposEnMision) { this.equiposEnMision = equiposEnMision; }

    public int getPersonasAfectadasTotal() { return personasAfectadasTotal; }
    public void setPersonasAfectadasTotal(int personasAfectadasTotal) { this.personasAfectadasTotal = personasAfectadasTotal; }
}
