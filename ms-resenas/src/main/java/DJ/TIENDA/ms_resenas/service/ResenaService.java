package DJ.TIENDA.ms_resenas.service;

import DJ.TIENDA.ms_resenas.dto.ResenaResponseDTO;
import DJ.TIENDA.ms_resenas.model.Resena;
import DJ.TIENDA.ms_resenas.repository.ResenaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ResenaService {

    @Autowired
    private ResenaRepository resenaRepository;

    public ResenaResponseDTO crearResena(Resena resena) {
        return construirRespuesta(resenaRepository.save(resena));
    }

    public List<ResenaResponseDTO> obtenerPorProducto(Long productoId) {
        return resenaRepository.findByProductoId(productoId)
                .stream()
                .map(this::construirRespuesta)
                .toList();
    }

    public List<ResenaResponseDTO> obtenerPorUsuario(Long usuarioId) {
        return resenaRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::construirRespuesta)
                .toList();
    }

    public Double obtenerPromedio(Long productoId) {
        Double promedio = resenaRepository.promedioByProductoId(productoId);
        return promedio != null ? Math.round(promedio * 10.0) / 10.0 : 0.0; // Redondea a 1 decimal
    }

    public boolean eliminar(Long id) {
        if (resenaRepository.existsById(id)) {
            resenaRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<ResenaResponseDTO> obtenerPorId(Long id) {
        return resenaRepository.findById(id).map(this::construirRespuesta);
    }

    // arma la respuesta sin exponer el objeto completo de la BD
    private ResenaResponseDTO construirRespuesta(Resena resena) {
        ResenaResponseDTO respuesta = new ResenaResponseDTO();
        respuesta.setResenaId(resena.getId());
        respuesta.setProductoId(resena.getProductoId());
        respuesta.setUsuarioId(resena.getUsuarioId());
        respuesta.setPuntuacion(resena.getPuntuacion());
        respuesta.setComentario(resena.getComentario());
        respuesta.setFechaCreacion(resena.getFechaCreacion());
        return respuesta;
    }
}