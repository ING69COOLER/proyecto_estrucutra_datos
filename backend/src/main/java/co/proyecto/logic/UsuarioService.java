package co.proyecto.logic;

import co.proyecto.repository.UsuarioRepository;
import co.proyecto.model.Usuario;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> login(String email, String contrasena) {
        return usuarioRepository.findByEmailAndContrasena(email, contrasena);
    }

}
