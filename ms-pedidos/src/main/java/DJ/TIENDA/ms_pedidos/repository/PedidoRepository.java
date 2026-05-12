package DJ.TIENDA.ms_pedidos.repository;

import DJ.TIENDA.ms_pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // Trae todos los pedidos de un usuario especifico
    List<Pedido> findByUsuarioId(Long usuarioId);
}