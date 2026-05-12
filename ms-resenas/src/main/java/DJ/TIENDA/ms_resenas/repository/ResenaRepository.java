package DJ.TIENDA.ms_resenas.repository;

import DJ.TIENDA.ms_resenas.model.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResenaRepository extends JpaRepository<Resena, Long> {

    // Trae todas las resenas de un producto
    List<Resena> findByProductoId(Long productoId);

    // Trae todas las resenas de un usuario
    List<Resena> findByUsuarioId(Long usuarioId);

    // Calcula el promedio de puntuacion de un producto
    @Query("SELECT AVG(r.puntuacion) FROM Resena r WHERE r.productoId = :productoId")
    Double promedioByProductoId(Long productoId);
}