import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { KEYS, getColeccion } from '../datos.js'
import { agregarConAviso } from '../alertas.js'
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

      <section className="container py-5">
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h2 className="section-title mb-0">Productos destacados</h2>
          <Link to="/productos" className="btn btn-outline-accent btn-sm">Ver todos <i className="bi bi-arrow-right"></i></Link>
        </div>
        <div className="position-relative">
          <button className="btn btn-accent carrusel-flecha carrusel-flecha-izq" onClick={() => mover(-1)} type="button">
            <i className="bi bi-chevron-left"></i>
          </button>
          <button className="btn btn-accent carrusel-flecha carrusel-flecha-der" onClick={() => mover(1)} type="button">
            <i className="bi bi-chevron-right"></i>
          </button>
          <div className="row g-4 carrusel-productos" ref={filaRef}>
            {destacados.map(p => <Tarjeta key={p.codigo} p={p} onAgregar={c => agregarConAviso(c)} />)}
          </div>
        </div>
      </section>

      <section className="container py-5">
        <div className="d-flex justify-content-between align-items-center mb-4">
          <h2 className="section-title mb-0">Ultimas noticias</h2>
          <Link to="/blog" className="btn btn-outline-accent btn-sm">Ver blog <i className="bi bi-arrow-right"></i></Link>
        </div>
        <div className="row g-4">
          {noticias.map(n => (
            <div className="col-md-6 col-lg-4" key={n.id}>
              <article className="card h-100 card-ff-hover">
                <img src={"/img/" + n.imagen} className="card-img-top" alt={n.titulo} />
                <div className="card-body d-flex flex-column">
                  <p className="small text-muted mb-1">{n.fecha}</p>
                  <h3 className="h6">{n.titulo}</h3>
                  <p className="small text-muted flex-grow-1">{n.resumen}</p>
                  <Link to={"/blog/" + n.slug} className="btn btn-outline-accent btn-sm align-self-start">Ver noticia</Link>
                </div>
              </article>
            </div>
          ))}
        </div>
      </section>
    </>
  )
}
