package DJ.TIENDA.ms_usuarios.service;

import DJ.TIENDA.ms_usuarios.model.Usuario;
import DJ.TIENDA.ms_usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

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
            existente.setPassword(datosActualizados.getPassword());
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

    // el RUN se guarda sin puntos ni guion y con la K en mayuscula, igual que en el frontend
    private String normalizarRun(String run) {
        return run == null ? null : run.toUpperCase().replaceAll("[^0-9K]", "");
    }
}
