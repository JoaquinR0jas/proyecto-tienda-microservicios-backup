package DJ.TIENDA.auth_service.controller;

import DJ.TIENDA.auth_service.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // POST /auth/login → Recibe email y password, devuelve el JWT si son correctos
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        try {
            String email = credenciales.get("email");       // Extrae email del JSON
            String password = credenciales.get("password"); // Extrae password del JSON

            String token = authService.login(email, password); // Intenta el login

            // Devuelve el token en un JSON estructurado
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("token", token);
            respuesta.put("tipo", "Bearer");
            return ResponseEntity.ok(respuesta);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage()); // 401
        }
    }

    // GET /auth/validate?token=... → Valida si un token JWT es válido
    @GetMapping("/validate")
    public ResponseEntity<?> validarToken(@RequestParam String token) {
        if (authService.validarToken(token)) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("email", authService.obtenerEmail(token)); // Quién es
            respuesta.put("rol", authService.obtenerRol(token));     // Su rol
            respuesta.put("valido", "true");
            return ResponseEntity.ok(respuesta);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
    }
}