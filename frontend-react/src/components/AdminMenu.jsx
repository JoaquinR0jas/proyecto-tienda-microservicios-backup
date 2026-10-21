import { Link } from 'react-router-dom'
import { Nav } from 'react-bootstrap'

export function AdminMenu() {
  return (
    <>
      <div className="admin-sidebar d-none d-lg-block">
        <Nav className="flex-column gap-1">
          <Nav.Link as={Link} to="/admin" className="px-3 py-2">Panel principal</Nav.Link>
          <Nav.Link as={Link} to="/admin/productos" className="px-3 py-2">Productos</Nav.Link>
          <Nav.Link as={Link} to="/admin/usuarios" className="px-3 py-2">Usuarios</Nav.Link>
          <Nav.Link as={Link} to="/" className="px-3 py-2">Volver a la tienda</Nav.Link>
        </Nav>
      </div>
    </>
  )
}

export function AdminLayout({ children }) {
  return (
    <div className="container-fluid">
      <div className="row">
        <div className="col-lg-3 col-xl-2 d-none d-lg-block px-0">
          <AdminMenu />
        </div>
        <div className="col-lg-9 col-xl-10 p-4">
          {children}
        </div>
      </div>
    </div>
  )
}
