package DJ.TIENDA.ms_usuarios.repository;
// ESTE MS CUBRE EL DE 👤 user-service (Usuarios): Responsable del registro de nuevos usuarios y gestión de sus datos personales.

import DJ.TIENDA.ms_usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// JpaRepository nos da CRUD completo gratis: save, findAll, findById, delete, etc.
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    // Spring genera el SQL automáticamente por el nombre del método
    Optional<Usuario> findByEmail(String email);   // SELECT * FROM usuarios WHERE email = ?
    boolean existsByEmail(String email);           // Para validar email único antes de guardar
}