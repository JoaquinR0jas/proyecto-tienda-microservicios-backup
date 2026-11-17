package DJ.TIENDA.ms_contenido.repository;

import DJ.TIENDA.ms_contenido.model.Articulo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArticuloRepository extends JpaRepository<Articulo, Long> {

    Optional<Articulo> findBySlug(String slug);

    boolean existsBySlug(String slug); // valida slug unico antes de guardar

    List<Articulo> findAllByOrderByFechaDesc();

    List<Articulo> findByEstadoOrderByFechaDesc(Articulo.Estado estado);
}
