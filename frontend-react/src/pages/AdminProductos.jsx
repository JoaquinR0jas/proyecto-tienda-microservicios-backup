import { useState } from 'react'
import { Link } from 'react-router-dom'
import { plata, nombreCategoria, precioFinal, enOferta } from '../datos.js'
import { listarProductos, eliminarProducto } from '../api.js'
import Cargando, { useCarga } from '../components/Cargando.jsx'
import { AdminLayout, useAdmin } from '../components/AdminMenu.jsx'
import { aviso, confirmar } from '../alertas.js'

export default function AdminProductos() {
  const sesion = useAdmin()
  const carga = useCarga(listarProductos) // todos los productos de ms-catalogo, activos e inactivos
  const [busqueda, setBusqueda] = useState("")
  if (!sesion) return null

  const soloVer = sesion.rolId === 2
  const prods = carga.datos || []

  async function eliminar(codigo) {
    if (!await confirmar("Eliminar producto?", "Esta accion no se puede deshacer.")) return
    try {
      await eliminarProducto(prods.find(p => p.codigo === codigo).id)
    } catch (e) {
      aviso("No se pudo eliminar", e.message, "error")
      return
    }
    carga.recargar()
    aviso("Eliminado", "El producto fue eliminado correctamente.")
  }

  const texto = busqueda.toLowerCase()
  const lista = prods.filter(p => !texto || p.nombre.toLowerCase().includes(texto) || p.codigo.toLowerCase().includes(texto))

  return (
    <AdminLayout>
      <div className="d-flex flex-wrap justify-content-between align-items-center gap-2 mb-3">
        <h1 className="section-title mb-0">Productos</h1>
        <div className="d-flex flex-wrap gap-2">
          <Link to="/admin/productos/criticos" className="btn btn-outline-danger btn-sm"><i className="bi bi-exclamation-triangle"></i> Productos criticos</Link>
          <Link to="/admin/productos/reportes" className="btn btn-outline-accent btn-sm"><i className="bi bi-bar-chart"></i> Reportes</Link>
          {!soloVer && <Link to="/admin/productos/nuevo" className="btn btn-accent btn-sm">+ Nuevo producto</Link>}
        </div>
      </div>
      <input className="form-control mb-3" placeholder="Buscar por nombre o codigo..." value={busqueda} onChange={e => setBusqueda(e.target.value)} />
      <Cargando cargando={carga.cargando} error={carga.error} onReintentar={carga.recargar} />
      <div className="table-responsive bg-ff-panel rounded p-2">
        <table className="table table-striped mb-0">
          <thead>
            <tr>
              <th>Codigo</th>
              <th>Nombre</th>
              <th>Categoria</th>
              <th>Precio</th>
              <th>Stock</th>
              <th>Estado</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {lista.map(p => (
              <tr key={p.codigo}>
                <td>{p.codigo}</td>
                <td>{p.nombre} {enOferta(p) && <span className="badge bg-danger">-{p.descuento}%</span>}</td>
                <td>{nombreCategoria(p.categoriaId)}</td>
                <td>{plata(precioFinal(p))}</td>
                <td className={p.stockCritico != null && p.stock <= p.stockCritico ? "stock-critico" : ""}>{p.stock}</td>
                <td><span className={"badge " + (p.estado === "Activo" ? "bg-success" : "bg-secondary")}>{p.estado}</span></td>
                <td className="text-nowrap">
                  <Link to={"/admin/productos/ver/" + p.codigo} className="btn btn-sm btn-outline-accent me-1"><i className="bi bi-eye"></i></Link>
                  {!soloVer && (
                    <>
                      <Link to={"/admin/productos/" + p.codigo} className="btn btn-sm btn-outline-dark me-1"><i className="bi bi-pencil"></i></Link>
                      <button className="btn btn-sm btn-outline-danger" onClick={() => eliminar(p.codigo)}><i className="bi bi-trash"></i></button>
                    </>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </AdminLayout>
  )
}
