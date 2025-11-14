package co.proyecto.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import co.proyecto.logic.UsuarioService;
import co.proyecto.model.Usuario;
import co.proyecto.model.enums.Rol;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    private final UsuarioService usuarioService;
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(LoginController.class);

    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
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
                                   RedirectAttributes redirectAttributes) {
        
        if (rolStr == null || rolStr.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Seleccione un rol.");
            return "redirect:/registro";
        }

        try {
            Rol rol = Rol.valueOf(rolStr);
            usuarioService.registrarUsuario(nombre, email, contrasena, rol);
            redirectAttributes.addFlashAttribute("mensaje", "Usuario registrado correctamente. Ya puedes iniciar sesión.");
            return "redirect:/login";
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
