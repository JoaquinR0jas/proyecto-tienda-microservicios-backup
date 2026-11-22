package DJ.TIENDA.ms_usuarios.dto;

import lombok.Data;

// Datos que se le pasan a auth-service para revisar el login.
// Es el unico lugar donde viaja la clave encriptada; nunca se devuelve al navegador.
@Data
public class UsuarioLoginDTO {
    private Long id;
    private String run;
    private String nombre;
    private String apellidos;
    private String email;
    private String password;
    private String rol;
    private String estado;
}
