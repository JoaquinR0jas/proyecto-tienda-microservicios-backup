package DJ.TIENDA.ms_envios.client;

import DJ.TIENDA.ms_envios.dto.UsuarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// Se conecta a ms-usuarios via Eureka para obtener la direccion del usuario
@FeignClient(name = "ms-usuarios")
public interface UsuarioClient {

    // Llama al endpoint GET /api/usuarios/{id} de ms-usuarios
    @GetMapping("/api/usuarios/{id}")
    UsuarioDTO obtenerUsuario(@PathVariable("id") Long id);
}