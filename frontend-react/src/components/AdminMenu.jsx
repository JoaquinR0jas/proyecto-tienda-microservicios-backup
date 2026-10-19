import { useEffect, useState } from 'react'
import { Link, NavLink, useNavigate } from 'react-router-dom'
import { KEYS, getSesion } from '../datos.js'
import { aviso } from '../alertas.js'

// opciones del menu, soloAdmin = el vendedor no las ve
const SECCIONES_ADMIN = [
  { to: "/admin", texto: "Dashboard", icono: "bi-speedometer2", fin: true, desc: "Vision general de las metricas y estadisticas clave." },
  { to: "/admin/ordenes", texto: "Ordenes", icono: "bi-receipt", desc: "Gestion y seguimiento de las ordenes de compra." },
  { to: "/admin/productos", texto: "Productos", icono: "bi-box-seam", desc: "Administrar inventario y detalles de los productos." },
  { to: "/admin/categorias", texto: "Categorias", icono: "bi-tags", soloAdmin: true, desc: "Organizar productos en categorias." },
  { to: "/admin/usuarios", texto: "Usuarios", icono: "bi-people", soloAdmin: true, desc: "Gestion de cuentas de usuario y sus roles." },
  { to: "/admin/reportes", texto: "Reportes", icono: "bi-bar-chart", soloAdmin: true, desc: "Informes detallados de las ventas del sistema." },
  { to: "/admin/mensajes", texto: "Mensajes", icono: "bi-envelope", desc: "Mensajes recibidos desde el formulario de contacto." },
  { to: "/admin/perfil", texto: "Perfil", icono: "bi-person-circle", desc: "Tu informacion personal y configuracion de cuenta." }
]

export function seccionesPara(sesion) {
  return SECCIONES_ADMIN.filter(s => !s.soloAdmin || sesion.rolId === 1)
}

// revisa que haya una sesion de admin (o vendedor si soloAdmin es false)
export function useAdmin(soloAdmin = false) {
  const nav = useNavigate()
  const [sesion] = useState(() => getSesion())
  const ok = !!sesion && (soloAdmin ? sesion.rolId === 1 : (sesion.rolId === 1 || sesion.rolId === 2))

  useEffect(() => {
    if (ok) return
    if (!sesion) {
      aviso("Acceso restringido", "Debes iniciar sesion con una cuenta autorizada para ver esta pagina.", "warning")
      nav("/login")
    } else {
      aviso("Acceso restringido", "Tu rol no permite ver esta seccion.", "warning")
      nav(sesion.rolId === 2 ? "/admin" : "/")
    }
  }, [])

  return ok ? sesion : null
}

export function AdminMenu() {
  const nav = useNavigate()
  const sesion = getSesion()
  if (!sesion) return null

  function salir() {
    localStorage.removeItem(KEYS.sesion)
    window.dispatchEvent(new Event("sesion-cambio"))
    nav("/login")
  }

  return (
    <div className="d-flex flex-column gap-1">
      {seccionesPara(sesion).map(s => (
        <NavLink key={s.to} to={s.to} end={s.fin} className="nav-link px-3 py-2">
          <i className={"bi me-2 " + s.icono}></i>{s.texto}
        </NavLink>
      ))}
      <hr />
      <Link to="/" className="btn btn-dark btn-sm mb-2"><i className="bi bi-shop me-1"></i> Tienda</Link>
      <button className="btn btn-danger btn-sm" onClick={salir}><i className="bi bi-box-arrow-right me-1"></i> Cerrar sesion</button>
    </div>
  )
}

export function AdminLayout({ children }) {
  return (
    <div className="container-fluid">
      <div className="row">
        <div className="col-lg-3 col-xl-2 d-none d-lg-block px-0">
          <div className="admin-sidebar">
            <AdminMenu />
          </div>
        </div>
        <div className="col-lg-9 col-xl-10 p-3 p-md-4">
          <div className="d-lg-none mb-3">
            <button className="btn btn-outline-dark btn-sm" type="button" data-bs-toggle="offcanvas" data-bs-target="#adminOffcanvas">
              <i className="bi bi-list"></i> Menu administracion
            </button>
          </div>
          {children}
        </div>
      </div>
      <div className="offcanvas offcanvas-start" tabIndex="-1" id="adminOffcanvas">
        <div className="offcanvas-header">
          <h5 className="offcanvas-title">Administracion</h5>
          <button type="button" className="btn-close" data-bs-dismiss="offcanvas"></button>
        </div>
        <div className="offcanvas-body" data-bs-dismiss="offcanvas">
          <AdminMenu />
        </div>
      </div>
    </div>
  )
}
