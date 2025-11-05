package co.proyecto.controller;

import co.proyecto.model.Ubicacion;
import co.proyecto.model.Recurso;
import co.proyecto.model.Ruta;
import java.util.Arrays;
import java.util.Date;
import org.springframework.web.bind.annotation.*;

import co.proyecto.repository.UbicacionRepository;
import co.proyecto.repository.RecursoRepository;
import co.proyecto.repository.RutaRepository;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UbicacionRepository ubicacionRepository;
    private final RecursoRepository recursoRepository;
    private final RutaRepository rutaRepository;

    @Autowired
    public AdminController(UbicacionRepository ubicacionRepository, RecursoRepository recursoRepository, RutaRepository rutaRepository) {
        this.ubicacionRepository = ubicacionRepository;
        this.recursoRepository = recursoRepository;
        this.rutaRepository = rutaRepository;
    }

    @PostMapping("/cargar-muestra")
    public void cargarMuestra() {
        // Limpia datos previos
        ubicacionRepository.deleteAll();
        recursoRepository.deleteAll();
        rutaRepository.deleteAll();

        // Recursos de muestra
        Recurso r1 = new Recurso(); r1.setNombre("Agua"); r1.setTipo("AGUA"); r1.setCantidad(100); r1.setDisponible(true);
        Recurso r2 = new Recurso(); r2.setNombre("Medicinas"); r2.setTipo("MEDICINA"); r2.setCantidad(50); r2.setDisponible(true);
        recursoRepository.save(r1);
        recursoRepository.save(r2);

        Ubicacion z1 = new Ubicacion();
        z1.setNombre("Zona Norte");
        z1.setTipo(co.proyecto.model.TipoUbicacion.URBANA);
        z1.setPersonasAfectadas(1200);
        z1.setNivelRiesgo("ALTO");
        z1.setLat(4.75); z1.setLng(-74.1);
        z1.setUpdatedAt(new Date());
        z1.setRecursosNecesarios(Arrays.asList(r1, r2));
        z1.setRecursosDisponibles(Arrays.asList(r1));

        Ubicacion z2 = new Ubicacion();
        z2.setNombre("Zona Sur");
        z2.setTipo(co.proyecto.model.TipoUbicacion.RURAL);
        z2.setPersonasAfectadas(800);
        z2.setNivelRiesgo("MEDIO");
        z2.setLat(4.55); z2.setLng(-74.2);
        z2.setUpdatedAt(new Date());
        z2.setRecursosNecesarios(Arrays.asList(r2));
        z2.setRecursosDisponibles(Arrays.asList(r2));

        ubicacionRepository.save(z1);
        ubicacionRepository.save(z2);

        // Ruta de muestra
        Ruta ruta = new Ruta();
        Ubicacion origen = ubicacionRepository.findById(z1.getId()).orElse(null);
        Ubicacion destino = ubicacionRepository.findById(z2.getId()).orElse(null);
        ruta.setOrigen(origen);
        ruta.setDestino(destino);
        ruta.setDistancia(12.5);
        rutaRepository.save(ruta);
    }
}
