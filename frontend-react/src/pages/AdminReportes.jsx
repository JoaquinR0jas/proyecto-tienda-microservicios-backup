import { KEYS, getColeccion, plata, nombreRegion } from '../datos.js'
import { AdminLayout, useAdmin } from '../components/AdminMenu.jsx'
import Barra from '../components/Barra.jsx'

function agrupar(lista, clave, valor) {
  const r = {}
  lista.forEach(x => {
    const k = clave(x)
    r[k] = (r[k] || 0) + valor(x)
  })
  return Object.entries(r)
}

export default function AdminReportes() {
  const sesion = useAdmin(true)
  if (!sesion) return null

  const ordenes = getColeccion(KEYS.ordenes)
  const pagadas = ordenes.filter(o => o.estado === "Pagada")
  const rechazadas = ordenes.length - pagadas.length
  const ventas = pagadas.reduce((s, o) => s + o.total, 0)
  const ticket = pagadas.length ? Math.round(ventas / pagadas.length) : 0
  const unidades = pagadas.reduce((s, o) => s + o.items.reduce((a, i) => a + i.cantidad, 0), 0)

  const porMes = agrupar(pagadas, o => o.fecha.slice(0, 7), o => o.total).sort()
  const porRegion = agrupar(pagadas.filter(o => o.envio.regionId), o => nombreRegion(o.envio.regionId), o => o.total)
  const porEntrega = agrupar(pagadas, o => o.envio.tipo, () => 1)
  const porCliente = agrupar(pagadas, o => o.cliente.nombre + " " + o.cliente.apellidos, o => o.total)
    .sort((a, b) => b[1] - a[1]).slice(0, 5)

  const max = lista => Math.max(0, ...lista.map(x => x[1]))

  return (
    <AdminLayout>
      <h1 className="section-title">Reportes</h1>
      <div className="row g-3 mb-4">
        <div className="col-6 col-md-3"><div className="card p-3"><small className="text-muted">Ventas totales</small><span className="fs-5 fw-bold">{plata(ventas)}</span></div></div>
        <div className="col-6 col-md-3"><div className="card p-3"><small className="text-muted">Ordenes pagadas</small><span className="fs-5 fw-bold">{pagadas.length}</span></div></div>
        <div className="col-6 col-md-3"><div className="card p-3"><small className="text-muted">Pagos rechazados</small><span className="fs-5 fw-bold text-danger">{rechazadas}</span></div></div>
        <div className="col-6 col-md-3"><div className="card p-3"><small className="text-muted">Ticket promedio</small><span className="fs-5 fw-bold">{plata(ticket)}</span></div></div>
      </div>
      <div className="row g-4">
        <div className="col-lg-6">
          <div className="card p-4 h-100">
            <h2 className="h6 mb-3">Ventas por mes</h2>
            {porMes.length === 0 && <p className="text-muted small">Sin ventas aun.</p>}
            {porMes.map(([mes, total]) => <Barra key={mes} etiqueta={mes} valor={total} maximo={max(porMes)} texto={plata(total)} />)}
          </div>
        </div>
        <div className="col-lg-6">
          <div className="card p-4 h-100">
            <h2 className="h6 mb-3">Ventas por region</h2>
            {porRegion.map(([reg, total]) => <Barra key={reg} etiqueta={reg} valor={total} maximo={max(porRegion)} texto={plata(total)} />)}
          </div>
        </div>
        <div className="col-lg-6">
          <div className="card p-4 h-100">
            <h2 className="h6 mb-3">Mejores clientes</h2>
            {porCliente.map(([cli, total]) => <Barra key={cli} etiqueta={cli} valor={total} maximo={max(porCliente)} texto={plata(total)} />)}
          </div>
        </div>
        <div className="col-lg-6">
          <div className="card p-4 h-100">
            <h2 className="h6 mb-3">Tipo de entrega</h2>
            {porEntrega.map(([tipo, n]) => <Barra key={tipo} etiqueta={tipo} valor={n} maximo={max(porEntrega)} texto={n + " ordenes"} />)}
            <p className="small text-muted mt-3 mb-0">Unidades vendidas en total: {unidades}</p>
          </div>
        </div>
      </div>
    </AdminLayout>
  )
}
