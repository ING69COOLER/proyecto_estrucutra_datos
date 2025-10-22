package co.proyecto.proyecto_estrucutra_datos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
public class ProyectoEstrucutraDatosApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProyectoEstrucutraDatosApplication.class, args);
	}

}

@Controller
class InicioController {
	@GetMapping("/")
	public String inicio() {
		return "inicio";
	}
}
