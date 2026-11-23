package DJ.TIENDA.ms_usuarios.repository;

import DJ.TIENDA.ms_usuarios.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email); // valida email unico antes de guardar
    boolean existsByRun(String run);     // valida run unico antes de guardar

    // Borra en una sola consulta las filas antiguas incompletas, sin cargarlas
    // en memoria (asi no fallan las validaciones del modelo al eliminarlas)
    @Modifying
    @Transactional
    @Query("delete from Usuario u where u.run is null or length(u.run) < 7 " +
           "or u.apellidos is null or trim(u.apellidos) = '' " +
           "or u.direccion is null or trim(u.direccion) = ''")
    int borrarUsuariosIncompletos();
}
