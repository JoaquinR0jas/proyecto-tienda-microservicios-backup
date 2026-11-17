package DJ.TIENDA.ms_contenido.config;

import DJ.TIENDA.ms_contenido.model.Articulo;
import DJ.TIENDA.ms_contenido.repository.ArticuloRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

// Carga los 3 articulos que tenia el blog del frontend, solo la primera vez (tabla vacia)
@Component
public class DatosIniciales implements CommandLineRunner {

    private final ArticuloRepository articuloRepository;

    public DatosIniciales(ArticuloRepository articuloRepository) {
        this.articuloRepository = articuloRepository;
    }

    @Override
    public void run(String... args) {
        if (articuloRepository.count() > 0) return;

        articulo("detalle-1", "FreshFits abre sus puertas",
                "Nace una nueva tienda online de ropa moderna pensada para todos.",
                "blog/blooms.png", "2026-08-01",
                "Despues de meses de trabajo, hoy nace FreshFits, una nueva tienda online pensada para quienes quieren "
                + "vestir bien sin complicarse. Nuestro objetivo es simple: ofrecer un catalogo variado de ropa moderna, "
                + "con una experiencia de compra rapida, clara y sin problemas.\n\n"
                + "Desde el primer dia contaras con un catalogo organizado por categorias, fichas de producto con toda "
                + "la informacion que necesitas, y un carrito de compras que recuerda tus prendas aunque cierres el navegador.\n\n"
                + "Este es solo el comienzo. En las proximas semanas seguiremos sumando prendas, promociones y contenido "
                + "para toda la comunidad. Gracias por ser parte de este lanzamiento.");

        articulo("detalle-2", "Los lanzamientos mas esperados de la temporada",
                "Repasamos las prendas que marcaran tendencia en los proximos meses.",
                "blog/lanzamientoesperados.png", "2026-08-10",
                "La nueva temporada viene cargada de novedades. Desde polerones oversized hasta jeans con cortes "
                + "renovados, repasamos las prendas que marcaran tendencia en los proximos meses.\n\n"
                + "En FreshFits ya estamos preparando el stock con los colores y estilos que mas se van a usar: tonos "
                + "tierra, denim lavado y poleras con graficos retro para todos los gustos.\n\n"
                + "Mantente atento a nuestro catalogo, porque cada semana llegan estrenos nuevos con precios especiales "
                + "de lanzamiento.");

        articulo("detalle-3", "5 consejos para un look streetwear",
                "Ideas simples para armar outfits fresh sin complicaciones.",
                "blog/5consejos.png", "2026-08-20",
                "Armar un outfit streetwear no requiere gastar de mas. Con estas cinco ideas simples logras un look "
                + "fresh, comodo y con identidad para el dia a dia.\n\n"
                + "1. Combina una prenda oversize (poleron o polera) con otra mas ajustada para equilibrar la silueta.\n"
                + "2. Usa colores neutros como base y agrega un solo acento de color o grafico.\n"
                + "3. Las zapatillas urbanas son el complemento clave: ligeras, comodas y con estilo.\n"
                + "4. Invierte en basicos de buena tela: una polera 100% algodon va con todo.\n"
                + "5. No descuides los accesorios: una gorra o un buzo cargo pueden cambiar todo el look.\n\n"
                + "Lo importante es sentirte comodo con lo que usas. Prueba combinaciones, revisa nuestro catalogo y "
                + "arma tu propio estilo fresh.");
    }

    private void articulo(String slug, String titulo, String resumen, String imagen, String fecha, String contenido) {
        Articulo articulo = new Articulo();
        articulo.setSlug(slug);
        articulo.setTitulo(titulo);
        articulo.setResumen(resumen);
        articulo.setImagen(imagen);
        articulo.setFecha(LocalDate.parse(fecha));
        articulo.setContenido(contenido);
        articuloRepository.save(articulo);
    }
}
