import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { listarProductos, listarArticulos } from '../api.js'
import { agregarConAviso } from '../alertas.js'
import { imgProducto } from '../datos.js'
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
    // si el backend no responde, el inicio igual se muestra con el banner
    listarProductos({ soloActivos: true })
      .then(prods => setDestacados(prods.filter(p => p.destacado)))
      .catch(() => setDestacados([]))
    listarArticulos()
      .then(arts => setNoticias(arts.slice(0, 3)))
      .catch(() => setNoticias([]))
  }, [])

  useEffect(() => {
    const t = setInterval(() => {
      setFoto(f => (f + 1) % fotos.length)
    }, 5000)
    return () => clearInterval(t)
  }, [])

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
        </div>
        <div className="hero-controles">
          <button className="hero-flecha" type="button" aria-label="Imagen anterior" onClick={() => setFoto((foto - 1 + fotos.length) % fotos.length)}>
            <i className="bi bi-chevron-left"></i>
          </button>
          {fotos.map((_, i) => (
            <button key={i} type="button" aria-label={"Imagen " + (i + 1)} className={"hero-punto" + (i === foto ? " activo" : "")} onClick={() => setFoto(i)} />
          ))}
          <button className="hero-flecha" type="button" aria-label="Imagen siguiente" onClick={() => setFoto((foto + 1) % fotos.length)}>
            <i className="bi bi-chevron-right"></i>
          </button>
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
            {destacados.map(p => <Tarjeta key={p.codigo} p={p} onAgregar={() => agregarConAviso(p)} />)}
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
            <div className="col-md-6 col-lg-4" key={n.id}>
              <article className="card h-100 card-ff-hover">
                <img src={imgProducto(n)} className="card-img-top" alt={n.titulo} />
                <div className="card-body d-flex flex-column">
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
