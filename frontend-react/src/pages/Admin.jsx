import { Link } from 'react-router-dom'
import { KEYS, getColeccion, plata } from '../datos.js'
import { AdminLayout, useAdmin, seccionesPara } from '../components/AdminMenu.jsx'

export default function Admin() {
  const sesion = useAdmin()
  if (!sesion) return null

  const prods = getColeccion(KEYS.productos)
  const users = getColeccion(KEYS.usuarios)
  const pagadas = getColeccion(KEYS.ordenes).filter(o => o.estado === "Pagada")
  const ventas = pagadas.reduce((s, o) => s + o.total, 0)
  const inventario = prods.reduce((s, p) => s + p.stock, 0)
  const criticos = prods.filter(p => p.stockCritico != null && p.stock <= p.stockCritico)
  const clientes = users.filter(u => u.rolId === 3).length
  const mensajes = getColeccion(KEYS.contactos).length
  const rolNombre = sesion.rolId === 1 ? "Administrador" : "Vendedor"

  return (
    <AdminLayout>
      <h1 className="section-title mb-1">Dashboard</h1>
      <p className="text-muted">Bienvenido, {sesion.nombre} ({rolNombre}). Resumen de las actividades de la tienda.</p>

      <div className="row g-3 mb-4">
        <div className="col-md-4">
          <div className="card stat-card text-white bg-primary p-3 h-100">
            <div className="d-flex align-items-center gap-3">
              <i className="bi bi-cart3 display-6"></i>
              <div><div className="fw-bold">Compras</div><div className="fs-3">{pagadas.length}</div></div>
            </div>
            <small className="mt-2">Ventas totales: {plata(ventas)}</small>
          </div>
        </div>
        <div className="col-md-4">
          <div className="card stat-card text-white bg-success p-3 h-100">
            <div className="d-flex align-items-center gap-3">
              <i className="bi bi-box-seam display-6"></i>
              <div><div className="fw-bold">Productos</div><div className="fs-3">{prods.length}</div></div>
            </div>
            <small className="mt-2">Inventario actual: {inventario} unidades</small>
          </div>
        </div>
        <div className="col-md-4">
          <div className="card stat-card text-dark bg-warning p-3 h-100">
            <div className="d-flex align-items-center gap-3">
              <i className="bi bi-people display-6"></i>
              <div><div className="fw-bold">Usuarios</div><div className="fs-3">{users.length}</div></div>
            </div>
            <small className="mt-2">Clientes registrados: {clientes}</small>
          </div>
        </div>
      </div>

      <div className="row g-3 mb-4">
        {seccionesPara(sesion).filter(s => s.to !== "/admin" && s.to !== "/admin/mensajes").map(s => (
          <div className="col-sm-6 col-xl-3" key={s.to}>
            <Link to={s.to} className="card admin-modulo h-100 p-3 text-center text-reset">
              <i className={"bi fs-3 text-primary " + s.icono}></i>
              <h2 className="h6 mt-2 mb-1">{s.texto}</h2>
              <p className="small text-muted mb-0">{s.desc}</p>
            </Link>
          </div>
        ))}
        <div className="col-sm-6 col-xl-3">
          <Link to="/" className="card admin-modulo h-100 p-3 text-center text-reset">
            <i className="bi bi-shop fs-3 text-primary"></i>
            <h2 className="h6 mt-2 mb-1">Tienda</h2>
            <p className="small text-muted mb-0">Visualiza la tienda como la ven los clientes.</p>
          </Link>
        </div>
        <div className="col-sm-6 col-xl-3">
          <Link to="/admin/mensajes" className="card admin-modulo h-100 p-3 text-center text-reset position-relative">
            {mensajes > 0 && <span className="badge rounded-pill bg-danger position-absolute top-0 end-0 m-2">{mensajes}</span>}
            <i className="bi bi-envelope fs-3 text-primary"></i>
            <h2 className="h6 mt-2 mb-1">Mensajes</h2>
            <p className="small text-muted mb-0">{mensajes} mensaje(s) recibidos desde el formulario de contacto.</p>
          </Link>
        </div>
      </div>

      <div className="card p-4">
        <div className="d-flex justify-content-between align-items-center">
          <h3 className="h5 mb-0"><i className="bi bi-exclamation-triangle-fill text-danger"></i> Stock critico</h3>
          <Link to="/admin/productos/criticos" className="btn btn-sm btn-outline-accent">Ver listado</Link>
        </div>
        <p className="text-muted mb-0 mt-2">
          {criticos.length === 0 ? "Sin alertas de stock por el momento." : criticos.length + " producto(s) estan en o bajo su stock critico."}
        </p>
      </div>
    </AdminLayout>
  )
}
