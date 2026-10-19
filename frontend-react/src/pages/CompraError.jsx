import { Link, useLocation, useParams } from 'react-router-dom'
import { KEYS, getColeccion } from '../datos.js'
import Boleta from '../components/Boleta.jsx'

export default function CompraError() {
  const { id } = useParams()
  const motivo = useLocation().state?.motivo
  const orden = getColeccion(KEYS.ordenes).find(o => o.id === Number(id) && o.estado === "Rechazada")

  if (!orden) {
    return (
      <section className="container py-5 text-center">
        <p className="text-muted">No encontramos ese intento de pago.</p>
        <Link to="/carrito" className="btn btn-accent">Ir al carrito</Link>
      </section>
    )
  }

  return (
    <section className="container py-5">
      <div className="row justify-content-center">
        <div className="col-lg-9">
          <div className="bg-ff-panel rounded p-4">
            <h1 className="h4 text-danger">
              <i className="bi bi-x-circle"></i> No se pudo realizar el pago nro #{orden.numero}
            </h1>
            <div className="alert alert-danger py-2">
              {motivo || "El pago fue rechazado"}. Tus productos siguen en el carrito, no se realizo ningun cobro.
            </div>
            <Boleta orden={orden} />
            <div className="d-flex flex-wrap justify-content-center gap-2">
              <Link to="/checkout" className="btn btn-success">Volver a realizar el pago</Link>
              <Link to="/carrito" className="btn btn-outline-accent">Revisar carrito</Link>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}
