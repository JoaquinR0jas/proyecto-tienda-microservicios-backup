import { listarOfertas } from '../api.js'
import { agregarConAviso } from '../alertas.js'
import Tarjeta from '../components/Tarjeta.jsx'
import Cargando, { useCarga } from '../components/Cargando.jsx'

export default function Ofertas() {
  // ms-catalogo devuelve los productos activos con descuento
  const ofertas = useCarga(listarOfertas)
  const lista = [...(ofertas.datos || [])].sort((a, b) => b.descuento - a.descuento)

  return (
    <section className="container py-5">
      <h1 className="section-title">Ofertas</h1>
      <p className="text-muted">Prendas con descuento por tiempo limitado.</p>
      <Cargando cargando={ofertas.cargando} error={ofertas.error} onReintentar={ofertas.recargar} />
      <div className="row g-4">
        {!ofertas.cargando && !ofertas.error && lista.length === 0 && <p className="text-muted text-center py-5">Por ahora no hay productos en oferta.</p>}
        {lista.map(p => <Tarjeta key={p.codigo} p={p} onAgregar={() => agregarConAviso(p)} />)}
      </div>
    </section>
  )
}
