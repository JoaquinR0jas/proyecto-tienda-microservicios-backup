import { Link, useParams } from 'react-router-dom'
import { KEYS, getColeccion, plata } from '../datos.js'
import Boleta from '../components/Boleta.jsx'
import { aviso } from '../alertas.js'

export default function CompraExitosa() {
  const { id } = useParams()
  const orden = getColeccion(KEYS.ordenes).find(o => o.id === Number(id) && o.estado === "Pagada")

  if (!orden) {
    return (
      <section className="container py-5 text-center">
        <p className="text-muted">No encontramos esa compra.</p>
        <Link to="/" className="btn btn-accent">Volver al inicio</Link>
      </section>
    )
  }

  function enviarCorreo() {
    aviso("Boleta enviada", "La boleta fue enviada a " + orden.cliente.correo + ".")
  }

  return (
    <section className="container py-5">
      <div className="row justify-content-center">
        <div className="col-lg-9">
          <div className="bg-ff-panel rounded p-4">
            <h1 className="h4 text-success">
              <i className="bi bi-check-circle"></i> Se ha realizado la compra nro #{orden.numero}
            </h1>
            <p className="small text-muted">Gracias por comprar en FreshFits, este es el resumen de tu pedido.</p>
            <Boleta orden={orden} />
            <div className="border rounded p-3 text-center mb-3">
              <span className="fs-5 fw-bold">Total pagado: {plata(orden.total)}</span>
            </div>
            <div className="d-flex flex-wrap justify-content-center gap-2 no-print">
              <button className="btn btn-danger btn-sm" onClick={() => window.print()}><i className="bi bi-file-earmark-pdf"></i> Imprimir boleta en PDF</button>
              <button className="btn btn-success btn-sm" onClick={enviarCorreo}><i className="bi bi-envelope"></i> Enviar boleta por email</button>
              <Link to="/productos" className="btn btn-outline-accent btn-sm">Seguir comprando</Link>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}
