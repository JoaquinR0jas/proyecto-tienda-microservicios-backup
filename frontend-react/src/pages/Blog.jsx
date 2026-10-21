import { Link } from 'react-router-dom'
import { Container, Row, Col, Card, Button } from 'react-bootstrap'
import { KEYS, getColeccion } from '../datos.js'
import { useEffect, useState } from 'react'

export default function Blog() {
  const [noticias, setNoticias] = useState([])

  useEffect(() => {
    setNoticias(getColeccion(KEYS.blog))
  }, [])

  return (
    <Container className="py-5">
      <h1 className="section-title">Blog de Moda</h1>
      <p className="text-muted mb-4">Novedades, tendencias y consejos de estilo para vestir fresh todos los dias.</p>
      <Row className="g-4">
        {noticias.map(n => (
          <Col md={6} lg={4} key={n.id}>
            <Card className="h-100 card-ff-hover">
              <Card.Img variant="top" src={"/img/" + n.imagen} alt={n.titulo} />
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
