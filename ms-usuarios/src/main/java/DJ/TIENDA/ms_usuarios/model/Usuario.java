package DJ.TIENDA.ms_usuarios.model;
// ESTE MS CUBRE EL DE 👤 user-service (Usuarios): Responsable del registro de nuevos usuarios y gestión de sus datos personales.

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data                          // Lombok: genera getters, setters, toString, equals
@Entity                        // Marca esta clase como tabla en la BD
@Table(name = "usuarios")      // Nombre de la tabla en MySQL
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremental
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")     // Validación: no puede estar vacío
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")       // Validación: formato email correcto
    @Column(nullable = false, unique = true)            // Único en la BD
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    private String password;   // ⚠️ Próximamente encriptar con BCrypt cuando hagamos seguridad

    @Column
    private String telefono;   // Opcional

    @Column
    private String direccion;  // Opcional
}