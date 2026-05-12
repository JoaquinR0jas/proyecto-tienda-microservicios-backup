package DJ.TIENDA.ms_envios.dto;

import lombok.Data;

// Datos que recibimos de ms-usuarios via Feign
@Data
public class UsuarioDTO {
    private Long id;
    private String nombre;
    private String email;
    private String direccion; // Solo nos interesa la direccion
}