import { Link } from 'react-router-dom'
import { Col, Card, Badge, Button } from 'react-bootstrap'
import { plata, imgProducto, nombreCategoria } from '../datos.js'

function Tarjeta({ p, onAgregar }) {
  const agotado = p.stock <= 0
  return (
    <Col sm={6} lg={3}>
      <Card className="h-100 card-ff-hover">
        <Card.Img variant="top" src={imgProducto(p)} alt={p.nombre} />
        <Card.Body className="d-flex flex-column">
          <Badge bg="secondary" className="align-self-start mb-2">{nombreCategoria(p.categoriaId)}</Badge>
          <Card.Title as="h3" className="h6">{p.nombre}</Card.Title>
          <p className="fw-bold mb-2">{plata(p.precio)}</p>
          <p className="small text-muted mb-3">
            {agotado ? <Badge bg="danger">Sin stock</Badge> : "Stock: " + p.stock}
          </p>
          <div className="mt-auto d-flex gap-2">
            <Button as={Link} to={"/producto/" + p.codigo} variant="outline-accent" size="sm" className="flex-fill">Ver detalle</Button>
            <Button variant="accent" size="sm" className="flex-fill" disabled={agotado} onClick={() => onAgregar(p.codigo)}>
              <i className="bi bi-bag-plus"></i>
            </Button>
          </div>
        </Card.Body>
      </Card>
    </Col>
  )
}

export default Tarjeta
