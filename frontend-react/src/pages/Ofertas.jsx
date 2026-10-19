import { KEYS, getColeccion, enOferta } from '../datos.js'
import { agregarConAviso } from '../alertas.js'
import Tarjeta from '../components/Tarjeta.jsx'

export default function Ofertas() {
  const lista = getColeccion(KEYS.productos)
    .filter(p => p.estado === "Activo" && enOferta(p))
    .sort((a, b) => b.descuento - a.descuento)

  return (
    <section className="container py-5">
      <h1 className="section-title">Ofertas</h1>
      <p className="text-muted">Prendas con descuento por tiempo limitado.</p>
      <div className="row g-4">
        {lista.length === 0 && <p className="text-muted text-center py-5">Por ahora no hay productos en oferta.</p>}
        {lista.map(p => <Tarjeta key={p.codigo} p={p} onAgregar={c => agregarConAviso(c)} />)}
      </div>
    </section>
  )
}
