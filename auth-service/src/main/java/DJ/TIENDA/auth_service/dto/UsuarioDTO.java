package DJ.TIENDA.auth_service.dto;

import lombok.Data;

// Representa los datos que nos devuelve ms-usuarios via Feign
// Solo necesitamos los campos relevantes para autenticación
@Data
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String email;
    private String password; // Para verificar la contraseña en el login
    private String rol;      // ADMIN o CLIENTE, para incluirlo en el JWT
}