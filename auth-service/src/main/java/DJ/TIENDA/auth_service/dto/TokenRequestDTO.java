package DJ.TIENDA.auth_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

// Datos para renovar el token. Igual que el login, se usa una clase
// para que Swagger muestre el campo en vez de "additionalProp".
@Data
public class TokenRequestDTO {

    @Schema(description = "Token JWT actual que se quiere renovar",
            example = "eyJhbGciOiJIUzI1NiJ9...")
    private String token;
}
