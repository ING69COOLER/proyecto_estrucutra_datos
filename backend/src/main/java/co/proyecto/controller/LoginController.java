package co.proyecto.controller;

import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import co.proyecto.logic.UsuarioService;
import co.proyecto.model.Usuario;
import co.proyecto.model.enums.Rol;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    private final UsuarioService usuarioService;

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

        if (usuario.getRol() == Rol.ADMINISTRADOR) {
            return "redirect:/admin/inicio";
        }

        if (usuario.getRol() == Rol.OPERADOR) {
            return "redirect:/operador/inicio";
        }

        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout";
    }

}
