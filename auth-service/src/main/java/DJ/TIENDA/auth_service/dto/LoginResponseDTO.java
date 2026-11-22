package DJ.TIENDA.auth_service.dto;

import lombok.Data;

// Respuesta del login: el token y los datos del usuario para guardar la sesion en el frontend
// (nunca se devuelve la contraseña)
@Data
public class LoginResponseDTO {
    private String token;
    private String tipo = "Bearer";
    private Usuario usuario;

    @Data
    public static class Usuario {
        private Long id;
        private String run;
        private String nombre;
        private String apellidos;
        private String email;
        private String rol;
    }
}
