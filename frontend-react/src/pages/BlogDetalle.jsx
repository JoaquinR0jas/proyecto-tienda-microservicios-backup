import { Link, useParams } from 'react-router-dom'
import { KEYS, getColeccion } from '../datos.js'

// contenido de cada noticia, el mismo que tenian las paginas blog-detalle del frontend original
const CONTENIDO = {
  "detalle-1": {
    fecha: "01 de agosto, 2026",
    cuerpo: (
      <>
        <p>
          Despues de meses de trabajo, hoy nace <strong>FreshFits</strong>, una nueva tienda online
          pensada para quienes quieren vestir bien sin complicarse. Nuestro objetivo es simple: ofrecer
          un catalogo variado de ropa moderna, con una experiencia de compra rapida, clara y sin problemas.
        </p>
        <p>
          Desde el primer dia contaras con un catalogo organizado por categorias, fichas de producto
          con toda la informacion que necesitas, y un carrito de compras que recuerda tus prendas
          aunque cierres el navegador.
        </p>
        <p>
          Este es solo el comienzo. En las proximas semanas seguiremos sumando prendas, promociones
          y contenido para toda la comunidad. Gracias por ser parte de este lanzamiento.
        </p>
      </>
    )
  },
  "detalle-2": {
    fecha: "10 de agosto, 2026",
    cuerpo: (
      <>
        <p>
          La nueva temporada viene cargada de <strong>novedades</strong>. Desde polerones oversized
          hasta jeans con cortes renovados, repasamos las prendas que marcaran tendencia en los
          proximos meses.
        </p>
        <p>
          En FreshFits ya estamos preparando el stock con los colores y estilos que mas se van a usar:
          tonos tierra, denim lavado y poleras con graficos retro para todos los gustos.
        </p>
        <p>
          Mantente atento a nuestro catalogo, porque cada semana llegan estrenos nuevos con precios
          especiales de lanzamiento.
        </p>
      </>
    )
  },
  "detalle-3": {
    fecha: "20 de agosto, 2026",
    cuerpo: (
      <>
        <p>
          Armar un outfit <strong>streetwear</strong> no requiere gastar de mas. Con estas cinco ideas
          simples logras un look fresh, comodo y con identidad para el dia a dia.
        </p>
        <ol>
          <li>Combina una prenda oversize (poleron o polera) con otra mas ajustada para equilibrar la silueta.</li>
          <li>Usa colores neutros como base y agrega un solo acento de color o grafico.</li>
          <li>Las zapatillas urbanas son el complemento clave: ligeras, comodas y con estilo.</li>
          <li>Invierte en basicos de buena tela: una polera 100% algodon va con todo.</li>
          <li>No descuides los accesorios: una gorra o un buzo cargo pueden cambiar todo el look.</li>
        </ol>
        <p>
          Lo importante es sentirte comodo con lo que usas. Prueba combinaciones, revisa nuestro
          catalogo y arma tu propio estilo fresh.
        </p>
      </>
    )
  }
}

export default function BlogDetalle() {
  const { slug } = useParams()
  const n = getColeccion(KEYS.blog).find(x => x.slug === slug)
  const contenido = CONTENIDO[slug]

  if (!n) {
    return (
      <section className="container py-5 text-center">
        <p className="text-muted">Noticia no encontrada.</p>
        <Link to="/blog" className="btn btn-accent">Volver al blog</Link>
      </section>
    )
  }

  return (
    <article className="container py-5" style={{ maxWidth: 800 }}>
      <img src={"/img/" + n.imagen} className="img-fluid rounded mb-4" alt={n.titulo} />
      <p className="text-muted small mb-1">{contenido ? contenido.fecha : n.fecha}</p>
      <h1 className="mb-4">{n.titulo}</h1>
      {contenido ? contenido.cuerpo : <p>{n.resumen}</p>}
      <Link to="/blog" className="btn btn-accent mt-3"><i className="bi bi-arrow-left"></i> Volver al Blog</Link>
    </article>
  )
}
