package com.backend.usersapp.models.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


/**
 *
 * @author Geuris-Abreu-PC
 */
@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Size(min = 4, max = 8,message = "El usuario debe tener minimo 4 y maximo 8 caracteres.")
    @Column(unique = true)
    private String username;

    @NotBlank(message = "La contraseña no puede tener espacio en blanco.")
    private String password;
    
    @NotBlank
    @Email(message = "El email no es valido ej: correo@correo.com.")
    @Column(unique = true)
    private String email;
}
