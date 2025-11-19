package co.proyecto.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.proyecto.model.Recurso;
import co.proyecto.model.Ruta;
import co.proyecto.model.Ubicacion;
import co.proyecto.model.Usuario;
import co.proyecto.model.EquipoRescate;
import co.proyecto.model.enums.EstadoEquipo;
import co.proyecto.model.enums.Rol;
import co.proyecto.repository.RecursoRepository;
import co.proyecto.repository.RutaRepository;
import co.proyecto.repository.UbicacionRepository;
import co.proyecto.repository.EquipoRescateRepository;
import java.util.Map;
import java.util.Optional;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    
    private final UbicacionRepository ubicacionRepository;
    private final RecursoRepository recursoRepository;
    private final RutaRepository rutaRepository;
    private final EquipoRescateRepository equipoRepository;

    @Autowired
    public AdminController(UbicacionRepository ubicacionRepository, 
                          RecursoRepository recursoRepository, 
                          RutaRepository rutaRepository,
                          EquipoRescateRepository equipoRepository) {
        this.ubicacionRepository = ubicacionRepository;
        this.recursoRepository = recursoRepository;
        this.rutaRepository = rutaRepository;
        this.equipoRepository = equipoRepository;
    }

    /**
     * Endpoint para cargar datos de muestra en la base de datos
     * Solo accesible para usuarios con rol ADMINISTRADOR
     */
    @PostMapping("/cargar-muestra")
    public ResponseEntity<?> cargarMuestra(HttpSession session) {
        // Verificar autenticación
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            logger.warn("Intento de acceso sin autenticación a cargar-muestra");
            return ResponseEntity.status(401).body("No autenticado");
        }

        // Verificar que sea administrador
        if (usuario.getRol() != Rol.ADMINISTRADOR) {
            logger.warn("Usuario {} (Rol: {}) intentó acceder a función de administrador", 
                       usuario.getNombre(), usuario.getRol());
            return ResponseEntity.status(403).body("Acceso denegado: Se requieren permisos de Administrador");
        }

        try {
            logger.info("Usuario {} está cargando datos de muestra", usuario.getNombre());
            
            // Limpia datos previos
            ubicacionRepository.deleteAll();
            recursoRepository.deleteAll();
            rutaRepository.deleteAll();

            // Crear recursos de muestra
            Recurso r1 = new Recurso();
            r1.setNombre("Agua Potable");
            r1.setTipo("AGUA");
            r1.setCantidad(100);
            r1.setDisponible(true);
            recursoRepository.save(r1);

            Recurso r2 = new Recurso();
            r2.setNombre("Kit Médico de Emergencia");
            r2.setTipo("MEDICINA");
            r2.setCantidad(50);
            r2.setDisponible(true);
            recursoRepository.save(r2);

            Recurso r3 = new Recurso();
            r3.setNombre("Alimentos No Perecederos");
            r3.setTipo("ALIMENTO");
            r3.setCantidad(200);
            r3.setDisponible(true);
            recursoRepository.save(r3);

            logger.info("Recursos de muestra creados: {} items", 3);

            // Crear ubicaciones de muestra
            Ubicacion z1 = new Ubicacion();
            z1.setNombre("Zona Norte");
            z1.setTipo(co.proyecto.model.TipoUbicacion.URBANA);
            z1.setPersonasAfectadas(1200);
            z1.setNivelRiesgo("ALTO");
            z1.setLat(4.75);
            z1.setLng(-74.1);
            z1.setUpdatedAt(new Date());
            ubicacionRepository.save(z1);

            Ubicacion z2 = new Ubicacion();
            z2.setNombre("Zona Sur");
            z2.setTipo(co.proyecto.model.TipoUbicacion.RURAL);
            z2.setPersonasAfectadas(800);
            z2.setNivelRiesgo("MEDIO");
            z2.setLat(4.55);
            z2.setLng(-74.2);
            z2.setUpdatedAt(new Date());
            ubicacionRepository.save(z2);

            Ubicacion z3 = new Ubicacion();
            z3.setNombre("Zona Este");
            z3.setTipo(co.proyecto.model.TipoUbicacion.URBANA);
            z3.setPersonasAfectadas(500);
            z3.setNivelRiesgo("BAJO");
            z3.setLat(4.65);
            z3.setLng(-74.05);
            z3.setUpdatedAt(new Date());
            ubicacionRepository.save(z3);

            Ubicacion z4 = new Ubicacion();
            z4.setNombre("Zona Oeste");
            z4.setTipo(co.proyecto.model.TipoUbicacion.RURAL);
            z4.setPersonasAfectadas(1500);
            z4.setNivelRiesgo("CRITICO");
            z4.setLat(4.70);
            z4.setLng(-74.15);
            z4.setUpdatedAt(new Date());
            ubicacionRepository.save(z4);

            logger.info("Ubicaciones de muestra creadas: {} zonas", 4);

            // Crear rutas de muestra
            Ruta ruta1 = new Ruta();
            ruta1.setOrigen(z1);
            ruta1.setDestino(z2);
            ruta1.setDistancia(12.5);
            rutaRepository.save(ruta1);

            Ruta ruta2 = new Ruta();
            ruta2.setOrigen(z1);
            ruta2.setDestino(z3);
            ruta2.setDistancia(8.3);
            rutaRepository.save(ruta2);

            Ruta ruta3 = new Ruta();
            ruta3.setOrigen(z2);
            ruta3.setDestino(z4);
            ruta3.setDistancia(15.7);
            rutaRepository.save(ruta3);

            logger.info("Rutas de muestra creadas: {} rutas", 3);
            logger.info("Carga de datos de muestra completada exitosamente por {}", usuario.getNombre());

            return ResponseEntity.ok().body(
                String.format("Datos de muestra cargados correctamente por %s. " +
                             "Recursos: 3, Ubicaciones: 4, Rutas: 3", 
                             usuario.getNombre())
            );

        } catch (Exception e) {
            logger.error("Error al cargar datos de muestra", e);
            return ResponseEntity.status(500).body("Error al cargar datos: " + e.getMessage());
        }
    }

    /**
     * Endpoint para crear un Equipo de Rescate y guardarlo en la base de datos.
     * Solo accesible para usuarios con rol ADMINISTRADOR
     */
    @PostMapping("/equipos")
    public ResponseEntity<?> crearEquipo(@RequestBody EquipoRescate equipo, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        if (usuario.getRol() != Rol.ADMINISTRADOR) {
            logger.warn("Usuario {} (Rol: {}) intentó crear equipo sin permisos",
                       usuario.getNombre(), usuario.getRol());
            return ResponseEntity.status(403).body("Acceso denegado");
        }

        try {
            // Validaciones básicas
            if (equipo.getTipo() == null || equipo.getTipo().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("El campo 'tipo' es requerido");
            }

            if (equipo.getMiembros() <= 0) {
                return ResponseEntity.badRequest().body("El número de miembros debe ser mayor a 0");
            }

            EquipoRescate saved = equipoRepository.save(equipo);
            logger.info("Usuario {} creó el equipo con id {}", usuario.getNombre(), saved.getIdEquipo());
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            logger.error("Error al crear equipo de rescate", e);
            return ResponseEntity.status(500).body("Error al crear equipo: " + e.getMessage());
        }
    }

    /**
     * Endpoint para listar equipos de rescate.
     * Solo accesible para usuarios con rol ADMINISTRADOR
     */
    @GetMapping("/equipos")
    public ResponseEntity<?> listarEquipos(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        if (usuario.getRol() != Rol.ADMINISTRADOR) {
            logger.warn("Usuario {} (Rol: {}) intentó listar equipos sin permisos",
                       usuario.getNombre(), usuario.getRol());
            return ResponseEntity.status(403).body("Acceso denegado");
        }

        try {
            List<EquipoRescate> equipos = equipoRepository.findAll();
            return ResponseEntity.ok(equipos);
        } catch (Exception e) {
            logger.error("Error al listar equipos", e);
            return ResponseEntity.status(500).body("Error al listar equipos: " + e.getMessage());
        }
    }

    @PutMapping("/equipos/{id}/ubicacion")
    public ResponseEntity<?> asignarEquipoUbicacion(@PathVariable int id, @RequestBody Map<String,Integer> body, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return ResponseEntity.status(401).body("No autenticado");
        if (usuario.getRol() != Rol.ADMINISTRADOR) return ResponseEntity.status(403).body("Acceso denegado");

        try {
            Integer ubicacionId = body.get("ubicacionId");
            if (ubicacionId == null) return ResponseEntity.badRequest().body("ubicacionId requerido");

            Optional<co.proyecto.model.EquipoRescate> eOpt = equipoRepository.findById(id);
            if (!eOpt.isPresent()) return ResponseEntity.status(404).body("Equipo no encontrado");

            Optional<Ubicacion> uOpt = ubicacionRepository.findById(ubicacionId);
            if (!uOpt.isPresent()) return ResponseEntity.status(404).body("Ubicacion no encontrada");

            co.proyecto.model.EquipoRescate equipo = eOpt.get();
            // Si ya tiene una ubicacion distinta, no permitir reasignar
            if (equipo.getUbicacion() != null && equipo.getUbicacion().getId() != uOpt.get().getId()) {
                return ResponseEntity.status(409).body("Equipo ya asignado a otra ubicacion");
            }

            equipo.setUbicacion(uOpt.get());
            equipo.setEstado(EstadoEquipo.EN_MISION);
            equipoRepository.save(equipo);
            return ResponseEntity.ok(equipo);
        } catch (Exception e) {
            logger.error("Error asignando equipo a ubicacion", e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    @PutMapping("/equipos/{id}/desasignar")
    public ResponseEntity<?> desasignarEquipo(@PathVariable int id, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        if (usuario == null) return ResponseEntity.status(401).body("No autenticado");
        if (usuario.getRol() != Rol.ADMINISTRADOR) return ResponseEntity.status(403).body("Acceso denegado");

        try {
            Optional<co.proyecto.model.EquipoRescate> eOpt = equipoRepository.findById(id);
            if (!eOpt.isPresent()) return ResponseEntity.status(404).body("Equipo no encontrado");

            co.proyecto.model.EquipoRescate equipo = eOpt.get();
            equipo.setUbicacion(null);
            equipo.setEstado(EstadoEquipo.DISPONIBLE);
            equipoRepository.save(equipo);
            return ResponseEntity.ok(equipo);
        } catch (Exception e) {
            logger.error("Error desasignando equipo", e);
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }

    /**
     * Endpoint para limpiar todos los datos de la base de datos
     * Solo accesible para usuarios con rol ADMINISTRADOR
     */
    @DeleteMapping("/limpiar-datos")
    public ResponseEntity<?> limpiarDatos(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuario == null) {
            return ResponseEntity.status(401).body("No autenticado");
        }

        if (usuario.getRol() != Rol.ADMINISTRADOR) {
            logger.warn("Usuario {} (Rol: {}) intentó limpiar datos sin permisos", 
                       usuario.getNombre(), usuario.getRol());
            return ResponseEntity.status(403).body("Acceso denegado");
        }

        try {
            logger.warn("Usuario {} está limpiando TODOS los datos", usuario.getNombre());
            
            ubicacionRepository.deleteAll();
            recursoRepository.deleteAll();
            rutaRepository.deleteAll();
            
            logger.info("Datos limpiados exitosamente por {}", usuario.getNombre());
            
            return ResponseEntity.ok().body(
                String.format("Todos los datos han sido eliminados por %s", usuario.getNombre())
            );
        } catch (Exception e) {
            logger.error("Error al limpiar datos", e);
            return ResponseEntity.status(500).body("Error al limpiar datos: " + e.getMessage());
        }
    }
}