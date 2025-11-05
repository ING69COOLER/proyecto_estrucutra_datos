package co.proyecto.controller;

import co.proyecto.model.Recurso;
import co.proyecto.repository.RecursoRepository;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/recursos")
public class RecursoController {

    private final RecursoRepository recursoRepository;

    public RecursoController(RecursoRepository recursoRepository) {
        this.recursoRepository = recursoRepository;
    }

    @GetMapping
    public List<Recurso> getAll() {
        return recursoRepository.findAll();
    }

    @PostMapping
    public Recurso create(@RequestBody Recurso recurso) {
        return recursoRepository.save(recurso);
    }

    // ...puedes agregar PUT, DELETE, etc. según lo necesite el frontend...
}
