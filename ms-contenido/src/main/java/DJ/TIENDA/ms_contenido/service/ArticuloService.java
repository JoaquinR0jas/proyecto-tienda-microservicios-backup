package DJ.TIENDA.ms_contenido.service;

import DJ.TIENDA.ms_contenido.model.Articulo;
import DJ.TIENDA.ms_contenido.repository.ArticuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ArticuloService {

    @Autowired
    private ArticuloRepository articuloRepository;

    // La tienda ve solo los publicados, el panel admin ve todos
    public List<Articulo> obtenerArticulos(boolean incluirBorradores) {
        return incluirBorradores
                ? articuloRepository.findAllByOrderByFechaDesc()
                : articuloRepository.findByEstadoOrderByFechaDesc(Articulo.Estado.PUBLICADO);
    }

    public Optional<Articulo> obtenerPorSlug(String slug) {
        return articuloRepository.findBySlug(slug);
    }

    public Articulo crear(Articulo articulo) {
        if (articuloRepository.existsBySlug(articulo.getSlug())) {
            throw new IllegalArgumentException("Ya existe un articulo con el slug: " + articulo.getSlug());
        }
        return articuloRepository.save(articulo);
    }

    public Optional<Articulo> actualizar(Long id, Articulo datos) {
        return articuloRepository.findById(id).map(existente -> {
            if (!existente.getSlug().equals(datos.getSlug()) && articuloRepository.existsBySlug(datos.getSlug())) {
                throw new IllegalArgumentException("Ya existe un articulo con el slug: " + datos.getSlug());
            }
            existente.setSlug(datos.getSlug());
            existente.setTitulo(datos.getTitulo());
            existente.setResumen(datos.getResumen());
            existente.setContenido(datos.getContenido());
            existente.setImagen(datos.getImagen());
            if (datos.getFecha() != null) existente.setFecha(datos.getFecha());
            if (datos.getEstado() != null) existente.setEstado(datos.getEstado());
            return articuloRepository.save(existente);
        });
    }

    public boolean eliminar(Long id) {
        if (articuloRepository.existsById(id)) {
            articuloRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
