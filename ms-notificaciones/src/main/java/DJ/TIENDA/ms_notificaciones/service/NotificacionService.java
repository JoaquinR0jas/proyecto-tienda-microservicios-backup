package DJ.TIENDA.ms_notificaciones.service;

import DJ.TIENDA.ms_notificaciones.dto.NotificacionResponseDTO;
import DJ.TIENDA.ms_notificaciones.model.Notificacion;
import DJ.TIENDA.ms_notificaciones.repository.NotificacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    // Al final no mandamos nada real, solo guardamos para que quede el registro
    public NotificacionResponseDTO enviarNotificacion(Long usuarioId, Notificacion.Tipo tipo, String mensaje) {
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuarioId(usuarioId);
        notificacion.setTipo(tipo);
        notificacion.setMensaje(mensaje);

        notificacionRepository.save(notificacion);
        return construirRespuesta(notificacion);
    }

    public List<NotificacionResponseDTO> obtenerPorUsuario(Long usuarioId) {
        return notificacionRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::construirRespuesta)
                .toList();
    }

    // Filtra las que el usuario aun no ha visto
    public List<NotificacionResponseDTO> obtenerNoLeidas(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdAndLeida(usuarioId, false)
                .stream()
                .map(this::construirRespuesta)
                .toList();
    }

    public NotificacionResponseDTO marcarComoLeida(Long notificacionId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new IllegalArgumentException("Notificacion no encontrada con ID: " + notificacionId));
        notificacion.setLeida(true);
        notificacionRepository.save(notificacion);
        return construirRespuesta(notificacion);
    }

    // Arma el DTO con lo que necesita el frontend
    private NotificacionResponseDTO construirRespuesta(Notificacion notificacion) {
        NotificacionResponseDTO respuesta = new NotificacionResponseDTO();
        respuesta.setNotificacionId(notificacion.getId());
        respuesta.setUsuarioId(notificacion.getUsuarioId());
        respuesta.setTipo(notificacion.getTipo().name());
        respuesta.setMensaje(notificacion.getMensaje());
        respuesta.setLeida(notificacion.getLeida());
        respuesta.setFechaCreacion(notificacion.getFechaCreacion());
        return respuesta;
    }
}