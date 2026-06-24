package DJ.TIENDA.auth_service.controller;

import DJ.TIENDA.auth_service.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Autenticacion", description = "Autenticacion y validacion de tokens JWT")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "Iniciar sesion", description = "Autentica un usuario con email y password, devuelve un token JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login exitoso, token JWT generado"),
        @ApiResponse(responseCode = "401", description = "Credenciales invalidas")
    })
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        try {
            String email = credenciales.get("email");
            String password = credenciales.get("password");

            String token = authService.login(email, password);

            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("token", token);
            respuesta.put("tipo", "Bearer");
            return ResponseEntity.ok(respuesta);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @Operation(summary = "Validar token JWT", description = "Verifica si un token JWT es valido y devuelve email y rol asociados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token valido, retorna email y rol"),
        @ApiResponse(responseCode = "401", description = "Token invalido o expirado")
    })
    @GetMapping("/validate")
    public ResponseEntity<?> validarToken(@RequestParam String token) {
        if (authService.validarToken(token)) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("email", authService.obtenerEmail(token));
            respuesta.put("rol", authService.obtenerRol(token));
            respuesta.put("valido", "true");
            return ResponseEntity.ok(respuesta);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token invalido o expirado");
    }
}