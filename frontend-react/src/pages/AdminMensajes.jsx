import { useState } from 'react'
import { fechaCorta } from '../datos.js'
import { listarMensajes, eliminarMensaje } from '../api.js'
import Cargando, { useCarga } from '../components/Cargando.jsx'
import { AdminLayout, useAdmin } from '../components/AdminMenu.jsx'
import { aviso, confirmar } from '../alertas.js'

export default function AdminMensajes() {
  const sesion = useAdmin()
  const carga = useCarga(listarMensajes) // mensajes guardados en ms-contenido
  const [busqueda, setBusqueda] = useState("")
  if (!sesion) return null

  async function eliminar(id) {
    if (!await confirmar("Eliminar mensaje?", "Esta accion no se puede deshacer.")) return
    try {
      await eliminarMensaje(id)
    } catch (e) {
      aviso("No se pudo eliminar", e.message, "error")
      return
    }
    carga.recargar()
    aviso("Eliminado", "El mensaje fue eliminado correctamente.")
  }

  const texto = busqueda.toLowerCase()
  const lista = (carga.datos || [])
    .filter(m => !texto || (m.nombre + " " + m.correo + " " + m.comentario).toLowerCase().includes(texto))
    .sort((a, b) => b.fecha.localeCompare(a.fecha) || b.id - a.id)

  return (
    <AdminLayout>
      <h1 className="section-title">Mensajes de contacto</h1>
      <p className="text-muted">Mensajes enviados desde el formulario de Contacto de la tienda.</p>
      <input className="form-control mb-3" placeholder="Buscar por nombre, correo o comentario..." value={busqueda} onChange={e => setBusqueda(e.target.value)} />
      <Cargando cargando={carga.cargando} error={carga.error} onReintentar={carga.recargar} />
      <div className="table-responsive bg-ff-panel rounded p-2">
        <table className="table table-striped mb-0 align-middle">
          <thead>
            <tr><th>Fecha</th><th>Nombre</th><th>Correo</th><th>Comentario</th><th></th></tr>
          </thead>
          <tbody>
            {lista.length === 0 && <tr><td colSpan="5" className="text-center text-muted">No hay mensajes.</td></tr>}
            {lista.map(m => (
              <tr key={m.id}>
                <td className="text-nowrap">{fechaCorta(m.fecha)}</td>
                <td>{m.nombre}</td>
                <td><a href={"mailto:" + m.correo}>{m.correo}</a></td>
                <td className="small" style={{ whiteSpace: "pre-wrap" }}>{m.comentario}</td>
                <td>
                  <button className="btn btn-sm btn-outline-danger" onClick={() => eliminar(m.id)}><i className="bi bi-trash"></i></button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </AdminLayout>
  )
}
