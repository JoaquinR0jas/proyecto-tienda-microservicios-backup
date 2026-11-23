package DJ.TIENDA.ms_catalogo.config;

import DJ.TIENDA.ms_catalogo.model.Categoria;
import DJ.TIENDA.ms_catalogo.model.Producto;
import DJ.TIENDA.ms_catalogo.repository.CategoriaRepository;
import DJ.TIENDA.ms_catalogo.repository.ProductoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

// Carga las mismas categorias y productos que tenia el frontend en localStorage,
// solo la primera vez (cuando las tablas estan vacias)
@Component
public class DatosIniciales implements CommandLineRunner {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;

    public DatosIniciales(CategoriaRepository categoriaRepository, ProductoRepository productoRepository) {
        this.categoriaRepository = categoriaRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public void run(String... args) {
        if (categoriaRepository.count() == 0) {
            categoria("Poleras", "Poleras basicas y estampadas para todos los dias.");
            categoria("Polerones", "Polerones oversized y con capucha.");
            categoria("Jeans", "Jeans y pantalones de distintos cortes.");
            categoria("Camisas", "Camisas frescas para cualquier ocasion.");
            categoria("Zapatillas", "Zapatillas urbanas y deportivas.");
            categoria("Accesorios", "Gorras y complementos para tu look.");
        }

        // Si quedaron productos de una version antigua (sin codigo ni imagen),
        // se borran para que se vuelvan a crear los de prueba completos.
        productoRepository.borrarProductosSinCodigo();

        if (productoRepository.count() == 0) {
            producto("FF-001", "Polera Fresh Basica", "Polera de algodon 100%, corte relajado y comodo para todos los dias.", 14990, 0, 15, 3, 1L, "productos/polera-basica.webp", true);
            producto("FF-002", "Poleron Cloud Hoodie", "Poleron oversized con capucha, bolsillo canguro y tela afelpada.", 29990, 20, 20, 5, 2L, "productos/polerones.png", true);
            producto("FF-003", "Jean Slim Fit", "Jean de corte slim, denim lavado medio, elastico y muy comodo.", 24990, 0, 8, 2, 3L, "productos/pantalones.png", true);
            producto("FF-004", "Camisa Lino Flow", "Camisa manga corta de lino, fresca y perfecta para el verano.", 21990, 0, 12, 4, 4L, "productos/camisas.png", true);
            producto("FF-005", "Polera Retro Graphic", "Polera con grafico retro, estilo vintage y tela premium.", 17990, 15, 10, 3, 1L, "productos/poleraretro.webp", false);
            producto("FF-006", "Zapatillas Urban Run", "Zapatillas urbanas ligeras, ideales para tu look del dia a dia.", 39990, 0, 25, 5, 5L, "productos/zaptilla.png", true);
            producto("FF-007", "Cargo Street Pants", "Pantalon cargo estilo militar, con bolsillos y corte streetwear.", 27990, 30, 2, 3, 3L, "productos/pantalonescargo.jpg", false);
            producto("FF-008", "Gorra Fresh Cap", "Gorra con logo bordado, ajuste regulable y tela transpirable.", 12990, 10, 6, 2, 6L, "productos/gorrofresh.jpg", false);
        }
    }

    private void categoria(String nombre, String descripcion) {
        Categoria categoria = new Categoria();
        categoria.setNombre(nombre);
        categoria.setDescripcion(descripcion);
        categoriaRepository.save(categoria);
    }

    private void producto(String codigo, String nombre, String descripcion, double precio, int descuento,
                          int stock, int stockCritico, Long categoriaId, String imagen, boolean destacado) {
        Producto producto = new Producto();
        producto.setCodigo(codigo);
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setDescuento(descuento);
        producto.setStock(stock);
        producto.setStockCritico(stockCritico);
        producto.setCategoriaId(categoriaId);
        producto.setImagen(imagen);
        producto.setDestacado(destacado);
        productoRepository.save(producto);
    }
}
