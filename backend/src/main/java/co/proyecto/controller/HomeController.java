package co.proyecto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import co.proyecto.model.Usuario;
import jakarta.servlet.http.HttpSession;

@Controller
public class HomeController {

    /**
     * Ruta raíz "/" - Redirige al login si no hay sesión, 
     * o al mockup si hay sesión activa
     */
    @GetMapping("/")
    public String index(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");
        
        if (usuario == null) {
            // Si no hay sesión, redirigir al login
            return "redirect:/login";
        }
        
        // Si hay sesión, redirigir al mockup
        return "redirect:/mockup";
    }
}