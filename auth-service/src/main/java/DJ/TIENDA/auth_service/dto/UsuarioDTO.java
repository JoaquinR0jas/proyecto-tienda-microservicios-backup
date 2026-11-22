package DJ.TIENDA.auth_service.dto;

import lombok.Data;

// Representa los datos que nos devuelve ms-usuarios via Feign
@Data
public class UsuarioDTO {
    private Long id;
    private String run;
    private String nombre;
    private String apellidos;
    private String email;
    private String password; // Para verificar la contraseña en el login
    private String rol;      // ADMIN, VENDEDOR o CLIENTE, para incluirlo en el JWT
    private String estado;   // ACTIVO o INACTIVO, un usuario INACTIVO no puede entrar
}
