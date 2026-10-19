import { useState } from 'react'
import { Link } from 'react-router-dom'
import { KEYS, getColeccion, setColeccion } from '../datos.js'
import { AdminLayout, useAdmin } from '../components/AdminMenu.jsx'
import { aviso, confirmar } from '../alertas.js'

export default function AdminCategorias() {
  const sesion = useAdmin(true)
  const [cats, setCats] = useState(() => getColeccion(KEYS.categorias))
  if (!sesion) return null

  const prods = getColeccion(KEYS.productos)

  async function eliminar(c) {
    if (prods.some(p => p.categoriaId === c.id)) {
      aviso("No se puede eliminar", "Hay productos en la categoria " + c.nombre + ".", "error")
      return
    }
    if (!await confirmar("Eliminar categoria?", "Se eliminara la categoria " + c.nombre + ".")) return
    setColeccion(KEYS.categorias, getColeccion(KEYS.categorias).filter(x => x.id !== c.id))
    setCats(getColeccion(KEYS.categorias))
    aviso("Eliminada", "La categoria fue eliminada correctamente.")
  }

  return (
    <AdminLayout>
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h1 className="section-title mb-0">Categorias</h1>
        <Link to="/admin/categorias/nueva" className="btn btn-accent btn-sm">+ Nueva categoria</Link>
      </div>
      <div className="table-responsive bg-ff-panel rounded p-2">
        <table className="table table-striped mb-0">
          <thead><tr><th>ID</th><th>Nombre</th><th>Descripcion</th><th>Productos</th><th></th></tr></thead>
          <tbody>
            {cats.map(c => (
              <tr key={c.id}>
                <td>{c.id}</td>
                <td>{c.nombre}</td>
                <td className="small text-muted">{c.descripcion}</td>
                <td>{prods.filter(p => p.categoriaId === c.id).length}</td>
                <td className="text-nowrap">
                  <Link to={"/admin/categorias/" + c.id} className="btn btn-sm btn-outline-dark me-1"><i className="bi bi-pencil"></i></Link>
                  <button className="btn btn-sm btn-outline-danger" onClick={() => eliminar(c)}><i className="bi bi-trash"></i></button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </AdminLayout>
  )
}
