package DJ.TIENDA.ms_catalogo.service;

import DJ.TIENDA.ms_catalogo.model.Categoria;
import DJ.TIENDA.ms_catalogo.repository.CategoriaRepository;
import DJ.TIENDA.ms_catalogo.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaService {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public List<Categoria> obtenerTodas() {
        return categoriaRepository.findAll();
    }

    public Optional<Categoria> obtenerPorId(Long id) {
        return categoriaRepository.findById(id);
    }

    public Categoria crear(Categoria categoria) {
        if (categoriaRepository.existsByNombreIgnoreCase(categoria.getNombre().trim())) {
            throw new IllegalArgumentException("Ya existe una categoria con el nombre: " + categoria.getNombre());
        }
        categoria.setNombre(categoria.getNombre().trim());
        return categoriaRepository.save(categoria);
    }

    public Optional<Categoria> actualizar(Long id, Categoria datos) {
        return categoriaRepository.findById(id).map(existente -> {
            String nombre = datos.getNombre().trim();
            if (!existente.getNombre().equalsIgnoreCase(nombre) && categoriaRepository.existsByNombreIgnoreCase(nombre)) {
                throw new IllegalArgumentException("Ya existe una categoria con el nombre: " + nombre);
            }
            existente.setNombre(nombre);
            existente.setDescripcion(datos.getDescripcion());
            return categoriaRepository.save(existente);
        });
    }

    // No se elimina si tiene productos, igual que en el panel admin del frontend
    public boolean eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            return false;
        }
        if (productoRepository.existsByCategoriaId(id)) {
            throw new IllegalStateException("No se puede eliminar, la categoria tiene productos");
        }
        categoriaRepository.deleteById(id);
        return true;
    }
}
