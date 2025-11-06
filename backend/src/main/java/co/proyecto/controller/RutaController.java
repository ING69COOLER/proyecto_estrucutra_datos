package co.proyecto.controller;

import co.proyecto.model.Ruta;
import co.proyecto.repository.RutaRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/rutas")
public class RutaController {

    private final RutaRepository rutaRepository;

    

    public RutaController(RutaRepository rutaRepository) {
        this.rutaRepository = rutaRepository;
    }

    @GetMapping
    public List<Ruta> getAll() {
        return rutaRepository.findAll();
    }

    @PostMapping
    public Ruta create(@RequestBody Ruta ruta) {
        return rutaRepository.save(ruta);
    }

    // ...puedes agregar PUT, DELETE, etc. según lo necesite el frontend...
}
