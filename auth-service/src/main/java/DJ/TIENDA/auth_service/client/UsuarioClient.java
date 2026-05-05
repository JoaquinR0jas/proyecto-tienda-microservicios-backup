package DJ.TIENDA.auth_service.client;

import DJ.TIENDA.auth_service.dto.UsuarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Se conecta a ms-usuarios via Eureka para buscar usuarios por email
@FeignClient(name = "ms-usuarios")
public interface UsuarioClient {

    // Llama al endpoint GET /api/usuarios/buscar?email=... que ya hicimos en ms-usuarios
    @GetMapping("/api/usuarios/buscar")
    UsuarioDTO buscarPorEmail(@RequestParam("email") String email);
}