package DJ.TIENDA.ms_envios.repository;

import DJ.TIENDA.ms_envios.model.Envio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnvioRepository extends JpaRepository<Envio, Long> {

    // Busca el envio de un pedido especifico
    Optional<Envio> findByPedidoId(Long pedidoId);

    // Trae todos los envios de un usuario
    List<Envio> findByUsuarioId(Long usuarioId);
}