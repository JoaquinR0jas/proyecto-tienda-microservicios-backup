package DJ.TIENDA.ms_usuarios.service;
// ESTE MS CUBRE EL DE 👤 user-service (Usuarios): Responsable del registro de nuevos usuarios y gestión de sus datos personales.
// y tambien el security service, ya que dimos un rol y el profe pidio eso, ya tenemos gestion de roles

import DJ.TIENDA.ms_usuarios.model.Usuario;
import DJ.TIENDA.ms_usuarios.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service // Marca esta clase como lógica de negocio (capa intermedia entre Controller y Repository)
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Retorna todos los usuarios de la BD
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    // Busca por ID, retorna Optional (puede no existir)
    public Optional<Usuario> obtenerPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    // Busca por email, retorna Optional
    public Optional<Usuario> obtenerPorEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    // Crea un usuario nuevo, lanzando excepción si el email ya existe
    public Usuario crear(Usuario usuario) {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + usuario.getEmail());
        }
        return usuarioRepository.save(usuario);
    }

    // Actualiza un usuario existente; si cambia email, verifica que no esté en uso
    public Optional<Usuario> actualizar(Long id, Usuario datosActualizados) {
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

    // Elimina por ID, retorna true si existía y fue eliminado
    public boolean eliminar(Long id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }
}