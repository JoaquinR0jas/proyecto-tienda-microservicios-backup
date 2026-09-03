package DJ.TIENDA.ms_resenas.repository;

import DJ.TIENDA.ms_resenas.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResenaRepository extends JpaRepository<Resena, Long> {

    List<Resena> findByProductoId(Long productoId);

    List<Resena> findByUsuarioId(Long usuarioId);

    // promedio de puntuacion de un producto (aunque sea 1 solo puede haber varias)
    @Query("SELECT AVG(r.puntuacion) FROM Resena r WHERE r.productoId = :productoId")
    Double promedioByProductoId(Long productoId);
}