import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { Container, Row, Col, Button } from 'react-bootstrap'
import { KEYS, getColeccion, setColeccion, plata, imgProducto } from '../datos.js'

export default function Carrito() {
  const [items, setItems] = useState([])

  function cargar() {
    setItems(getColeccion(KEYS.carrito))
  }

  useEffect(() => {
    cargar()
  }, [])

  function prodDe(codigo) {
    return getColeccion(KEYS.productos).find(p => p.codigo === codigo)
  }

  function cambiar(codigo, delta) {
    const carrito = getColeccion(KEYS.carrito)
    const item = carrito.find(i => i.codigo === codigo)
    const prod = prodDe(codigo)
    if (!item || !prod) return
    const nueva = item.cantidad + delta
    if (nueva <= 0) {
      if (confirm("Quitar este producto del carrito?")) {
        setColeccion(KEYS.carrito, carrito.filter(i => i.codigo !== codigo))
        cargar()
        window.dispatchEvent(new Event("carrito-cambio"))
      }
      return
    }
    if (nueva > prod.stock) {
      alert("Solo hay " + prod.stock + " unidades disponibles")
      return
    }
    item.cantidad = nueva
    setColeccion(KEYS.carrito, carrito)
    cargar()
    window.dispatchEvent(new Event("carrito-cambio"))
  }

  function sacar(codigo) {
    if (!confirm("Se quitara del carrito, seguro?")) return
    setColeccion(KEYS.carrito, getColeccion(KEYS.carrito).filter(i => i.codigo !== codigo))
    cargar()
    window.dispatchEvent(new Event("carrito-cambio"))
  }

  function vaciar() {
    if (!confirm("Vaciar todo el carrito?")) return
    setColeccion(KEYS.carrito, [])
    cargar()
    window.dispatchEvent(new Event("carrito-cambio"))
  }

  function comprar() {
    if (items.length === 0) return
    if (!confirm("Total a pagar: " + plata(total) + ". Confirmar compra?")) return
    setColeccion(KEYS.carrito, [])
    cargar()
    window.dispatchEvent(new Event("carrito-cambio"))
    alert("Gracias por tu compra, tu pedido quedo registrado")
  }

  let total = 0
  items.forEach(i => {
    const p = prodDe(i.codigo)
    if (p) total += p.precio * i.cantidad
  })

  if (items.length === 0) {
    return (
      <Container className="py-5 text-center">
        <i className="bi bi-bag-x display-1 text-muted"></i>
        <p className="text-muted mt-3">Tu carrito esta vacio.</p>
        <Button as={Link} to="/productos" variant="accent">Ver catalogo</Button>
      </Container>
    )
  }

  return (
    <Container className="py-5">
      <h1 className="section-title">Carrito de compras</h1>
      <Row>
        <Col lg={8}>
          {items.map(i => {
            const p = prodDe(i.codigo)
            if (!p) return null
            return (
              <Row className="align-items-center bg-ff-panel rounded p-3 mb-3 g-3" key={i.codigo}>
                <Col xs={3} md={2}>
                  <img src={imgProducto(p)} className="img-fluid rounded" alt={p.nombre} />
                </Col>
                <Col xs={9} md={4}>
                  <h3 className="h6 mb-1">{p.nombre}</h3>
                  <p className="small text-muted mb-0">{plata(p.precio)} c/u</p>
                </Col>
                <Col xs={6} md={3} className="d-flex align-items-center gap-2">
                  <Button variant="outline-accent" className="cart-qty-btn" onClick={() => cambiar(i.codigo, -1)}>-</Button>
                  <span className="fw-bold">{i.cantidad}</span>
                  <Button variant="outline-accent" className="cart-qty-btn" onClick={() => cambiar(i.codigo, 1)}>+</Button>
                </Col>
                <Col xs={4} md={2}>
                  <span className="fw-bold">{plata(p.precio * i.cantidad)}</span>
                </Col>
                <Col xs={2} md={1} className="text-end">
                  <Button variant="outline-danger" size="sm" onClick={() => sacar(i.codigo)}><i className="bi bi-trash"></i></Button>
                </Col>
              </Row>
            )
          })}
        </Col>
        <Col lg={4}>
          <div className="bg-ff-panel rounded p-4">
            <h3 className="h5 mb-3">Resumen</h3>
            <div className="d-flex justify-content-between mb-3">
              <span>Total</span>
              <span className="fw-bold fs-4">{plata(total)}</span>
            </div>
            <Button variant="accent" className="w-100 mb-2" onClick={comprar}>Finalizar compra</Button>
            <Button variant="outline-danger" className="w-100" onClick={vaciar}>Vaciar carrito</Button>
          </div>
        </Col>
      </Row>
    </Container>
  )
}
