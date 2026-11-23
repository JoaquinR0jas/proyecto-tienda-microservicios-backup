package DJ.TIENDA.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

// Datos que se mandan al iniciar sesion. Se usa una clase en vez de un Map
// para que Swagger muestre los campos reales (email y password).
@Data
public class LoginRequestDTO {

    @Schema(description = "Correo con el que se registro la cuenta", example = "admin@gmail.com")
    private String email;

    @Schema(description = "Clave de la cuenta", example = "admin123")
    private String password;
}
