import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { Container, Row, Col, Card, Button } from 'react-bootstrap'
import { KEYS, getColeccion, setColeccion } from '../datos.js'
import Tarjeta from '../components/Tarjeta.jsx'

export default function Home() {
  const [destacados, setDestacados] = useState([])
  const [noticias, setNoticias] = useState([])
  const [foto, setFoto] = useState(0)
  const filaRef = useRef(null)

  const fotos = [
    "/img/banner-principal.png",
    "/img/banner2.png",
    "/img/banner3.png",
    "/img/carrusel1.png"
  ]

  useEffect(() => {
    const prods = getColeccion(KEYS.productos).filter(p => p.estado === "Activo" && p.destacado)
    setDestacados(prods)
    setNoticias(getColeccion(KEYS.blog).slice(0, 3))
  }, [])

  useEffect(() => {
    const t = setInterval(() => {
      setFoto(f => (f + 1) % fotos.length)
    }, 5000)
    return () => clearInterval(t)
  }, [])

  function agregar(codigo) {
    const prods = getColeccion(KEYS.productos)
    const prod = prods.find(p => p.codigo === codigo)
    if (!prod || prod.stock <= 0) return
    const carrito = getColeccion(KEYS.carrito)
    const item = carrito.find(i => i.codigo === codigo)
    const actual = item ? item.cantidad : 0
    if (actual + 1 > prod.stock) {
      alert("Solo hay " + prod.stock + " unidades de " + prod.nombre)
      return
    }
    if (item) item.cantidad += 1
    else carrito.push({ codigo, cantidad: 1 })
    setColeccion(KEYS.carrito, carrito)
    window.dispatchEvent(new Event("carrito-cambio"))
  }

  function mover(dir) {
    if (filaRef.current) {
      filaRef.current.scrollBy({ left: dir * filaRef.current.clientWidth * 0.8, behavior: "smooth" })
    }
  }

  return (
    <>
      <section className="hero-intro">
        <div className="hero-intro-bg">
          {fotos.map((src, i) => (
            <img
              key={src}
              src={src}
              alt="FreshFits"
              className="hero-intro-img"
              style={{ opacity: i === foto ? 1 : 0, transition: "opacity 0.8s" }}
            />
          ))}
          <div className="hero-intro-overlay"></div>
        </div>
        <div className="hero-intro-content">
          <h1 className="hero-intro-title">FRESH <span>FITS</span></h1>
          <div className="d-flex gap-2 justify-content-center mt-3">
            <Button variant="light" size="sm" onClick={() => setFoto((foto - 1 + fotos.length) % fotos.length)}>{"<"}</Button>
            <Button variant="light" size="sm" onClick={() => setFoto((foto + 1) % fotos.length)}>{">"}</Button>
          </div>
          <div className="d-flex gap-2 justify-content-center mt-2">
            {fotos.map((_, i) => (
              <button
                key={i}
                onClick={() => setFoto(i)}
                style={{
                  width: 10, height: 10, borderRadius: "50%", border: "none",
                  background: i === foto ? "#fff" : "rgba(255,255,255,0.4)"
                }}
              />
            ))}
          </div>
        </div>
        <div className="scroll-indicator" id="scrollIndicator">
          <span className="scroll-text">scroll</span>
          <i className="bi bi-chevron-double-down"></i>
        </div>
      </section>

      <Container className="py-5">
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h2 className="section-title mb-0">Productos destacados</h2>
          <Button as={Link} to="/productos" variant="outline-accent" size="sm">Ver todos <i className="bi bi-arrow-right"></i></Button>
        </div>
        <div className="position-relative">
          <Button variant="accent" className="carrusel-flecha carrusel-flecha-izq" onClick={() => mover(-1)} type="button">
            <i className="bi bi-chevron-left"></i>
          </Button>
          <Button variant="accent" className="carrusel-flecha carrusel-flecha-der" onClick={() => mover(1)} type="button">
            <i className="bi bi-chevron-right"></i>
          </Button>
          <div className="row g-4 carrusel-productos" ref={filaRef}>
            {destacados.map(p => <Tarjeta key={p.codigo} p={p} onAgregar={agregar} />)}
          </div>
        </div>
      </Container>

      <Container className="py-5">
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h2 className="section-title mb-0">Ultimas noticias</h2>
          <Button as={Link} to="/blog" variant="outline-accent" size="sm">Ver blog <i className="bi bi-arrow-right"></i></Button>
        </div>
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
    </>
  )
}
