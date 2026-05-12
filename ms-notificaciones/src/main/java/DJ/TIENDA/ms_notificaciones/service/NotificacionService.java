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

    // Crea y guarda una notificacion simulando el envio
    public NotificacionResponseDTO enviarNotificacion(Long usuarioId, Notificacion.Tipo tipo, String mensaje) {
        Notificacion notificacion = new Notificacion();
        notificacion.setUsuarioId(usuarioId);
        notificacion.setTipo(tipo);
        notificacion.setMensaje(mensaje);

        notificacionRepository.save(notificacion);
        return construirRespuesta(notificacion);
    }

    // Obtiene todas las notificaciones de un usuario
    public List<NotificacionResponseDTO> obtenerPorUsuario(Long usuarioId) {
        return notificacionRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::construirRespuesta)
                .toList();
    }

    // Obtiene solo las notificaciones no leidas de un usuario
    public List<NotificacionResponseDTO> obtenerNoLeidas(Long usuarioId) {
        return notificacionRepository.findByUsuarioIdAndLeida(usuarioId, false)
                .stream()
                .map(this::construirRespuesta)
                .toList();
    }

    // Marca una notificacion como leida
    public NotificacionResponseDTO marcarComoLeida(Long notificacionId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
                .orElseThrow(() -> new IllegalArgumentException("Notificacion no encontrada con ID: " + notificacionId));
        notificacion.setLeida(true);
        notificacionRepository.save(notificacion);
        return construirRespuesta(notificacion);
    }

    // Construye el DTO de respuesta
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