import { useEffect, useState } from 'react'
import { Link, NavLink, useNavigate } from 'react-router-dom'
import { Offcanvas } from 'react-bootstrap'
import { getSesion, cerrarSesion } from '../datos.js'
import { aviso } from '../alertas.js'

// opciones del menu, soloAdmin = el vendedor no las ve
const SECCIONES_ADMIN = [
  { to: "/admin", texto: "Dashboard", icono: "bi-speedometer2", fin: true, desc: "Vision general de las metricas y estadisticas clave." },
  { to: "/admin/ordenes", texto: "Ordenes", icono: "bi-receipt", desc: "Gestion y seguimiento de las ordenes de compra." },
  { to: "/admin/productos", texto: "Productos", icono: "bi-box-seam", desc: "Administrar inventario y detalles de los productos." },
  { to: "/admin/categorias", texto: "Categorias", icono: "bi-tags", soloAdmin: true, desc: "Organizar productos en categorias." },
  { to: "/admin/usuarios", texto: "Usuarios", icono: "bi-people", soloAdmin: true, desc: "Gestion de cuentas de usuario y sus roles." },
  { to: "/admin/reportes", texto: "Reportes", icono: "bi-bar-chart", soloAdmin: true, desc: "Informes detallados de las ventas del sistema." },
  { to: "/admin/mensajes", texto: "Mensajes", icono: "bi-envelope", soloAdmin: true, desc: "Mensajes recibidos desde el formulario de contacto." },
  { to: "/admin/perfil", texto: "Perfil", icono: "bi-person-circle", soloAdmin: true, desc: "Tu informacion personal y configuracion de cuenta." }
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
    cerrarSesion()
    nav("/login")
  }

  return (
    <div className="d-flex flex-column gap-1">
      {seccionesPara(sesion).map(s => (
        <NavLink key={s.to} to={s.to} end={s.fin} className="nav-link px-3 py-2">
          <i className={"bi me-2 " + s.icono}></i>{s.texto}
        </NavLink>
      ))}
    </div>
  )
}

export function AdminLayout({ children }) {
  // menu lateral en celular: Offcanvas de react-bootstrap controlado con estado
  const [menuAbierto, setMenuAbierto] = useState(false)
  const cerrarMenu = () => setMenuAbierto(false)

  return (
    <div className="container-fluid">
      <div className="row">
        <div className="col-lg-3 col-xl-2 d-none d-lg-block px-0 admin-sidebar-col">
          <div className="admin-sidebar">
            <AdminMenu />
          </div>
        </div>
        <div className="col-lg-9 col-xl-10 p-3 p-md-4 admin-contenido">
          <div className="d-lg-none mb-3">
            <button className="btn btn-outline-dark btn-sm" type="button" onClick={() => setMenuAbierto(true)}>
              <i className="bi bi-list"></i> Menu administracion
            </button>
          </div>
          {children}
        </div>
      </div>
      <Offcanvas show={menuAbierto} onHide={cerrarMenu} placement="start">
        <Offcanvas.Header closeButton>
          <Offcanvas.Title>Administracion</Offcanvas.Title>
        </Offcanvas.Header>
        {/* al elegir una opcion del menu se cierra el panel */}
        <Offcanvas.Body onClick={cerrarMenu}>
          <AdminMenu />
        </Offcanvas.Body>
      </Offcanvas>
    </div>
  )
}
