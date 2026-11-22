import { useEffect, useState } from 'react'
import { Container, Row, Col, Form, InputGroup } from 'react-bootstrap'
import { listarProductos, listarCategorias } from '../api.js'
import { agregarConAviso } from '../alertas.js'
import Cargando, { useCarga } from '../components/Cargando.jsx'
import Tarjeta from '../components/Tarjeta.jsx'

export default function Productos() {
  const [busqueda, setBusqueda] = useState("")
  const [cat, setCat] = useState("")
  const [cats, setCats] = useState([])
  const [lista, setLista] = useState([])
  // productos activos traidos de ms-catalogo, se filtran aqui mismo
  const productos = useCarga(() => listarProductos({ soloActivos: true }))

  useEffect(() => {
    listarCategorias().then(setCats).catch(() => setCats([]))
  }, [])

  useEffect(() => {
    let prods = productos.datos || []
    if (busqueda) {
      prods = prods.filter(p => p.nombre.toLowerCase().includes(busqueda.toLowerCase()))
    }
    if (cat) {
      prods = prods.filter(p => p.categoriaId === Number(cat))
    }
    setLista(prods)
  }, [busqueda, cat, productos.datos])

  function agregar(codigo) {
    agregarConAviso(lista.find(p => p.codigo === codigo))
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
              // EVENTO ONCHANGE
              // Actualizar el estado de busqueda cuando el usuario escribe en el input
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

      <Cargando cargando={productos.cargando} error={productos.error} onReintentar={productos.recargar} />
      <Row className="g-4">
        {!productos.cargando && !productos.error && lista.length === 0 && (
          <Col xs={12}><p className="text-muted text-center py-5">No se encontraron prendas con esos criterios.</p></Col>
        )}
        {lista.map(p => <Tarjeta key={p.codigo} p={p} onAgregar={agregar} />)}
      </Row>
    </Container>
  )
}
