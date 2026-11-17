import { Link, useLocation, useParams } from 'react-router-dom'
import { getSesion, plata } from '../datos.js'
import { obtenerOrden } from '../api.js'
import Cargando, { useCarga } from '../components/Cargando.jsx'
import Boleta from '../components/Boleta.jsx'
import { aviso } from '../alertas.js'

export default function CompraExitosa() {
  const { id } = useParams()
  const sesion = getSesion()
  const desdeCheckout = useLocation().state?.orden
  // si viene del checkout ya trae la orden; si se recarga la pagina se pide a ms-pedidos
  const carga = useCarga(async () => desdeCheckout || await obtenerOrden(id), [id])
  const orden = carga.datos && carga.datos.estado === "Pagada" ? carga.datos : null

  if (carga.cargando || carga.error) {
    return (
      <section className="container py-5">
        <Cargando cargando={carga.cargando} error={carga.error} onReintentar={carga.recargar} />
      </section>
    )
  }

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
              {sesion && sesion.rolId === 3 && (
                <Link to="/mis-compras" className="btn btn-outline-dark btn-sm"><i className="bi bi-bag-check"></i> Ver mis compras</Link>
              )}
              <Link to="/productos" className="btn btn-outline-accent btn-sm">Seguir comprando</Link>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}
