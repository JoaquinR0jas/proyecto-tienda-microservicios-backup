package DJ.TIENDA.ms_marketing.service;

import DJ.TIENDA.ms_marketing.dto.PromocionResponseDTO;
import DJ.TIENDA.ms_marketing.model.Promocion;
import DJ.TIENDA.ms_marketing.repository.PromocionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MarketingService {

    @Autowired
    private PromocionRepository promocionRepository;

    public PromocionResponseDTO crearPromocion(Promocion promocion) {
        return construirRespuesta(promocionRepository.save(promocion));
    }

    public List<PromocionResponseDTO> obtenerTodas() {
        return promocionRepository.findAll()
                .stream()
                .map(this::construirRespuesta)
                .toList();
    }

    // Solo las que estan vigentes, el resto no interesan
    public List<PromocionResponseDTO> obtenerActivas() {
        return promocionRepository.findByEstado(Promocion.Estado.ACTIVO)
                .stream()
                .map(this::construirRespuesta)
                .toList();
    }

    public Optional<PromocionResponseDTO> obtenerPorId(Long id) {
        return promocionRepository.findById(id).map(this::construirRespuesta);
    }

    // Cambia el estado sin tocar el resto de la promocion
    public PromocionResponseDTO cambiarEstado(Long id, Promocion.Estado nuevoEstado) {
        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promocion no encontrada con ID: " + id));
        promocion.setEstado(nuevoEstado);
        return construirRespuesta(promocionRepository.save(promocion));
    }

    public boolean eliminar(Long id) {
        if (promocionRepository.existsById(id)) {
            promocionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Mapea la entidad a lo que el frontend necesita
    private PromocionResponseDTO construirRespuesta(Promocion promocion) {
        PromocionResponseDTO respuesta = new PromocionResponseDTO();
        respuesta.setPromocionId(promocion.getId());
        respuesta.setNombre(promocion.getNombre());
        respuesta.setDescripcion(promocion.getDescripcion());
        respuesta.setDescuentoPorcentaje(promocion.getDescuentoPorcentaje());
        respuesta.setEstado(promocion.getEstado().name());
        respuesta.setFechaCreacion(promocion.getFechaCreacion());
        respuesta.setFechaExpiracion(promocion.getFechaExpiracion());
        return respuesta;
    }
}