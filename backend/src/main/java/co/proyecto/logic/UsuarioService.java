package co.proyecto.logic;

import java.util.Optional;

import org.springframework.stereotype.Service;

import co.proyecto.model.Administrador;
import co.proyecto.model.OperadorEmergencia;
import co.proyecto.model.Usuario;
import co.proyecto.model.enums.Rol;
import co.proyecto.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> login(String email, String contrasena) {
        return usuarioRepository.findByEmailAndContrasena(email, contrasena);
    }
    
     public void registrarUsuario(String nombre,
                                 String email,
                                 String contrasena,
                                 Rol rol) {

        // Validar que los campos no estén vacíos
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("El email no puede estar vacío.");
        }
        if (contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        if (rol == null) {
            throw new IllegalArgumentException("El rol no puede estar vacío.");
        }

        Usuario usuario;

        if (rol == Rol.ADMINISTRADOR) {
            usuario = new Administrador();
        } else if (rol == Rol.OPERADOR) {
            usuario = new OperadorEmergencia();
        } else {
            throw new IllegalArgumentException("Rol no válido");
        }

        usuario.setNombre(nombre);
        usuario.setEmail(email);
        usuario.setContrasena(contrasena);
        usuario.setRol(rol);

        usuarioRepository.save(usuario);
    }
}
