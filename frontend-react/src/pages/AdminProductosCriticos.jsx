import { Link } from 'react-router-dom'
import { KEYS, getColeccion, nombreCategoria } from '../datos.js'
import { AdminLayout, useAdmin } from '../components/AdminMenu.jsx'

export default function AdminProductosCriticos() {
  const sesion = useAdmin()
  if (!sesion) return null

  const criticos = getColeccion(KEYS.productos)
    .filter(p => p.stockCritico != null && p.stock <= p.stockCritico)
    .sort((a, b) => a.stock - b.stock)

  return (
    <AdminLayout>
      <h1 className="section-title">Listado de productos criticos</h1>
      <p className="text-muted">Productos cuyo stock esta en o bajo el stock critico definido.</p>
      <div className="table-responsive bg-ff-panel rounded p-2">
        <table className="table table-striped mb-0">
          <thead>
            <tr><th>Codigo</th><th>Nombre</th><th>Categoria</th><th>Stock</th><th>Stock critico</th><th>Estado</th><th></th></tr>
          </thead>
          <tbody>
            {criticos.length === 0 && <tr><td colSpan="7" className="text-center text-muted">Sin productos criticos.</td></tr>}
            {criticos.map(p => (
              <tr key={p.codigo}>
                <td>{p.codigo}</td>
                <td>{p.nombre}</td>
                <td>{nombreCategoria(p.categoriaId)}</td>
                <td className="stock-critico">{p.stock}</td>
                <td>{p.stockCritico}</td>
                <td>{p.stock === 0 ? <span className="badge bg-danger">Agotado</span> : <span className="badge bg-warning text-dark">Stock bajo</span>}</td>
                <td className="text-nowrap">
                  <Link to={"/admin/productos/ver/" + p.codigo} className="btn btn-sm btn-outline-accent me-1"><i className="bi bi-eye"></i></Link>
                  {sesion.rolId === 1 && <Link to={"/admin/productos/" + p.codigo} className="btn btn-sm btn-outline-dark"><i className="bi bi-pencil"></i> Reponer</Link>}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
      <Link to="/admin/productos" className="btn btn-outline-accent mt-3">Volver a productos</Link>
    </AdminLayout>
  )
}
