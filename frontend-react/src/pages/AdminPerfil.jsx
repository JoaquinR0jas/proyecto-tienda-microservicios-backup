import { useEffect, useState } from 'react'
import { KEYS, getColeccion, setColeccion } from '../datos.js'
import { passOk, textoOk } from '../validaciones.js'
import { AdminLayout, useAdmin } from '../components/AdminMenu.jsx'
import { aviso } from '../alertas.js'

export default function AdminPerfil() {
  const sesion = useAdmin()
  const [regiones] = useState(() => getColeccion(KEYS.regiones))
  const [comunas, setComunas] = useState([])
  const [form, setForm] = useState(() => {
    const u = sesion && getColeccion(KEYS.usuarios).find(x => x.run === sesion.run)
    return u
      ? { nombre: u.nombre, apellidos: u.apellidos, direccion: u.direccion, region: u.regionId, comuna: u.comunaId, pass: "", pass2: "" }
      : null
  })

  useEffect(() => {
    if (form) setComunas(getColeccion(KEYS.comunas).filter(c => c.regionId === Number(form.region)))
  }, [form?.region])

  if (!sesion || !form) return null
  const rol = getColeccion(KEYS.roles).find(r => r.id === sesion.rolId)

  function cambiar(e) {
    const nuevo = { ...form, [e.target.name]: e.target.value }
    if (e.target.name === "region") nuevo.comuna = ""
    setForm(nuevo)
  }

  function guardar(e) {
    e.preventDefault()
    if (!textoOk(form.nombre, 50) || !textoOk(form.apellidos, 100) || !textoOk(form.direccion, 300) || !form.comuna) {
      aviso("Revisa el formulario", "Completa nombre, apellidos, direccion y comuna.", "error")
      return
    }
    if (form.pass && (!passOk(form.pass) || form.pass !== form.pass2)) {
      aviso("Revisa el formulario", "La contrasena debe tener entre 4 y 10 caracteres y coincidir.", "error")
      return
    }
    const users = getColeccion(KEYS.usuarios).map(u => {
      if (u.run !== sesion.run) return u
      return {
        ...u, nombre: form.nombre.trim(), apellidos: form.apellidos.trim(), direccion: form.direccion.trim(),
        regionId: Number(form.region), comunaId: Number(form.comuna),
        password: form.pass ? form.pass : u.password
      }
    })
    setColeccion(KEYS.usuarios, users)
    localStorage.setItem(KEYS.sesion, JSON.stringify({ ...sesion, nombre: form.nombre.trim(), apellidos: form.apellidos.trim() }))
    window.dispatchEvent(new Event("sesion-cambio"))
    setForm({ ...form, pass: "", pass2: "" })
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
