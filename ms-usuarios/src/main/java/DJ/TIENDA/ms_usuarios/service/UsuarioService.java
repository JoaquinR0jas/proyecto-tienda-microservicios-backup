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
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + usuario.getEmail());
        }
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> actualizar(Long id, Usuario datosActualizados) {
        // cambio solo los campos que el usuario puede editar, el rol no se toca
        return usuarioRepository.findById(id).map(existente -> {
            if (!existente.getEmail().equals(datosActualizados.getEmail())) {
                if (usuarioRepository.existsByEmail(datosActualizados.getEmail())) {
                    throw new IllegalArgumentException("El email ya está en uso: " + datosActualizados.getEmail());
                }
            }
            existente.setNombre(datosActualizados.getNombre());
            existente.setEmail(datosActualizados.getEmail());
            existente.setPassword(datosActualizados.getPassword());
            existente.setTelefono(datosActualizados.getTelefono());
            existente.setDireccion(datosActualizados.getDireccion());
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
}