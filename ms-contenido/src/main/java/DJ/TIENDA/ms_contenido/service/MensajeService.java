package DJ.TIENDA.ms_contenido.service;

import DJ.TIENDA.ms_contenido.model.Mensaje;
import DJ.TIENDA.ms_contenido.repository.MensajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MensajeService {

    @Autowired
    private MensajeRepository mensajeRepository;

    public List<Mensaje> obtenerTodos() {
        return mensajeRepository.findAllByOrderByFechaDesc();
    }

    public Mensaje crear(Mensaje mensaje) {
        mensaje.setNombre(mensaje.getNombre().trim());
        mensaje.setEmail(mensaje.getEmail().trim());
        mensaje.setComentario(mensaje.getComentario().trim());
        return mensajeRepository.save(mensaje);
    }

    public boolean eliminar(Long id) {
        if (mensajeRepository.existsById(id)) {
            mensajeRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
