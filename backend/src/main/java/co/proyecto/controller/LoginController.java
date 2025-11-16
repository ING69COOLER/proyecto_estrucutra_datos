package co.proyecto.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import co.proyecto.logic.UsuarioService;
import co.proyecto.logic.Clientes.PruebaGrafo;
import co.proyecto.model.Usuario;
import co.proyecto.model.enums.Rol;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    private final UsuarioService usuarioService;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LoginController.class);
    private final PruebaGrafo pruebasGrafo; // Spring inyectará

    public LoginController(UsuarioService usuarioService, PruebaGrafo pruebaGrafo) {
        this.usuarioService = usuarioService;
        this.pruebasGrafo = pruebaGrafo;
    }

    @GetMapping("/login")
    public String mostrarLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String email,
                                @RequestParam String contrasena,
                                HttpSession session,
                                Model model) {

        
        pruebasGrafo.verificarGrafo();
        
        Optional<Usuario> usuarioOpt = usuarioService.login(email, contrasena);

        if (usuarioOpt.isEmpty()) {
            model.addAttribute("error", "Email o contraseña incorrectos");
            return "login";
        }

        Usuario usuario = usuarioOpt.get();

        session.setAttribute("usuarioLogueado", usuario);
        session.setAttribute("usuarioId", usuario.getId());
        session.setAttribute("usuarioNombre", usuario.getNombre());
        session.setAttribute("usuarioEmail", usuario.getEmail());
        session.setAttribute("usuarioRol", usuario.getRol().toString());

        logger.info("Usuario autenticado: {} (ID: {}, Rol: {})",
                usuario.getNombre(), usuario.getId(), usuario.getRol());

        return "redirect:/mockup";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }

    @PostMapping("/registro")
public String procesarRegistro(@RequestParam String nombre,
                               @RequestParam String email,
                               @RequestParam String contrasena,
                               @RequestParam(name = "rol", required = false) String rolStr,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {

    if (rolStr == null || rolStr.isBlank()) {
        redirectAttributes.addFlashAttribute("error", "Seleccione un rol.");
        return "redirect:/registro";
    }

    try {
        // 1. Convertimos el rol del form al enum
        Rol rol = Rol.valueOf(rolStr);

        // 2. Registramos el usuario
        usuarioService.registrarUsuario(nombre, email, contrasena, rol);
        logger.info("Usuario registrado: {} ({}, rol={})", nombre, email, rol);

        // 3. Auto-login: usamos el mismo servicio de login
        Optional<Usuario> usuarioOpt = usuarioService.login(email, contrasena);

        if (usuarioOpt.isEmpty()) {
            // Algo raro pasó: lo registramos pero no lo encontramos para login
            redirectAttributes.addFlashAttribute("mensaje",
                    "Usuario registrado correctamente. Inicia sesión con tus credenciales.");
            return "redirect:/login";
        }

        Usuario usuario = usuarioOpt.get();

        // 4. Guardar usuario en sesión igual que en el login normal
        session.setAttribute("usuarioLogueado", usuario);
        session.setAttribute("usuarioId", usuario.getId());
        session.setAttribute("usuarioNombre", usuario.getNombre());
        session.setAttribute("usuarioEmail", usuario.getEmail());
        session.setAttribute("usuarioRol", usuario.getRol().toString());

        logger.info("Usuario logueado automáticamente tras registro: {} (ID: {}, Rol: {})",
                usuario.getNombre(), usuario.getId(), usuario.getRol());

        // 5. Redirigir a /mockup -> MockupController decide admin/operador
        return "redirect:/mockup";

    } catch (IllegalArgumentException e) {
        redirectAttributes.addFlashAttribute("error", "Rol inválido o datos incorrectos: " + e.getMessage());
        return "redirect:/registro";
    } catch (Exception e) {
        logger.error("Error al registrar usuario", e);
        redirectAttributes.addFlashAttribute("error", "Error al registrar usuario: " + e.getMessage());
        return "redirect:/registro";
    }
}


}
