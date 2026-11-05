import { useState } from 'react'
import { Link } from 'react-router-dom'
import { getSesion, ordenesDeUsuario, plata, fechaCorta } from '../datos.js'

export default function MisCompras() {
  const [sesion] = useState(() => getSesion())
  const [estado, setEstado] = useState("")
  const [busqueda, setBusqueda] = useState("")

  if (!sesion) {
    return (
      <section className="container py-5">
        <div className="text-center">
          <h1 className="section-title">Mis compras</h1>
          <p className="text-muted">Debes iniciar sesion para ver tu historial de compras.</p>
          <div className="d-flex flex-wrap justify-content-center gap-2 mt-3">
            <Link to="/login" className="btn btn-accent">Iniciar sesion</Link>
            <Link to="/productos" className="btn btn-outline-accent">Ir a la tienda</Link>
          </div>
        </div>
      </section>
    )
  }

  if (sesion.rolId !== 3) {
    return (
      <section className="container py-5">
        <div className="text-center">
          <h1 className="section-title">Mis compras</h1>
          <p className="text-muted">Esta seccion solo esta disponible para clientes.</p>
          <div className="d-flex flex-wrap justify-content-center gap-2 mt-3">
            {(sesion.rolId === 1 || sesion.rolId === 2) && <Link to="/admin" className="btn btn-accent">Ir al panel admin</Link>}
            <Link to="/" className="btn btn-outline-accent">Volver al inicio</Link>
          </div>
        </div>
      </section>
    )
  }

  const texto = busqueda.trim().toLowerCase()
  const todas = ordenesDeUsuario(sesion)
  const ordenes = todas
    .filter(o => !estado || o.estado === estado)
    .filter(o => {
      if (!texto) return true
      const numero = String(o.numero || "").toLowerCase()
      const items = (o.items || []).map(i => String(i.nombre || "").toLowerCase()).join(" ")
      const cliente = ((o.cliente?.nombre || "") + " " + (o.cliente?.apellidos || "")).toLowerCase()
      return numero.includes(texto) || items.includes(texto) || cliente.includes(texto)
    })
    .sort((a, b) => String(b.fecha || "").localeCompare(String(a.fecha || "")))

  const pagadas = todas.filter(o => o.estado === "Pagada")
  const gastado = pagadas.reduce((s, o) => s + Number(o.total || 0), 0)
  const rechazadas = todas.length - pagadas.length

  return (
    <section className="container py-5">
      <div className="d-flex flex-wrap justify-content-between align-items-end gap-2 mb-4">
        <div>
          <h1 className="section-title mb-1">Mis compras</h1>
          <p className="text-muted mb-0">Aqui puedes revisar el historial de todas tus compras en FreshFits.</p>
        </div>
        <Link to="/productos" className="btn btn-accent"><i className="bi bi-bag me-1"></i>Seguir comprando</Link>
      </div>

      <div className="row g-3 mb-4">
        <div className="col-md-4">
          <div className="card p-3 h-100"><small className="text-muted">Compras realizadas</small><span className="fs-4 fw-bold">{pagadas.length}</span></div>
        </div>
        <div className="col-md-4">
          <div className="card p-3 h-100"><small className="text-muted">Total gastado</small><span className="fs-4 fw-bold">{plata(gastado)}</span></div>
        </div>
        <div className="col-md-4">
          <div className="card p-3 h-100"><small className="text-muted">Pagos rechazados</small><span className="fs-4 fw-bold text-danger">{rechazadas}</span></div>
        </div>
      </div>

      <div className="row g-2 mb-3">
        <div className="col-md-8">
          <input className="form-control" placeholder="Buscar por numero de orden o producto..." value={busqueda} onChange={e => setBusqueda(e.target.value)} />
        </div>
        <div className="col-md-4">
          <select className="form-select" value={estado} onChange={e => setEstado(e.target.value)}>
            <option value="">Todos los estados</option>
            <option>Pagada</option>
            <option>Rechazada</option>
          </select>
        </div>
      </div>

      <div className="table-responsive bg-ff-panel rounded p-2">
        <table className="table table-striped mb-0 align-middle">
          <thead>
            <tr><th>Numero</th><th>Fecha</th><th>Productos</th><th>Entrega</th><th>Total</th><th>Estado</th><th></th></tr>
          </thead>
          <tbody>
            {ordenes.length === 0 && (
              <tr>
                <td colSpan="7" className="text-center text-muted py-4">
                  {todas.length === 0
                    ? "No a realizado ninguna compra por el momento"
                    : "No encontramos compras que coincidan con la busqueda."}
                </td>
              </tr>
            )}
            {ordenes.map(o => (
              <tr key={o.id}>
                <td>#{o.numero}</td>
                <td>{fechaCorta(o.fecha)}</td>
                <td className="small">{(o.items || []).map(i => i.cantidad + "x " + i.nombre).join(", ")}</td>
                <td>{o.envio?.tipo || "-"}</td>
                <td>{plata(o.total)}</td>
                <td><span className={"badge " + (o.estado === "Pagada" ? "bg-success" : "bg-danger")}>{o.estado}</span></td>
                <td><Link to={"/mis-compras/" + o.id} className="btn btn-sm btn-outline-accent"><i className="bi bi-eye"></i> Ver detalle</Link></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </section>
  )
}
