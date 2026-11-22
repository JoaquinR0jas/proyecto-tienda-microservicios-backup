import { useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { getReferencias } from '../datos.js'
import { crearCategoria, actualizarCategoria, cargarReferencias } from '../api.js'
import { textoOk } from '../validaciones.js'
import { AdminLayout, useAdmin } from '../components/AdminMenu.jsx'
import { aviso } from '../alertas.js'

export default function AdminCategoriaForm() {
  const { id } = useParams()
  const nav = useNavigate()
  const sesion = useAdmin(true)
  const editando = !!id
  const [form, setForm] = useState(() => {
    const c = id && getReferencias().categorias.find(x => x.id === Number(id))
    return c ? { nombre: c.nombre, descripcion: c.descripcion || "" } : { nombre: "", descripcion: "" }
  })
  const [error, setError] = useState("")
  const [validado, setValidado] = useState(false)
  if (!sesion) return null

  function cambiar(e) {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  async function guardar(e) {
    e.preventDefault()
    const todas = getReferencias().categorias
    let err = ""
    if (!textoOk(form.nombre, 50)) err = "El nombre es obligatorio (maximo 50 caracteres)."
    else if (todas.some(c => c.nombre.toLowerCase() === form.nombre.trim().toLowerCase() && c.id !== Number(id))) err = "Ya existe una categoria con ese nombre."
    setError(err)
    setValidado(true)
    if (err) {
      aviso("Revisa el formulario", err, "error")
      return
    }
    const datos = { nombre: form.nombre.trim(), descripcion: form.descripcion.trim() }
    try {
      if (editando) await actualizarCategoria(id, datos)
      else await crearCategoria(datos)
    } catch (e2) {
      setError(e2.message)
      aviso("No se pudo guardar", e2.message, "error")
      return
    }
    await cargarReferencias() // para que la tienda vea la categoria nueva o con su nuevo nombre
    await aviso("Correcto", "Categoria guardada correctamente.")
    nav("/admin/categorias")
  }

  return (
    <AdminLayout>
      <h1 className="section-title">{editando ? "Editar categoria" : "Nueva categoria"}</h1>
      <form onSubmit={guardar} className="bg-ff-panel rounded p-4">
        <div className="mb-3">
          <label htmlFor="nombreCategoria" className="form-label">Nombre</label>
          <input id="nombreCategoria" name="nombre" className={"form-control" + (validado ? (error ? " is-invalid" : " is-valid") : "")} maxLength="50" value={form.nombre} onChange={cambiar} />
          <div className="invalid-feedback">{error}</div>
        </div>
        <div className="mb-3">
          <label className="form-label">Descripcion</label>
          <textarea name="descripcion" className="form-control" rows="2" value={form.descripcion} onChange={cambiar}></textarea>
        </div>
        <div className="d-flex gap-2">
          <button type="submit" className="btn btn-accent">Guardar</button>
          <Link to="/admin/categorias" className="btn btn-outline-accent">Volver</Link>
        </div>
      </form>
    </AdminLayout>
  )
}
