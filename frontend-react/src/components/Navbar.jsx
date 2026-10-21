import { Link, useNavigate, useLocation } from 'react-router-dom'
import { useEffect, useState } from 'react'
import { Navbar as NavbarBootstrap, Nav, NavDropdown, Container, Badge, Button } from 'react-bootstrap'
import { KEYS, getColeccion, getSesion } from '../datos.js'

export default function Navbar() {
  const [sesion, setSesion] = useState(null)
  const [cant, setCant] = useState(0)
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

  function salir() {
    localStorage.removeItem(KEYS.sesion)
    window.dispatchEvent(new Event("sesion-cambio"))
    nav("/login")
  }

  const esAdmin = sesion && (sesion.rolId === 1 || sesion.rolId === 2)

  return (
    <header>
      <NavbarBootstrap expand="lg" className="navbar-light ff-navbar sticky-top">
        <Container>
          <NavbarBootstrap.Brand as={Link} to="/" className="d-flex align-items-center gap-2">
            <span className="brand-text">FRESH FITS</span>
          </NavbarBootstrap.Brand>
          <NavbarBootstrap.Toggle aria-controls="navMenu" />
          <NavbarBootstrap.Collapse id="navMenu">
            <Nav className="mx-auto mb-2 mb-lg-0">
              <Nav.Item>
                <Nav.Link as={Link} to="/" active={ruta === "/"}>Inicio</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link as={Link} to="/productos" active={ruta.startsWith("/productos")}>Tienda</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link as={Link} to="/blog" active={ruta.startsWith("/blog")}>Blog</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link as={Link} to="/nosotros" active={ruta === "/nosotros"}>Nosotros</Nav.Link>
              </Nav.Item>
              <Nav.Item>
                <Nav.Link as={Link} to="/contacto" active={ruta === "/contacto"}>Contacto</Nav.Link>
              </Nav.Item>
              {esAdmin && (
                <Nav.Item>
                  <Nav.Link as={Link} to="/admin"><i className="bi bi-speedometer2"></i> Administracion</Nav.Link>
                </Nav.Item>
              )}
            </Nav>
            <Nav className="align-items-lg-center gap-lg-3">
              <Nav.Item>
                <Nav.Link as={Link} to="/carrito" title="Carrito" className="position-relative">
                  <i className="bi bi-bag fs-5"></i>
                  {cant > 0 && (
                    <Badge pill bg="danger" className="position-absolute top-0 start-100 translate-middle">{cant}</Badge>
                  )}
                </Nav.Link>
              </Nav.Item>
              {!sesion ? (
                <Nav.Item>
                  <div className="d-flex align-items-center gap-2">
                    <Button as={Link} to="/login" variant="outline-dark" size="sm">Iniciar sesion</Button>
                    <Button as={Link} to="/registro" variant="dark" size="sm">Registrarse</Button>
                  </div>
                </Nav.Item>
              ) : (
                <NavDropdown
                  id="userMenu"
                  align="end"
                  title={<><i className="bi bi-person-circle fs-5"></i> {sesion.nombre}</>}
                >
                  <NavDropdown.Item as="button" onClick={salir}><i className="bi bi-box-arrow-right"></i> Cerrar sesion</NavDropdown.Item>
                </NavDropdown>
              )}
            </Nav>
          </NavbarBootstrap.Collapse>
        </Container>
      </NavbarBootstrap>
    </header>
  )
}
