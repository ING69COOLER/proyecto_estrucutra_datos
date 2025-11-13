package co.proyecto.model;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;

@Entity
@PrimaryKeyJoinColumn(name = "id_usuario")
public class Administrador extends Usuario {
    // Métodos específicos del administrador
}