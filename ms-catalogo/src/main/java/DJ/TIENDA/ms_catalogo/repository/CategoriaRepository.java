package DJ.TIENDA.ms_catalogo.repository;

import DJ.TIENDA.ms_catalogo.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    boolean existsByNombreIgnoreCase(String nombre); // valida nombre unico antes de guardar
}
