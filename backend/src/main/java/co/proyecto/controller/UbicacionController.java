package co.proyecto.controller;

import co.proyecto.model.Ubicacion;
import co.proyecto.repository.UbicacionRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/zonas")
public class UbicacionController {

    private final UbicacionRepository ubicacionRepository;

    public UbicacionController(UbicacionRepository ubicacionRepository) {
        this.ubicacionRepository = ubicacionRepository;
    }
    //obtiene todas las ubicaciones
    @GetMapping
    public List<Ubicacion> getAll() {
        // Si usas ListaDobleSimple en el repositorio, conviértelo a List antes de retornar
        // return ubicacionRepository.findAll().toList(); // Ejemplo si tu lista personalizada tiene un método toList()
        return ubicacionRepository.findAll(); // Si ya retorna List, no necesitas adaptar
    }

    @GetMapping("/{id}")
    public String getMethodName(@RequestParam int id) {
        System.out.println("sfgdgdfgdsgdfggdfgsdggdfgdgfd"+id+ubicacionRepository.findById(valor));
        return ubicacionRepository.findById(id)
                .map(Ubicacion::toString)
                .orElse("Ubicacion not found");

    }
    //guarda una ubicacion
    @PostMapping
    public Ubicacion create(@RequestBody Ubicacion ubicacion) {
        ubicacion.setUpdatedAt(new java.util.Date());
        return ubicacionRepository.save(ubicacion);
    }
    // ...puedes agregar PUT, DELETE, etc. según lo necesite el frontend...
}
