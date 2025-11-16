package co.proyecto.controller;

import co.proyecto.dto.RutaRequest;
import co.proyecto.logic.Clientes.RutaClient;
import co.proyecto.model.Ruta;
import co.proyecto.model.Ubicacion;
import co.proyecto.repository.RutaRepository;

import org.springframework.http.HttpStatus;
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

     @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public List<Ruta> getAll() {
        return rutaRepository.findAll();
    }

   
    @PostMapping
    public Ruta create(@RequestBody Ruta ruta) {
        return rutaRepository.save(ruta);
    }

    // ...puedes agregar PUT, DELETE, etc. según lo necesite el frontend...

    @PostMapping("/crear")
    public ResponseEntity<?> crearRuta(@RequestBody RutaRequest request) {
        try{
        Ruta ruta = new Ruta();
        ruta.setOrigen(request.getOrigen());
        ruta.setDestino(request.getDestino());
        RutaClient cliente = new RutaClient();
        ruta.setDistancia(cliente.calcularDistancia(request.getOrigen().getLat(), request.getOrigen().getLng(), request.getDestino().getLat(), request.getDestino().getLng()));
        return ResponseEntity.ok("se guardo correctamente");
            } catch (Exception e){
                return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body("Ocurrió un error: " + e.getMessage());
        }
        
    }
}
