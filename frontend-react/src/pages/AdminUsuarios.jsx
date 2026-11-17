import { Link } from 'react-router-dom'
import { nombreRegion, nombreRol } from '../datos.js'
import { listarUsuarios, eliminarUsuario } from '../api.js'
import { AdminLayout, useAdmin } from '../components/AdminMenu.jsx'
import { aviso, confirmar } from '../alertas.js'
import Cargando, { useCarga } from '../components/Cargando.jsx'

export default function AdminUsuarios() {
  const sesion = useAdmin(true)
  const carga = useCarga(listarUsuarios) // usuarios de ms-usuarios
  if (!sesion) return null

  const users = carga.datos || []

  async function eliminar(run) {
    if (run === sesion.run) {
      aviso("No permitido", "No puedes eliminar tu propia cuenta.", "error")
      return
    }
    if (!await confirmar("Eliminar usuario?", "Esta accion no se puede deshacer.")) return
    try {
      await eliminarUsuario(users.find(u => u.run === run).id)
    } catch (e) {
      aviso("No se pudo eliminar", e.message, "error")
      return
    }
    carga.recargar()
    aviso("Eliminado", "El usuario fue eliminado correctamente.")
  }

  return (
    <AdminLayout>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h1 className="section-title mb-0">Usuarios</h1>
        <Link to="/admin/usuarios/nuevo" className="btn btn-accent btn-sm">+ Nuevo usuario</Link>
      </div>
      <Cargando cargando={carga.cargando} error={carga.error} onReintentar={carga.recargar} />
      <div className="table-responsive bg-ff-panel rounded p-2">
        <table className="table table-striped mb-0">
          <thead>
            <tr>
              <th>RUN</th>
              <th>Nombre</th>
              <th>Correo</th>
              <th>Rol</th>
              <th>Region</th>
              <th>Estado</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {users.map(u => (
              <tr key={u.run}>
                <td>{u.run}</td>
                <td>{u.nombre} {u.apellidos}</td>
                <td>{u.correo}</td>
                <td>{nombreRol(u.rolId)}</td>
                <td>{nombreRegion(u.regionId)}</td>
                <td><span className={"badge " + (u.estado === "Activo" ? "bg-success" : "bg-secondary")}>{u.estado}</span></td>
                <td className="text-nowrap">
                  <Link to={"/admin/usuarios/ver/" + u.run} className="btn btn-sm btn-outline-accent me-1"><i className="bi bi-eye"></i></Link>
                  <Link to={"/admin/usuarios/" + u.run} className="btn btn-sm btn-outline-dark me-1"><i className="bi bi-pencil"></i></Link>
                  <button className="btn btn-sm btn-outline-danger" onClick={() => eliminar(u.run)}><i className="bi bi-trash"></i></button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </AdminLayout>
  )
}
