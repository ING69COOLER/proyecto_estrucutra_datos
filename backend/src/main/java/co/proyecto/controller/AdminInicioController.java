package co.proyecto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import co.proyecto.model.Usuario;
import co.proyecto.model.enums.Rol;
import jakarta.servlet.http.HttpSession;

@Controller
public class AdminInicioController {

    @GetMapping("/admin/inicio")
    public String inicioAdmin(HttpSession session, Model model) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            return "redirect:/login";
        }

        if (usuario.getRol() != Rol.ADMINISTRADOR) {
            session.invalidate();
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        return "admin_inicio";
    }

}
