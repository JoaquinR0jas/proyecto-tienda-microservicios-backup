import { Link, useNavigate, useParams } from 'react-router-dom'
import { KEYS, getColeccion } from '../datos.js'
import { AdminLayout, useAdmin } from '../components/AdminMenu.jsx'
import Boleta from '../components/Boleta.jsx'

export default function AdminBoleta() {
  const { id } = useParams()
  const nav = useNavigate()
  const sesion = useAdmin()
  if (!sesion) return null

  const orden = getColeccion(KEYS.ordenes).find(o => o.id === Number(id))

  return (
    <AdminLayout>
      {!orden ? (
        <p className="text-muted">Orden no encontrada. <Link to="/admin/ordenes">Volver</Link></p>
      ) : (
        <div className="bg-ff-panel rounded p-4">
          <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
            <h1 className="h4 mb-0">Boleta #{orden.numero}</h1>
            <span className={"badge fs-6 " + (orden.estado === "Pagada" ? "bg-success" : "bg-danger")}>{orden.estado}</span>
          </div>
          {orden.envio.medio && <p className="small text-muted">Medio de pago: {orden.envio.medio}</p>}
          <Boleta orden={orden} />
          <div className="d-flex gap-2 no-print">
            <button className="btn btn-outline-accent" onClick={() => nav(-1)}>Volver</button>
            <button className="btn btn-danger" onClick={() => window.print()}><i className="bi bi-printer"></i> Imprimir</button>
          </div>
        </div>
      )}
    </AdminLayout>
  )
}
