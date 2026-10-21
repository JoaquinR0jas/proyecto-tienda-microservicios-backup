import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Row, Col, Card, ListGroup } from 'react-bootstrap'
import { KEYS, getColeccion, getSesion } from '../datos.js'
import { AdminLayout } from '../components/AdminMenu.jsx'

export default function Admin() {
  const nav = useNavigate()
  const [info, setInfo] = useState({ prods: [], users: [], contactos: [], sesion: null })

  useEffect(() => {
    const s = getSesion()
    if (!s || (s.rolId !== 1 && s.rolId !== 2)) {
      alert("Tienes que entrar con una cuenta admin")
      nav("/login")
      return
    }
    setInfo({
      prods: getColeccion(KEYS.productos),
      users: getColeccion(KEYS.usuarios),
      contactos: getColeccion(KEYS.contactos),
      sesion: s
    })
  }, [])

  if (!info.sesion) return null

  const criticos = info.prods.filter(p => p.stockCritico != null && p.stock <= p.stockCritico)
  const rolNombre = info.sesion.rolId === 1 ? "Administrador" : "Vendedor"

  return (
    <AdminLayout>
      <h1 className="section-title">Panel principal</h1>
      <p className="text-muted">Bienvenido, {info.sesion.nombre} ({rolNombre})</p>

      <Row className="g-4 mt-2">
        <Col md={4}>
          <Card className="admin-summary-card p-4">
            <i className="bi bi-handbag fs-2"></i>
            <h3 className="h2 mt-2 mb-0">{info.prods.length}</h3>
            <p className="text-muted mb-0">Productos registrados</p>
            <Link to="/admin/productos" className="stretched-link"></Link>
          </Card>
        </Col>
        {info.sesion.rolId === 1 && (
          <Col md={4}>
            <Card className="admin-summary-card p-4">
              <i className="bi bi-people fs-2"></i>
              <h3 className="h2 mt-2 mb-0">{info.users.length}</h3>
              <p className="text-muted mb-0">Usuarios registrados</p>
              <Link to="/admin/usuarios" className="stretched-link"></Link>
            </Card>
          </Col>
        )}
        <Col md={4}>
          <Card className="admin-summary-card p-4">
            <i className="bi bi-envelope fs-2"></i>
            <h3 className="h2 mt-2 mb-0">{info.contactos.length}</h3>
            <p className="text-muted mb-0">Mensajes de contacto</p>
          </Card>
        </Col>
      </Row>

      <Row className="g-4 mt-1">
        <Col md={6}>
          <Card className="p-4">
            <h3 className="h5"><i className="bi bi-exclamation-triangle-fill text-danger"></i> Stock critico</h3>
            <ListGroup variant="flush">
              {criticos.length === 0 && <ListGroup.Item className="text-muted">Sin alertas de stock por el momento.</ListGroup.Item>}
              {criticos.map(p => (
                <ListGroup.Item className="d-flex justify-content-between" key={p.codigo}>
                  <span>{p.nombre}</span>
                  <span className="stock-critico">Stock: {p.stock}</span>
                </ListGroup.Item>
              ))}
            </ListGroup>
          </Card>
        </Col>
      </Row>
    </AdminLayout>
  )
}
