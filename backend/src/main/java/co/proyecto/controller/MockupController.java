package co.proyecto.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class MockupController {

    private static final Logger logger = LoggerFactory.getLogger(MockupController.class);

    @GetMapping("/mockup")
    public String mostrarMockup(HttpSession session) {

        // 1. Verificamos que haya sesión
        Object usuarioObj = session.getAttribute("usuarioLogueado");
        if (usuarioObj == null) {
            logger.info("Acceso a /mockup SIN sesión, redirigiendo a /login");
            return "redirect:/login";
        }

        // 2. Leemos los datos que tú mismo guardaste en el LoginController
        Long usuarioId = (Long) session.getAttribute("usuarioId");
        String usuarioNombre = (String) session.getAttribute("usuarioNombre");
        String usuarioEmail = (String) session.getAttribute("usuarioEmail");
        String usuarioRol = (String) session.getAttribute("usuarioRol");

        // 3. Log bonito para saber quién entró al mockup
        logger.info(
            "Acceso a /mockup -> Usuario: {} (email: {}, id: {}, rol: {})",
            usuarioNombre,
            usuarioEmail,
            usuarioId,
            usuarioRol
        );

        // 4. Elegir qué mockup ver según el rol
        if ("ADMINISTRADOR".equals(usuarioRol)) {
            logger.info("Mostrando MOCKUP ADMIN para usuario id={}", usuarioId);
            return "forward:/mockup_admin.html";
        }

        logger.info("Mostrando MOCKUP OPERADOR para usuario id={}", usuarioId);
        return "forward:/mockup_operador.html";
    }
}



