import { useState } from 'react'
import { Link } from 'react-router-dom'
import { KEYS, getColeccion, plata, fechaCorta } from '../datos.js'
import { AdminLayout, useAdmin } from '../components/AdminMenu.jsx'

export default function AdminOrdenes() {
  const sesion = useAdmin()
  const [busqueda, setBusqueda] = useState("")
  const [estado, setEstado] = useState("")
  if (!sesion) return null

  const texto = busqueda.toLowerCase()
  const ordenes = getColeccion(KEYS.ordenes)
    .filter(o => !estado || o.estado === estado)
    .filter(o => !texto || o.numero.includes(texto) ||
      (o.cliente.nombre + " " + o.cliente.apellidos + " " + o.cliente.correo).toLowerCase().includes(texto))
    .sort((a, b) => b.fecha.localeCompare(a.fecha))

  return (
    <AdminLayout>
      <h1 className="section-title">Ordenes / Boletas</h1>
      <div className="row g-2 mb-3">
        <div className="col-md-8">
          <input className="form-control" placeholder="Buscar por numero, cliente o correo..." value={busqueda} onChange={e => setBusqueda(e.target.value)} />
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
            <tr><th>Numero</th><th>Fecha</th><th>Cliente</th><th>Entrega</th><th>Total</th><th>Estado</th><th></th></tr>
          </thead>
          <tbody>
            {ordenes.length === 0 && <tr><td colSpan="7" className="text-center text-muted">No hay ordenes.</td></tr>}
            {ordenes.map(o => (
              <tr key={o.id}>
                <td>#{o.numero}</td>
                <td>{fechaCorta(o.fecha)}</td>
                <td>{o.cliente.nombre} {o.cliente.apellidos}<br /><small className="text-muted">{o.cliente.correo}</small></td>
                <td>{o.envio.tipo}</td>
                <td>{plata(o.total)}</td>
                <td><span className={"badge " + (o.estado === "Pagada" ? "bg-success" : "bg-danger")}>{o.estado}</span></td>
                <td><Link to={"/admin/ordenes/" + o.id} className="btn btn-sm btn-outline-accent"><i className="bi bi-eye"></i> Boleta</Link></td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </AdminLayout>
  )
}
