package DJ.TIENDA.ms_usuarios.model;

// ESTE MS CUBRE EL DE 👤 user-service (Usuarios): Responsable del registro de nuevos usuarios y gestión de sus datos personales.
// y tambien el security service, ya que dimos un rol y el profe pidio eso, ya tenemos gestion de roles
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email inválido")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Column(nullable = false)
    private String password;

    @Column
    private String telefono;

    @Column
    private String direccion;

    @Enumerated(EnumType.STRING)  // Guarda el rol como texto en la BD (ej: "ADMIN", "CLIENTE")
    @Column(nullable = false)
    private Rol rol = Rol.CLIENTE; // Por defecto todo usuario nuevo es CLIENTE

    // Enum interno: define los roles posibles del sistema
    public enum Rol {
        ADMIN,
        CLIENTE
    }
}