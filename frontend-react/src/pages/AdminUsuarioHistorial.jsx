import { Link, useParams } from 'react-router-dom'
import { plata, fechaCorta } from '../datos.js'
import { obtenerUsuarioPorRun, ordenesDeUsuario } from '../api.js'
import { AdminLayout, useAdmin } from '../components/AdminMenu.jsx'
import Cargando, { useCarga } from '../components/Cargando.jsx'

export default function AdminUsuarioHistorial() {
  const { run } = useParams()
  const sesion = useAdmin(true)
  const carga = useCarga(async () => {
    const usuario = await obtenerUsuarioPorRun(run)
    const ordenes = usuario ? await ordenesDeUsuario(usuario.id) : []
    return { usuario, ordenes }
  }, [run])
  if (!sesion) return null

  const u = carga.datos ? carga.datos.usuario : null
  const ordenes = carga.datos ? [...carga.datos.ordenes].sort((a, b) => b.fecha.localeCompare(a.fecha)) : []
  const gastado = ordenes.filter(o => o.estado === "Pagada").reduce((s, o) => s + o.total, 0)

  return (
    <AdminLayout>
      <Cargando cargando={carga.cargando} error={carga.error} onReintentar={carga.recargar} />
      {carga.cargando || carga.error ? null : !u ? (
        <p className="text-muted">Usuario no encontrado. <Link to="/admin/usuarios">Volver</Link></p>
      ) : (
        <>
          <h1 className="section-title mb-1">Historial de compras</h1>
          <p className="text-muted">{u.nombre} {u.apellidos} - Total gastado: {plata(gastado)}</p>
          <div className="table-responsive bg-ff-panel rounded p-2">
            <table className="table table-striped mb-0 align-middle">
              <thead><tr><th>Numero</th><th>Fecha</th><th>Productos</th><th>Total</th><th>Estado</th><th></th></tr></thead>
              <tbody>
                {ordenes.length === 0 && <tr><td colSpan="6" className="text-center text-muted">Este usuario no tiene compras.</td></tr>}
                {ordenes.map(o => (
                  <tr key={o.id}>
                    <td>#{o.numero}</td>
                    <td>{fechaCorta(o.fecha)}</td>
                    <td className="small">{o.items.map(i => i.cantidad + "x " + i.nombre).join(", ")}</td>
                    <td>{plata(o.total)}</td>
                    <td><span className={"badge " + (o.estado === "Pagada" ? "bg-success" : "bg-danger")}>{o.estado}</span></td>
                    <td><Link to={"/admin/ordenes/" + o.id} className="btn btn-sm btn-outline-accent">Boleta</Link></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
          <Link to={"/admin/usuarios/ver/" + u.run} className="btn btn-outline-accent mt-3">Volver al usuario</Link>
        </>
      )}
    </AdminLayout>
  )
}
