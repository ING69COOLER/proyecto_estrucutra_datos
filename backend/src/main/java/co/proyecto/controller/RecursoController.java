package co.proyecto.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.proyecto.model.Recurso;
import co.proyecto.repository.RecursoRepository;


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

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        recursoRepository.deleteById(id);
    }

    // ...puedes agregar PUT, etc. según lo necesite el frontend...
}
