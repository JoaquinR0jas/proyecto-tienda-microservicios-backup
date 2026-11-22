import { Link } from 'react-router-dom'
import { Button, Card, Col, Container, Row } from 'react-bootstrap'
import { imgProducto } from '../datos.js'
import { listarArticulos } from '../api.js'
import Cargando, { useCarga } from '../components/Cargando.jsx'

export default function Blog() {
  // articulos publicados en ms-contenido
  const articulos = useCarga(listarArticulos)
  const noticias = articulos.datos || []

  return (
    <Container className="py-5">
      <h1 className="section-title">Blog de Moda</h1>
      <p className="text-muted mb-4">Novedades, tendencias y consejos de estilo para vestir fresh todos los dias.</p>
      <Cargando cargando={articulos.cargando} error={articulos.error} onReintentar={articulos.recargar} />
      <Row className="g-4">
        {noticias.map(n => (
          <Col md={6} lg={4} key={n.id}>
            <Card as="article" className="h-100 card-ff-hover">
              <img src={imgProducto(n)} className="card-img-top" alt={n.titulo} />
              <Card.Body className="d-flex flex-column">
                <p className="small text-muted mb-1">{n.fecha}</p>
                <Card.Title as="h3" className="h6">{n.titulo}</Card.Title>
                <p className="small text-muted flex-grow-1">{n.resumen}</p>
                <Button as={Link} to={"/blog/" + n.slug} variant="outline-accent" size="sm" className="align-self-start">Ver noticia</Button>
              </Card.Body>
            </Card>
          </Col>
        ))}
      </Row>
    </Container>
  )
}
