package co.proyecto.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class MockupController {

    @GetMapping("/mockup")
    public String mostrarMockup(HttpSession session) {
        // Verificamos que haya alguien logueado
        Object usuarioObj = session.getAttribute("usuarioLogueado");
        if (usuarioObj == null) {
            System.out.println(">>> /mockup: no hay usuario en sesión, redirigiendo a /login");
            return "redirect:/login";
        }

        // Rol guardado en sesión por el LoginController
        String rol = (String) session.getAttribute("usuarioRol");
        System.out.println(">>> /mockup: usuarioLogueado en sesión, rol = " + rol);

        if ("ADMINISTRADOR".equals(rol)) {
            System.out.println(">>> /mockup: vista ADMIN -> mockup_admin.html");
            return "forward:/mockup_admin.html";
        }

        // Cualquier otro rol (OPERADOR, null, etc.) va al mockup de operador
        System.out.println(">>> /mockup: vista OPERADOR -> mockup_operador.html");
        return "forward:/mockup_operador.html";
    }
}


