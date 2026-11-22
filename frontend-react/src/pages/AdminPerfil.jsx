import { useEffect, useState } from 'react'
import { getReferencias, guardarSesion, nombreRol } from '../datos.js'
import { obtenerUsuario, actualizarUsuario } from '../api.js'
import { passOk, textoOk } from '../validaciones.js'
import { AdminLayout, useAdmin } from '../components/AdminMenu.jsx'
import { aviso } from '../alertas.js'
import Cargando, { useCarga } from '../components/Cargando.jsx'

export default function AdminPerfil() {
  const sesion = useAdmin()
  const [regiones] = useState(() => getReferencias().regiones)
  const [comunas, setComunas] = useState([])
  const [form, setForm] = useState(null)
  // los datos de la cuenta vienen de ms-usuarios
  const carga = useCarga(async () => (sesion ? obtenerUsuario(sesion.id) : null))

  useEffect(() => {
    const u = carga.datos
    if (u) setForm({ nombre: u.nombre, apellidos: u.apellidos, direccion: u.direccion, region: u.regionId, comuna: u.comunaId, pass: "", pass2: "" })
  }, [carga.datos])

  useEffect(() => {
    if (form) setComunas(getReferencias().comunas.filter(c => c.regionId === Number(form.region)))
  }, [form?.region])

  if (!sesion) return null
  if (!form) {
    return (
      <AdminLayout>
        <h1 className="section-title">Perfil</h1>
        <Cargando cargando={carga.cargando} error={carga.error} onReintentar={carga.recargar} />
      </AdminLayout>
    )
  }
  const rol = { nombre: nombreRol(sesion.rolId) }

  function cambiar(e) {
    const nuevo = { ...form, [e.target.name]: e.target.value }
    if (e.target.name === "region") nuevo.comuna = ""
    setForm(nuevo)
  }

  async function guardar(e) {
    e.preventDefault()
    if (!textoOk(form.nombre, 50) || !textoOk(form.apellidos, 100) || !textoOk(form.direccion, 300) || !form.comuna) {
      aviso("Revisa el formulario", "Completa nombre, apellidos, direccion y comuna.", "error")
      return
    }
    if (form.pass && (!passOk(form.pass) || form.pass !== form.pass2)) {
      aviso("Revisa el formulario", "La contrasena debe tener entre 4 y 10 caracteres y coincidir.", "error")
      return
    }
    const u = carga.datos
    try {
      await actualizarUsuario(u.id, {
        ...u, nombre: form.nombre.trim(), apellidos: form.apellidos.trim(), direccion: form.direccion.trim(),
        regionId: Number(form.region), comunaId: Number(form.comuna),
        password: form.pass ? form.pass : u.password
      })
    } catch (err) {
      aviso("No se pudo guardar", err.message, "error")
      return
    }
    guardarSesion({ ...sesion, nombre: form.nombre.trim(), apellidos: form.apellidos.trim() })
    setForm({ ...form, pass: "", pass2: "" })
    carga.recargar()
    aviso("Correcto", "Perfil actualizado correctamente.")
  }

  return (
    <AdminLayout>
      <h1 className="section-title">Perfil</h1>
      <form onSubmit={guardar} className="bg-ff-panel rounded p-4">
        <p className="text-muted">{sesion.correo} - {rol ? rol.nombre : ""} - RUN {sesion.run}</p>
        <div className="row g-3">
          <div className="col-md-6">
            <label className="form-label">Nombre</label>
            <input name="nombre" className="form-control" value={form.nombre} onChange={cambiar} />
          </div>
          <div className="col-md-6">
            <label className="form-label">Apellidos</label>
            <input name="apellidos" className="form-control" value={form.apellidos} onChange={cambiar} />
          </div>
          <div className="col-md-4">
            <label className="form-label">Region</label>
            <select name="region" className="form-select" value={form.region} onChange={cambiar}>
              {regiones.map(r => <option key={r.id} value={r.id}>{r.nombre}</option>)}
            </select>
          </div>
          <div className="col-md-4">
            <label className="form-label">Comuna</label>
            <select name="comuna" className="form-select" value={form.comuna} onChange={cambiar}>
              <option value="">Seleccione...</option>
              {comunas.map(c => <option key={c.id} value={c.id}>{c.nombre}</option>)}
            </select>
          </div>
          <div className="col-md-4">
            <label className="form-label">Direccion</label>
            <input name="direccion" className="form-control" value={form.direccion} onChange={cambiar} />
          </div>
          <div className="col-md-6">
            <label className="form-label">Nueva contrasena (opcional)</label>
            <input name="pass" type="password" className="form-control" maxLength="10" value={form.pass} onChange={cambiar} />
          </div>
          <div className="col-md-6">
            <label className="form-label">Repetir contrasena</label>
            <input name="pass2" type="password" className="form-control" maxLength="10" value={form.pass2} onChange={cambiar} />
          </div>
        </div>
        <button type="submit" className="btn btn-accent mt-3">Guardar cambios</button>
      </form>
    </AdminLayout>
  )
}
