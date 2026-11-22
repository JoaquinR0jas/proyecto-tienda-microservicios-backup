package DJ.TIENDA.ms_usuarios.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El RUN es obligatorio")
    @Size(min = 7, max = 9, message = "El RUN debe tener entre 7 y 9 caracteres, sin puntos ni guion")
    @Column(nullable = false, unique = true, length = 9)
    private String run;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 50, message = "El nombre puede tener maximo 50 caracteres")
    @Column(nullable = false, length = 50)
    private String nombre;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Size(max = 100, message = "Los apellidos pueden tener maximo 100 caracteres")
    @Column(nullable = false, length = 100)
    private String apellidos;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Formato de email invalido")
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @NotBlank(message = "La contrasena es obligatoria")
    @Size(min = 4, max = 72, message = "La contrasena debe tener entre 4 y 72 caracteres")
    @Column(nullable = false)
    // WRITE_ONLY significa que la clave se puede enviar al crear/editar,
    // pero nunca sale en las respuestas JSON de la API
    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column
    private LocalDate fechaNacimiento; // Opcional

    @NotNull(message = "La region es obligatoria")
    @Column(nullable = false)
    private Long regionId; // Referencia a la tabla regiones de este mismo microservicio

    @NotNull(message = "La comuna es obligatoria")
    @Column(nullable = false)
    private Long comunaId; // Referencia a la tabla comunas de este mismo microservicio

    @NotBlank(message = "La direccion es obligatoria")
    @Size(max = 300, message = "La direccion puede tener maximo 300 caracteres")
    @Column(nullable = false, length = 300)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol = Rol.CLIENTE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.ACTIVO; // Un usuario INACTIVO no puede iniciar sesion

    public enum Rol {
        ADMIN,     // Acceso a todo el panel de administracion
        VENDEDOR,  // Ve productos, ordenes y mensajes, sin editar
        CLIENTE    // Solo compra en la tienda
    }

    public enum Estado {
        ACTIVO,
        INACTIVO
    }
}
