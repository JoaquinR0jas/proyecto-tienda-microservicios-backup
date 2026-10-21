import { useEffect, useState } from 'react'
import { Container, Row, Col, Form, InputGroup } from 'react-bootstrap'
import { KEYS, getColeccion, setColeccion } from '../datos.js'
import Tarjeta from '../components/Tarjeta.jsx'

export default function Productos() {
  const [busqueda, setBusqueda] = useState("")
  const [cat, setCat] = useState("")
  const [cats, setCats] = useState([])
  const [lista, setLista] = useState([])

  useEffect(() => {
    setCats(getColeccion(KEYS.categorias))
  }, [])

  useEffect(() => {
    let prods = getColeccion(KEYS.productos).filter(p => p.estado === "Activo")
    if (busqueda) {
      prods = prods.filter(p => p.nombre.toLowerCase().includes(busqueda.toLowerCase()))
    }
    if (cat) {
      prods = prods.filter(p => p.categoriaId === Number(cat))
    }
    setLista(prods)
  }, [busqueda, cat])

  function agregar(codigo) {
    const prod = getColeccion(KEYS.productos).find(p => p.codigo === codigo)
    if (!prod || prod.stock <= 0) return
    const carrito = getColeccion(KEYS.carrito)
    const item = carrito.find(i => i.codigo === codigo)
    const actual = item ? item.cantidad : 0
    if (actual + 1 > prod.stock) {
      alert("Solo hay " + prod.stock + " unidades disponibles")
      return
    }
    if (item) item.cantidad += 1
    else carrito.push({ codigo, cantidad: 1 })
    setColeccion(KEYS.carrito, carrito)
    window.dispatchEvent(new Event("carrito-cambio"))
  }

  return (
    <Container className="py-5">
      <h1 className="section-title">Catalogo FreshFits</h1>

      <Row className="g-3 mb-4 bg-ff-panel p-3 rounded">
        <Col md={8}>
          <InputGroup>
            <InputGroup.Text className="bg-transparent text-muted"><i className="bi bi-search"></i></InputGroup.Text>
            <Form.Control
              type="text"
              placeholder="Buscar prenda por nombre..."
              value={busqueda}
              onChange={e => setBusqueda(e.target.value)}
            />
          </InputGroup>
        </Col>
        <Col md={4}>
          <Form.Select value={cat} onChange={e => setCat(e.target.value)}>
            <option value="">Todas las categorias</option>
            {cats.map(c => <option key={c.id} value={c.id}>{c.nombre}</option>)}
          </Form.Select>
        </Col>
      </Row>

      <Row className="g-4">
        {lista.length === 0 && (
          <Col xs={12}><p className="text-muted text-center py-5">No se encontraron prendas con esos criterios.</p></Col>
        )}
        {lista.map(p => <Tarjeta key={p.codigo} p={p} onAgregar={agregar} />)}
      </Row>
    </Container>
  )
}
