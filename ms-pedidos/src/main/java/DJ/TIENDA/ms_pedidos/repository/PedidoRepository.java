package DJ.TIENDA.ms_pedidos.repository;

import DJ.TIENDA.ms_pedidos.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    List<Pedido> findByUsuarioIdOrderByFechaCreacionDesc(Long usuarioId); // historial de compras

    List<Pedido> findAllByOrderByFechaCreacionDesc(); // ordenes del panel admin, las mas nuevas primero
}
