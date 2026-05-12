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

    // Crea una nueva promocion
    public PromocionResponseDTO crearPromocion(Promocion promocion) {
        return construirRespuesta(promocionRepository.save(promocion));
    }

    // Obtiene todas las promociones
    public List<PromocionResponseDTO> obtenerTodas() {
        return promocionRepository.findAll()
                .stream()
                .map(this::construirRespuesta)
                .toList();
    }

    // Obtiene solo las promociones activas
    public List<PromocionResponseDTO> obtenerActivas() {
        return promocionRepository.findByEstado(Promocion.Estado.ACTIVO)
                .stream()
                .map(this::construirRespuesta)
                .toList();
    }

    // Obtiene una promocion por ID
    public Optional<PromocionResponseDTO> obtenerPorId(Long id) {
        return promocionRepository.findById(id).map(this::construirRespuesta);
    }

    // Cambia el estado de una promocion
    public PromocionResponseDTO cambiarEstado(Long id, Promocion.Estado nuevoEstado) {
        Promocion promocion = promocionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Promocion no encontrada con ID: " + id));
        promocion.setEstado(nuevoEstado);
        return construirRespuesta(promocionRepository.save(promocion));
    }

    // Elimina una promocion
    public boolean eliminar(Long id) {
        if (promocionRepository.existsById(id)) {
            promocionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Construye el DTO de respuesta
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