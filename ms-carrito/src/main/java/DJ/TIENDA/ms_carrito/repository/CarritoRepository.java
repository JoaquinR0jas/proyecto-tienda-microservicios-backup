package DJ.TIENDA.ms_carrito.repository;

import DJ.TIENDA.ms_carrito.model.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoRepository extends JpaRepository<Carrito, Long> {

    // Busca el carrito ACTIVO de un usuario especifico
    Optional<Carrito> findByUsuarioIdAndEstado(Long usuarioId, Carrito.Estado estado);
}