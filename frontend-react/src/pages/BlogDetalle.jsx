import { Link, useParams } from 'react-router-dom'
import { Button, Container } from 'react-bootstrap'
import { imgProducto } from '../datos.js'
import { obtenerArticulo } from '../api.js'
import Cargando, { useCarga } from '../components/Cargando.jsx'

const MESES = ["enero", "febrero", "marzo", "abril", "mayo", "junio", "julio", "agosto",
  "septiembre", "octubre", "noviembre", "diciembre"]

// "2026-08-20" -> "20 de agosto, 2026"
function fechaLarga(fecha) {
  const [anio, mes, dia] = String(fecha).split("-")
  if (!anio || !mes || !dia) return fecha
  return dia + " de " + MESES[Number(mes) - 1] + ", " + anio
}

// El contenido viene como texto: los parrafos se separan con una linea en blanco
// y las lineas que empiezan con "1.", "2."... forman una lista numerada
function Contenido({ texto }) {
  return String(texto).split(/\n\s*\n/).map((bloque, i) => {
    const lineas = bloque.split("\n").filter(l => l.trim())
    if (lineas.length && lineas.every(l => /^\d+\.\s/.test(l.trim()))) {
      return <ol key={i}>{lineas.map((l, j) => <li key={j}>{l.trim().replace(/^\d+\.\s/, "")}</li>)}</ol>
    }
    return <p key={i}>{bloque}</p>
  })
}

export default function BlogDetalle() {
  const { slug } = useParams()
  const carga = useCarga(() => obtenerArticulo(slug), [slug])
  const n = carga.datos

  if (carga.cargando || carga.error) {
    return (
      <section className="container py-5">
        <Cargando cargando={carga.cargando} error={carga.error} onReintentar={carga.recargar} />
      </section>
    )
  }

  if (!n || n.estado === "BORRADOR") {
    return (
      <Container className="py-5 text-center">
        <p className="text-muted">Noticia no encontrada.</p>
        <Button as={Link} to="/blog" variant="accent">Volver al blog</Button>
      </Container>
    )
  }

  return (
    <article className="container py-5" style={{ maxWidth: 800 }}>
      <img src={imgProducto(n)} className="img-fluid rounded mb-4" alt={n.titulo} />
      <p className="text-muted small mb-1">{fechaLarga(n.fecha)}</p>
      <h1 className="mb-4">{n.titulo}</h1>
      <Contenido texto={n.contenido} />
      <Link to="/blog" className="btn btn-accent mt-3"><i className="bi bi-arrow-left"></i> Volver al Blog</Link>
    </article>
  )
}
