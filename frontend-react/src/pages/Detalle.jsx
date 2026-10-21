import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { Container, Row, Col, Badge, Button, Form, Breadcrumb } from 'react-bootstrap'
import { KEYS, getColeccion, setColeccion, plata, imgProducto, nombreCategoria } from '../datos.js'

export default function Detalle() {
  const { codigo } = useParams()
  const [p, setP] = useState(null)
  const [cant, setCant] = useState(1)

  useEffect(() => {
    const prod = getColeccion(KEYS.productos).find(x => x.codigo === codigo)
    setP(prod || null)
    setCant(1)
  }, [codigo])

  if (!p) {
    return (
      <Container className="py-5">
        <p className="text-muted text-center">Prenda no encontrada.</p>
        <div className="text-center"><Button as={Link} to="/productos" variant="accent">Volver al catalogo</Button></div>
      </Container>
    )
  }

  const agotado = p.stock <= 0

  function agregar() {
    const carrito = getColeccion(KEYS.carrito)
    const item = carrito.find(i => i.codigo === p.codigo)
    const actual = item ? item.cantidad : 0
    if (actual + cant > p.stock) {
      alert("Solo hay " + p.stock + " unidades disponibles")
      return
    }
    if (item) item.cantidad += cant
    else carrito.push({ codigo: p.codigo, cantidad: cant })
    setColeccion(KEYS.carrito, carrito)
    window.dispatchEvent(new Event("carrito-cambio"))
    alert(p.nombre + " se agrego al carrito")
  }

  return (
    <Container className="py-5">
      <Breadcrumb>
        <Breadcrumb.Item linkAs={Link} linkProps={{ to: "/" }}>Inicio</Breadcrumb.Item>
        <Breadcrumb.Item linkAs={Link} linkProps={{ to: "/productos" }}>Tienda</Breadcrumb.Item>
        <Breadcrumb.Item active>Detalle</Breadcrumb.Item>
      </Breadcrumb>
      <Row className="g-4">
        <Col md={5}>
          <img src={imgProducto(p)} className="img-fluid rounded bg-ff-panel p-2" alt={p.nombre} />
        </Col>
        <Col md={7}>
          <Badge bg="secondary" className="mb-2">{nombreCategoria(p.categoriaId)}</Badge>
          <h1 className="h3">{p.nombre}</h1>
          <p className="text-muted">{p.descripcion || "Sin descripcion disponible."}</p>
          <p className="display-6">{plata(p.precio)}</p>
          <p>{agotado ? <Badge bg="danger">Sin stock</Badge> : <Badge bg="success">Stock disponible: {p.stock}</Badge>}</p>
          <div className="d-flex align-items-center gap-3 my-3">
            <Form.Label className="mb-0">Cantidad</Form.Label>
            <Form.Control
              type="number"
              style={{ width: 90 }}
              value={cant}
              min={1}
              max={Math.max(p.stock, 1)}
              disabled={agotado}
              onChange={e => setCant(Math.max(1, parseInt(e.target.value) || 1))}
            />
          </div>
          <Button variant="accent" size="lg" disabled={agotado} onClick={agregar}>
            <i className="bi bi-bag-plus"></i> Añadir al carrito
          </Button>
        </Col>
      </Row>
    </Container>
  )
}
