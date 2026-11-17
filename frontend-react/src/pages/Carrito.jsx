import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { getCarrito, setCarrito, getSesion, plata, imgProducto, detalleCarrito, COSTO_DESPACHO, DESPACHO_GRATIS_DESDE } from '../datos.js'
import { listarProductos } from '../api.js'
import Cargando, { useCarga } from '../components/Cargando.jsx'
import { aviso, confirmar } from '../alertas.js'

export default function Carrito() {
  const nav = useNavigate()
  const [detalle, setDetalle] = useState({ items: [], subtotal: 0 })
  // el carrito vive en el navegador, pero los precios y el stock se toman del backend
  const productos = useCarga(() => listarProductos({ soloActivos: true }))

  function cargar() {
    setDetalle(detalleCarrito(productos.datos || []))
  }

  useEffect(() => {
    cargar()
  }, [productos.datos])

  function guardar(carrito) {
    setCarrito(carrito)
    cargar()
  }

  function cambiar(codigo, delta) {
    const carrito = getCarrito()
    const item = carrito.find(i => i.codigo === codigo)
    const info = detalle.items.find(i => i.codigo === codigo)
    if (!item || !info) return
    const nueva = item.cantidad + delta
    if (nueva <= 0) {
      sacar(codigo)
      return
    }
    if (nueva > info.stock) {
      aviso("Stock insuficiente", "Solo hay " + info.stock + " unidades disponibles.", "warning")
      return
    }
    item.cantidad = nueva
    guardar(carrito)
  }

  async function sacar(codigo) {
    if (!await confirmar("Eliminar producto?", "Se quitara del carrito de compras.")) return
    guardar(getCarrito().filter(i => i.codigo !== codigo))
    aviso("Eliminado", "El producto fue eliminado del carrito.")
  }

  // se puede llenar el carrito sin cuenta, pero para comprar hay que iniciar sesion
  async function irAPagar() {
    if (!getSesion()) {
      await aviso("Inicia sesion para comprar", "Tu carrito se mantiene, despues de entrar vuelves al pago.", "info")
      nav("/login", { state: { volverA: "/checkout" } })
      return
    }
    nav("/checkout")
  }

  async function vaciar() {
    if (!await confirmar("Vaciar carrito?", "Se quitaran todos los productos.", "Vaciar")) return
    guardar([])
  }

  const { items, subtotal } = detalle

  if (productos.cargando || productos.error) {
    return (
      <section className="container py-5">
        <Cargando cargando={productos.cargando} error={productos.error} onReintentar={productos.recargar} />
      </section>
    )
  }

  if (items.length === 0) {
    return (
      <section className="container py-5 text-center">
        <i className="bi bi-bag-x display-1 text-muted"></i>
        <p className="text-muted mt-3">Tu carrito esta vacio.</p>
        <Link to="/productos" className="btn btn-accent">Ver catalogo</Link>
      </section>
    )
  }

  return (
    <section className="container py-5">
      <h1 className="section-title">Carrito de compras</h1>
      <div className="row g-4">
        <div className="col-lg-8">
          {items.map(i => (
            <div className="row align-items-center bg-ff-panel rounded p-3 mb-3 g-3" key={i.codigo}>
              <div className="col-3 col-md-2">
                <img src={imgProducto(i)} className="img-fluid rounded" alt={i.nombre} />
              </div>
              <div className="col-9 col-md-4">
                <h3 className="h6 mb-1">{i.nombre}</h3>
                <p className="small text-muted mb-0">{plata(i.precio)} c/u</p>
              </div>
              <div className="col-6 col-md-3 d-flex align-items-center gap-2">
                <button className="btn btn-outline-accent cart-qty-btn" onClick={() => cambiar(i.codigo, -1)}>-</button>
                <span className="fw-bold">{i.cantidad}</span>
                <button className="btn btn-outline-accent cart-qty-btn" onClick={() => cambiar(i.codigo, 1)}>+</button>
              </div>
              <div className="col-4 col-md-2">
                <span className="fw-bold">{plata(i.precio * i.cantidad)}</span>
              </div>
              <div className="col-2 col-md-1 text-end">
                <button className="btn btn-outline-danger btn-sm" onClick={() => sacar(i.codigo)}><i className="bi bi-trash"></i></button>
              </div>
            </div>
          ))}
        </div>
        <div className="col-lg-4">
          <div className="bg-ff-panel rounded p-4">
            <h3 className="h5 mb-3">Resumen</h3>
            <div className="d-flex justify-content-between mb-2">
              <span>Subtotal</span>
              <span className="fw-bold fs-4">{plata(subtotal)}</span>
            </div>
            <p className="small text-muted">
              {subtotal >= DESPACHO_GRATIS_DESDE
                ? "Tu compra tiene despacho gratis."
                : "Despacho " + plata(COSTO_DESPACHO) + " (gratis sobre " + plata(DESPACHO_GRATIS_DESDE) + "). Retiro en tienda sin costo."}
            </p>
            <button className="btn btn-accent w-100 mb-2" onClick={irAPagar}>Ir a pagar</button>
            <button className="btn btn-outline-danger w-100 mb-2" onClick={vaciar}>Vaciar carrito</button>
            <Link to="/productos" className="btn btn-link w-100">Seguir comprando</Link>
          </div>
        </div>
      </div>
    </section>
  )
}
