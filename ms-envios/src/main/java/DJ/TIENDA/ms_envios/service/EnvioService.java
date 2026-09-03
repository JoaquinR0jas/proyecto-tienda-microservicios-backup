package DJ.TIENDA.ms_envios.service;

import DJ.TIENDA.ms_envios.client.UsuarioClient;
import DJ.TIENDA.ms_envios.dto.EnvioResponseDTO;
import DJ.TIENDA.ms_envios.dto.UsuarioDTO;
import DJ.TIENDA.ms_envios.model.Envio;
import DJ.TIENDA.ms_envios.repository.EnvioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EnvioService {

    @Autowired
    private EnvioRepository envioRepository;

    @Autowired
    private UsuarioClient usuarioClient; // Para obtener direccion del usuario

    public EnvioResponseDTO crearEnvio(Long pedidoId, Long usuarioId) {
        // Evita que se cree un segundo envio para el mismo pedido
        if (envioRepository.findByPedidoId(pedidoId).isPresent()) {
            throw new IllegalArgumentException("Ya existe un envio para el pedido ID: " + pedidoId);
        }

        UsuarioDTO usuario = usuarioClient.obtenerUsuario(usuarioId);
        if (usuario == null || usuario.getDireccion() == null) {
            throw new IllegalArgumentException("El usuario no tiene direccion registrada.");
        }

        Envio envio = new Envio();
        envio.setPedidoId(pedidoId);
        envio.setUsuarioId(usuarioId);
        envio.setDireccionEntrega(usuario.getDireccion());

        envioRepository.save(envio);
        return construirRespuesta(envio);
    }

    public EnvioResponseDTO actualizarEstado(Long envioId, Envio.Estado nuevoEstado) {
        Envio envio = envioRepository.findById(envioId)
                .orElseThrow(() -> new IllegalArgumentException("Envio no encontrado con ID: " + envioId));

        envio.setEstado(nuevoEstado);

        // Si se marca ENTREGADO guardamos la fecha, sino queda null y despues no se puede reclamar
        if (nuevoEstado == Envio.Estado.ENTREGADO) {
            envio.setFechaEntrega(LocalDateTime.now());
        }

        envioRepository.save(envio);
        return construirRespuesta(envio);
    }

    public Optional<EnvioResponseDTO> obtenerPorPedido(Long pedidoId) {
        return envioRepository.findByPedidoId(pedidoId).map(this::construirRespuesta);
    }

    public List<EnvioResponseDTO> obtenerPorUsuario(Long usuarioId) {
        return envioRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(this::construirRespuesta)
                .toList();
    }

    // Arma el DTO con los campos que va a usar el frontend
    private EnvioResponseDTO construirRespuesta(Envio envio) {
        EnvioResponseDTO respuesta = new EnvioResponseDTO();
        respuesta.setEnvioId(envio.getId());
        respuesta.setPedidoId(envio.getPedidoId());
        respuesta.setUsuarioId(envio.getUsuarioId());
        respuesta.setDireccionEntrega(envio.getDireccionEntrega());
        respuesta.setEstado(envio.getEstado().name());
        respuesta.setFechaCreacion(envio.getFechaCreacion());
        respuesta.setFechaEntrega(envio.getFechaEntrega());
        return respuesta;
    }
}