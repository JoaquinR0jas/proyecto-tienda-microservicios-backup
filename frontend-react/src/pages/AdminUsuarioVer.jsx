import { Link, useParams } from 'react-router-dom'
import { plata, nombreRegion, nombreComuna, nombreRol } from '../datos.js'
import { obtenerUsuarioPorRun, ordenesDeUsuario } from '../api.js'
import { AdminLayout, useAdmin } from '../components/AdminMenu.jsx'
import Cargando, { useCarga } from '../components/Cargando.jsx'

export default function AdminUsuarioVer() {
  const { run } = useParams()
  const sesion = useAdmin(true)
  // el usuario viene de ms-usuarios y sus compras de ms-pedidos
  const carga = useCarga(async () => {
    const usuario = await obtenerUsuarioPorRun(run)
    const ordenes = usuario ? await ordenesDeUsuario(usuario.id) : []
    return { usuario, ordenes }
  }, [run])
  if (!sesion) return null

  const u = carga.datos ? carga.datos.usuario : null
  const rol = u && { nombre: nombreRol(u.rolId) }
  const compras = carga.datos ? carga.datos.ordenes.filter(o => o.estado === "Pagada") : []

  return (
    <AdminLayout>
      <Cargando cargando={carga.cargando} error={carga.error} onReintentar={carga.recargar} />
      {carga.cargando || carga.error ? null : !u ? (
        <p className="text-muted">Usuario no encontrado. <Link to="/admin/usuarios">Volver</Link></p>
      ) : (
        <>
          <h1 className="section-title">Mostrar usuario</h1>
          <div className="bg-ff-panel rounded p-4">
            <h2 className="h4">{u.nombre} {u.apellidos}</h2>
            <dl className="row mb-0">
              <dt className="col-sm-4">RUN</dt><dd className="col-sm-8">{u.run}</dd>
              <dt className="col-sm-4">Correo</dt><dd className="col-sm-8">{u.correo}</dd>
              <dt className="col-sm-4">Fecha nacimiento</dt><dd className="col-sm-8">{u.fechaNacimiento || "-"}</dd>
              <dt className="col-sm-4">Rol</dt><dd className="col-sm-8">{rol ? rol.nombre : "-"}</dd>
              <dt className="col-sm-4">Direccion</dt><dd className="col-sm-8">{u.direccion}, {nombreComuna(u.comunaId)}, {nombreRegion(u.regionId)}</dd>
              <dt className="col-sm-4">Estado</dt><dd className="col-sm-8">{u.estado}</dd>
              <dt className="col-sm-4">Compras realizadas</dt><dd className="col-sm-8">{compras.length} ({plata(compras.reduce((s, o) => s + o.total, 0))})</dd>
            </dl>
            <div className="d-flex flex-wrap gap-2 mt-4">
              <Link to={"/admin/usuarios/" + u.run} className="btn btn-accent"><i className="bi bi-pencil"></i> Editar usuario</Link>
              <Link to={"/admin/usuarios/" + u.run + "/historial"} className="btn btn-success"><i className="bi bi-clock-history"></i> Historial de compras</Link>
              <Link to="/admin/usuarios" className="btn btn-outline-accent">Volver</Link>
            </div>
          </div>
        </>
      )}
    </AdminLayout>
  )
}
