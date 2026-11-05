import { useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { getSesion, ordenesDeUsuario, plata } from '../datos.js'
import Boleta from '../components/Boleta.jsx'

export default function MiCompraDetalle() {
  const { id } = useParams()
  const [sesion] = useState(() => getSesion())

  if (!sesion) {
    return (
      <section className="container py-5 text-center">
        <p className="text-muted">Debes iniciar sesion para ver tu compra.</p>
        <Link to="/login" className="btn btn-accent">Iniciar sesion</Link>
      </section>
    )
  }

  if (sesion.rolId !== 3) {
    return (
      <section className="container py-5 text-center">
        <p className="text-muted">Esta seccion es solo para clientes.</p>
        {(sesion.rolId === 1 || sesion.rolId === 2) && <Link to="/admin" className="btn btn-accent me-2">Ir al panel admin</Link>}
        <Link to="/" className="btn btn-outline-accent">Volver al inicio</Link>
      </section>
    )
  }

  const orden = ordenesDeUsuario(sesion).find(o => o.id === Number(id))

  if (!orden) {
    return (
      <section className="container py-5 text-center">
        <p className="text-muted">No encontramos esa compra en tu historial.</p>
        <Link to="/mis-compras" className="btn btn-accent">Volver a mis compras</Link>
      </section>
    )
  }

  return (
    <section className="container py-5">
      <div className="row justify-content-center">
        <div className="col-lg-9">
          <Link to="/mis-compras" className="btn btn-outline-accent btn-sm mb-3 no-print">
            <i className="bi bi-arrow-left me-1"></i>Volver a mis compras
          </Link>
          <div className="bg-ff-panel rounded p-4">
            <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
              <div>
                <h1 className="h4 mb-0">Compra #{orden.numero}</h1>
                <p className="small text-muted mb-0">Detalle de tu pedido en FreshFits.</p>
              </div>
              <span className={"badge fs-6 " + (orden.estado === "Pagada" ? "bg-success" : "bg-danger")}>{orden.estado}</span>
            </div>
            {orden.envio?.medio && <p className="small text-muted">Medio de pago: {orden.envio.medio}</p>}
            <Boleta orden={orden} />
            <div className="border rounded p-3 text-center mb-3">
              <span className="fs-5 fw-bold">Total del pedido: {plata(orden.total)}</span>
            </div>
            <div className="d-flex flex-wrap gap-2 no-print">
              <button className="btn btn-danger" onClick={() => window.print()}><i className="bi bi-printer me-1"></i>Imprimir / Guardar PDF</button>
              <Link to="/productos" className="btn btn-outline-accent">Seguir comprando</Link>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}
