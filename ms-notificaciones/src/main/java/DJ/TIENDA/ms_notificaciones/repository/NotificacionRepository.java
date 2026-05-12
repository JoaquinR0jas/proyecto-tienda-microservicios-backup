package DJ.TIENDA.ms_notificaciones.repository;

import DJ.TIENDA.ms_notificaciones.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    // Trae todas las notificaciones de un usuario
    List<Notificacion> findByUsuarioId(Long usuarioId);

    // Trae solo las notificaciones no leidas de un usuario
    List<Notificacion> findByUsuarioIdAndLeida(Long usuarioId, Boolean leida);
}