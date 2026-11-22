package DJ.TIENDA.ms_usuarios.service;

import DJ.TIENDA.ms_usuarios.model.Usuario;
import DJ.TIENDA.ms_usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // BCrypt: convierte la clave en un hash

    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Usuario crear(Usuario usuario) {
        usuario.setRun(normalizarRun(usuario.getRun()));
        if (usuarioRepository.existsByRun(usuario.getRun())) {
            throw new IllegalArgumentException("Ya existe un usuario con el RUN: " + usuario.getRun());
        }
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + usuario.getEmail());
        }
        // Guardamos la clave encriptada, nunca en texto plano
        usuario.setPassword(encriptar(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> actualizar(Long id, Usuario datosActualizados) {
        // el RUN no se cambia, es el identificador de la persona
        return usuarioRepository.findById(id).map(existente -> {
            if (!existente.getEmail().equals(datosActualizados.getEmail())) {
                if (usuarioRepository.existsByEmail(datosActualizados.getEmail())) {
                    throw new IllegalArgumentException("El email ya está en uso: " + datosActualizados.getEmail());
                }
            }
            existente.setNombre(datosActualizados.getNombre());
            existente.setApellidos(datosActualizados.getApellidos());
            existente.setEmail(datosActualizados.getEmail());

            // Si el formulario viene sin clave, se mantiene la que ya estaba.
            // Si viene con una clave nueva, se encripta antes de guardarla.
            String nuevaClave = encriptar(datosActualizados.getPassword());
            if (nuevaClave != null) {
                existente.setPassword(nuevaClave);
            }

            existente.setFechaNacimiento(datosActualizados.getFechaNacimiento());
            existente.setRegionId(datosActualizados.getRegionId());
            existente.setComunaId(datosActualizados.getComunaId());
            existente.setDireccion(datosActualizados.getDireccion());
            // el administrador asigna el rol y el estado desde el panel
            if (datosActualizados.getRol() != null) existente.setRol(datosActualizados.getRol());
            if (datosActualizados.getEstado() != null) existente.setEstado(datosActualizados.getEstado());
            return usuarioRepository.save(existente);
        });
    }

    public boolean eliminar(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Encripta una clave con BCrypt. Si viene vacia devuelve null (para no borrar la actual)
    // y si ya viene encriptada la deja igual.
    private String encriptar(String clave) {
        if (clave == null || clave.isBlank()) {
            return null;
        }
        if (clave.startsWith("$2a$") || clave.startsWith("$2b$") || clave.startsWith("$2y$")) {
            return clave;
        }
        return passwordEncoder.encode(clave);
    }

    // el RUN se guarda sin puntos ni guion y con la K en mayuscula, igual que en el frontend
    private String normalizarRun(String run) {
        return run == null ? null : run.toUpperCase().replaceAll("[^0-9K]", "");
    }
}
