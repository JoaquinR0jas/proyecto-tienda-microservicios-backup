package DJ.TIENDA.ms_carrito.repository;

import DJ.TIENDA.ms_carrito.model.CarritoItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoItemRepository extends JpaRepository<CarritoItem, Long> {

    // Busca un item especifico dentro de un carrito por su productoId
    Optional<CarritoItem> findByCarritoIdAndProductoId(Long carritoId, Long productoId);
} 