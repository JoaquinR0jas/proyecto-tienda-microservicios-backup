package DJ.TIENDA.ms_inventario.repository;

import DJ.TIENDA.ms_inventario.model.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    
    // aqui basicamente estamos pregintandole si tiene ese producto mediante un from * where
    Optional<Inventario> findByProductoId(Long productoId);
}   