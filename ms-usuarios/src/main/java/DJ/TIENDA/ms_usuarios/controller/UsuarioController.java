package DJ.TIENDA.ms_usuarios.controller;

import DJ.TIENDA.ms_usuarios.dto.UsuarioLoginDTO;
import DJ.TIENDA.ms_usuarios.model.Usuario;
import DJ.TIENDA.ms_usuarios.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Usuarios", description = "Gestion de usuarios del sistema")
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/v1/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Operation(summary = "Listar todos los usuarios", description = "Retorna una lista con todos los usuarios registrados")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida correctamente")
    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() {
        return ResponseEntity.ok(usuarioService.obtenerTodos());
    }

    @Operation(summary = "Datos de login por email", description = "Uso interno de auth-service. Devuelve el hash de la clave para comparar en el login")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado con ese email")
    })
    @GetMapping("/auth")
    public ResponseEntity<?> datosLogin(@RequestParam String email) {
        return usuarioService.obtenerPorEmail(email)
                .<ResponseEntity<?>>map(u -> {
                    UsuarioLoginDTO dto = new UsuarioLoginDTO();
                    dto.setId(u.getId());
                    dto.setRun(u.getRun());
                    dto.setNombre(u.getNombre());
                    dto.setApellidos(u.getApellidos());
                    dto.setEmail(u.getEmail());
                    dto.setPassword(u.getPassword());
                    dto.setRol(u.getRol().name());
                    dto.setEstado(u.getEstado().name());
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado con email: " + email));
    }

    @Operation(summary = "Obtener usuario por ID", description = "Busca un usuario por su ID unico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "403", description = "Un cliente solo puede ver su propio perfil"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id,
                                          @RequestHeader(value = "X-Usuario-Id", required = false) Long idSesion,
                                          @RequestHeader(value = "X-Usuario-Rol", required = false) String rolSesion) {
        // Un cliente solo puede ver sus propios datos. El admin ve a todos.
        if (!"ADMIN".equals(rolSesion) && (idSesion == null || !idSesion.equals(id))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No tienes permiso para ver este usuario");
        }
        return usuarioService.obtenerPorId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado con ID: " + id));
    }

    @Operation(summary = "Buscar usuario por email", description = "Busca un usuario utilizando su correo electronico")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado con ese email")
    })
    @GetMapping("/buscar")
    public ResponseEntity<?> obtenerPorEmail(@RequestParam String email) {
        return usuarioService.obtenerPorEmail(email)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Usuario no encontrado con email: " + email));
    }

    @Operation(summary = "Crear un nuevo usuario", description = "Registra un nuevo usuario en el sistema")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente"),
        @ApiResponse(responseCode = "409", description = "El email ya esta registrado")
    })
    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody Usuario usuario) {
        usuario.setId(null);
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(usuario));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "409", description = "El email ya esta en uso por otro usuario")
    })
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        try {
            return usuarioService.actualizar(id, usuario)
                    .<ResponseEntity<?>>map(ResponseEntity::ok)
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body("Usuario no encontrado con ID: " + id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema por su ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Usuario eliminado correctamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        if (usuarioService.eliminar(id)) {
            return ResponseEntity.ok("Usuario eliminado correctamente.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Usuario no encontrado con ID: " + id);
    }
}