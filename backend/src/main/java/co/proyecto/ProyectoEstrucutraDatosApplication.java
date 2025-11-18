package co.proyecto;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
// 🔹 Solo escanea entidades dentro de co.proyecto.model
@EntityScan(basePackages = {"co.proyecto.model"})
// 🔹 Solo escanea componentes (controladores, servicios, etc.) en estos paquetes
@ComponentScan(basePackages = {
    "co.proyecto.controller", 
    "co.proyecto.service", 
    "co.proyecto.logic",
    "co.proyecto.estructuras"
})
// 🔹 Solo busca repositorios JPA aquí
@EnableJpaRepositories(basePackages = {"co.proyecto.repository"})
public class ProyectoEstrucutraDatosApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProyectoEstrucutraDatosApplication.class, args);
    }
}

@Controller
class InicioController {
    @GetMapping("/")
    public String inicio() {
		return "forward:/mockup.html";
	}
}

