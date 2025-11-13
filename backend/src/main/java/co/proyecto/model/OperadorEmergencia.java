package co.proyecto.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "id_usuario")
public class OperadorEmergencia extends Usuario {
    // Métodos específicos del operador de emergencia
}