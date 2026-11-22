import { Link } from 'react-router-dom'
import { Card, Col, Badge, Button } from 'react-bootstrap'
import { plata, imgProducto, nombreCategoria, precioFinal, enOferta } from '../datos.js'

function Tarjeta({ p, onAgregar }) {
  const agotado = p.stock <= 0
  const oferta = enOferta(p)
  return (
    <Col sm={6} lg={3}>
      <Card className="h-100 card-ff-hover position-relative">
        {oferta && <span className="badge bg-danger badge-oferta">-{p.descuento}%</span>}
        <img src={imgProducto(p)} className="card-img-top" alt={p.nombre} />
        <Card.Body className="d-flex flex-column">
          <span className="badge bg-secondary align-self-start mb-2">{nombreCategoria(p.categoriaId)}</span>
          <h3 className="h6">{p.nombre}</h3>
          <p className="fw-bold mb-2">
            {oferta && <span className="precio-tachado me-2">{plata(p.precio)}</span>}
            <span className={oferta ? "text-danger" : ""}>{plata(precioFinal(p))}</span>
          </p>
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
