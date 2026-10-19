import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useEffect, useRef, useState } from 'react'
import { KEYS, getColeccion, getSesion } from '../datos.js'

export default function Navbar() {
  const [sesion, setSesion] = useState(null)
  const [cant, setCant] = useState(0)
  const [texto, setTexto] = useState("")
  const [linea, setLinea] = useState({ left: 0, width: 0 })
  const menuRef = useRef(null)
  const nav = useNavigate()
  const ruta = useLocation().pathname

  function refrescar() {
    setSesion(getSesion())
    const carrito = getColeccion(KEYS.carrito)
    let total = 0
    carrito.forEach(i => total += i.cantidad)
    setCant(total)
  }

  useEffect(() => {
    refrescar()
    window.addEventListener("carrito-cambio", refrescar)
    window.addEventListener("sesion-cambio", refrescar)
    return () => {
      window.removeEventListener("carrito-cambio", refrescar)
      window.removeEventListener("sesion-cambio", refrescar)
    }
  }, [ruta])

  // mueve la linea indicadora bajo el enlace indicado, o bajo el activo si no se indica ninguno
  function moverLinea(enlace) {
    const menu = menuRef.current
    if (!menu) return
    const destino = enlace || menu.querySelector(".nav-link.active")
    if (!destino) {
      setLinea({ left: 0, width: 0 })
      return
    }
    const r = destino.getBoundingClientRect()
    const m = menu.getBoundingClientRect()
    setLinea({ left: r.left - m.left, width: r.width })
  }

  useEffect(() => {
    moverLinea(null)
    // cuando terminan de cargar las fuentes cambia el ancho de los enlaces
    document.fonts?.ready.then(() => moverLinea(null))
    const alCambiarTamano = () => moverLinea(null)
    window.addEventListener("resize", alCambiarTamano)
    return () => window.removeEventListener("resize", alCambiarTamano)
  }, [ruta, sesion])

  function buscar(e) {
    e.preventDefault()
    nav("/productos" + (texto.trim() ? "?q=" + encodeURIComponent(texto.trim()) : ""))
  }

  function salir(e) {
    e.preventDefault()
    localStorage.removeItem(KEYS.sesion)
    window.dispatchEvent(new Event("sesion-cambio"))
    nav("/login")
  }

  const esAdmin = sesion && (sesion.rolId === 1 || sesion.rolId === 2)

  function enlace(to, contenido, activo) {
    return (
      <li className="nav-item">
        <Link className={"nav-link" + (activo ? " active" : "")} to={to} onMouseEnter={e => moverLinea(e.currentTarget)}>
          {contenido}
        </Link>
      </li>
    )
  }

  return (
    <header>
      <nav className="navbar navbar-expand-lg navbar-light ff-navbar sticky-top">
        <div className="container">
          <Link className="navbar-brand d-flex align-items-center gap-2" to="/">
            <span className="brand-text">FRESH FITS</span>
          </Link>
          <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMenu">
            <span className="navbar-toggler-icon"></span>
          </button>
          <div className="collapse navbar-collapse" id="navMenu">
            <ul className="navbar-nav mx-auto mb-2 mb-lg-0" id="navMenuPrincipal" ref={menuRef} onMouseLeave={() => moverLinea(null)}>
              <span className="nav-indicator" style={{ left: linea.left, width: linea.width }}></span>
              {enlace("/", "Inicio", ruta === "/")}
              {enlace("/productos", "Tienda", ruta.startsWith("/productos") || ruta.startsWith("/producto/"))}
              {enlace("/ofertas", "Ofertas", ruta === "/ofertas")}
              {enlace("/blog", "Blog", ruta.startsWith("/blog"))}
              {enlace("/nosotros", "Nosotros", ruta === "/nosotros")}
              {enlace("/contacto", "Contacto", ruta === "/contacto")}
              {esAdmin && enlace("/admin", <><i className="bi bi-speedometer2"></i> Administracion</>, ruta.startsWith("/admin"))}
            </ul>
            <form className="d-flex my-2 my-lg-0 me-lg-3" role="search" onSubmit={buscar}>
              <input className="form-control form-control-sm" type="search" placeholder="Buscar" value={texto} onChange={e => setTexto(e.target.value)} />
              <button className="btn btn-outline-dark btn-sm ms-1" type="submit"><i className="bi bi-search"></i></button>
            </form>
            <ul className="navbar-nav align-items-lg-center gap-lg-3">
              <li className="nav-item">
                <Link className="nav-link position-relative" to="/carrito" title="Carrito">
                  <i className="bi bi-bag fs-5"></i>
                  {cant > 0 && (
                    <span className="badge rounded-pill bg-danger position-absolute top-0 start-100 translate-middle">{cant}</span>
                  )}
                </Link>
              </li>
              {!sesion ? (
                <li className="nav-item">
                  <div className="d-flex align-items-center gap-2 text-nowrap">
                    <Link className="btn btn-outline-dark btn-sm" to="/login">Iniciar sesion</Link>
                    <Link className="btn btn-dark btn-sm" to="/registro">Registrarse</Link>
                  </div>
                </li>
              ) : (
                <li className="nav-item dropdown">
                  <a className="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown">
                    <i className="bi bi-person-circle fs-5"></i> {sesion.nombre}
                  </a>
                  <ul className="dropdown-menu dropdown-menu-end">
                    <li><a className="dropdown-item" href="#" onClick={salir}><i className="bi bi-box-arrow-right"></i> Cerrar sesion</a></li>
                  </ul>
                </li>
              )}
            </ul>
          </div>
        </div>
      </nav>
    </header>
  )
}
