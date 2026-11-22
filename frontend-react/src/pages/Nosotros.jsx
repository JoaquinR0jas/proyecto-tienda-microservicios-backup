import { Container, Row, Col, Card } from 'react-bootstrap'

export default function Nosotros() {
  return (
    <Container className="py-5">
      <Row className="align-items-center g-5">
        <Col lg={6}>
          <h1 className="section-title">Quienes somos</h1>
          <p>
            <strong>FreshFits</strong> es una tienda online dedicada a la venta de ropa moderna,
            creada para que lleves un estilo comodo, actual y sobre todo fresh en tu dia a dia.
          </p>
          <p>
            En FreshFits encontraras poleras, polerones, jeans, camisas, zapatillas y accesorios,
            con una experiencia de compra simple, rapida y pensada para quienes aman vestir bien
            sin complicaciones.
          </p>
          <p>
            Nuestro proposito es ofrecer un catalogo variado, informacion clara de cada prenda
            y un proceso de compra sin complicaciones, todo dentro de una interfaz moderna y
            atractiva con identidad streetwear.
          </p>
        </Col>
        <Col lg={6} className="text-center">
          <img src="/img/quiensomos.jpg" alt="Quienes somos - FreshFits" className="img-fluid rounded" />
        </Col>
      </Row>

      <Row className="g-4 mt-5">
        <Col md={4}>
          <Card className="h-100 text-center p-4">
            <i className="bi bi-handbag display-5 mb-3"></i>
            <h3 className="h5">Que vendemos</h3>
            <p className="small text-muted mb-0">Ropa moderna para hombre y mujer, de multiples estilos y categorias.</p>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="h-100 text-center p-4">
            <i className="bi bi-bullseye display-5 mb-3"></i>
            <h3 className="h5">Nuestro proposito</h3>
            <p className="small text-muted mb-0">Acercar la moda a mas personas con una plataforma simple y confiable.</p>
          </Card>
        </Col>
        <Col md={4}>
          <Card className="h-100 text-center p-4">
            <i className="bi bi-people display-5 mb-3"></i>
            <h3 className="h5">Quienes lo desarrollamos</h3>
            <p className="small text-muted mb-0">Un equipo de estudiantes que construyo este proyecto academico con mucha actitud.</p>
          </Card>
        </Col>
      </Row>
    </Container>
  )
}
