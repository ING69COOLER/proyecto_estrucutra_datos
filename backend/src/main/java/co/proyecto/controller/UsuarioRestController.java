package co.proyecto.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;

@RestController
public class UsuarioRestController {

    @GetMapping("/api/usuario/actual")
    public ResponseEntity<UsuarioActualDTO> usuarioActual(HttpSession session) {
        Long id = (Long) session.getAttribute("usuarioId");
        String nombre = (String) session.getAttribute("usuarioNombre");
        String email = (String) session.getAttribute("usuarioEmail");
        String rol = (String) session.getAttribute("usuarioRol");

        // Si no hay usuario en sesión, devolvemos 401
        if (id == null || nombre == null || email == null || rol == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        UsuarioActualDTO dto = new UsuarioActualDTO(id, nombre, email, rol);
        return ResponseEntity.ok(dto);
    }

    // DTO sencillo para enviar al front
    public static class UsuarioActualDTO {
        private Long id;
        private String nombre;
        private String email;
        private String rol;

        public UsuarioActualDTO(Long id, String nombre, String email, String rol) {
            this.id = id;
            this.nombre = nombre;
            this.email = email;
            this.rol = rol;
        }

        public Long getId() { return id; }
        public String getNombre() { return nombre; }
        public String getEmail() { return email; }
        public String getRol() { return rol; }

        public void setId(Long id) { this.id = id; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public void setEmail(String email) { this.email = email; }
        public void setRol(String rol) { this.rol = rol; }
    }
}
