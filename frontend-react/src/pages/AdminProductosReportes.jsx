import { Link } from 'react-router-dom'
import { KEYS, getColeccion, plata, nombreCategoria } from '../datos.js'
import { AdminLayout, useAdmin } from '../components/AdminMenu.jsx'
import Barra from '../components/Barra.jsx'

export default function AdminProductosReportes() {
  const sesion = useAdmin()
  if (!sesion) return null

  const prods = getColeccion(KEYS.productos)
  const pagadas = getColeccion(KEYS.ordenes).filter(o => o.estado === "Pagada")

  // unidades e ingresos por producto
  const ventas = prods.map(p => {
    let unidades = 0
    let ingresos = 0
    pagadas.forEach(o => o.items.filter(i => i.codigo === p.codigo).forEach(i => {
      unidades += i.cantidad
      ingresos += i.precio * i.cantidad
    }))
    return { ...p, unidades, ingresos, valorStock: p.stock * p.precio }
  }).sort((a, b) => b.unidades - a.unidades)

  const maxUnidades = Math.max(0, ...ventas.map(v => v.unidades))
  const valorInventario = ventas.reduce((s, v) => s + v.valorStock, 0)

  const porCategoria = {}
  ventas.forEach(v => {
    const nombre = nombreCategoria(v.categoriaId)
    porCategoria[nombre] = (porCategoria[nombre] || 0) + v.stock
  })
  const maxCat = Math.max(0, ...Object.values(porCategoria))

  return (
    <AdminLayout>
      <h1 className="section-title">Reportes de productos</h1>
      <div className="row g-3 mb-4">
        <div className="col-md-4"><div className="card p-3"><small className="text-muted">Productos</small><span className="fs-4 fw-bold">{prods.length}</span></div></div>
        <div className="col-md-4"><div className="card p-3"><small className="text-muted">Unidades en stock</small><span className="fs-4 fw-bold">{prods.reduce((s, p) => s + p.stock, 0)}</span></div></div>
        <div className="col-md-4"><div className="card p-3"><small className="text-muted">Valor del inventario</small><span className="fs-4 fw-bold">{plata(valorInventario)}</span></div></div>
      </div>
      <div className="row g-4">
        <div className="col-lg-6">
          <div className="card p-4 h-100">
            <h2 className="h6 mb-3">Unidades vendidas por producto</h2>
            {ventas.map(v => <Barra key={v.codigo} etiqueta={v.nombre} valor={v.unidades} maximo={maxUnidades} />)}
          </div>
        </div>
        <div className="col-lg-6">
          <div className="card p-4 h-100">
            <h2 className="h6 mb-3">Stock por categoria</h2>
            {Object.entries(porCategoria).map(([nombre, stock]) => <Barra key={nombre} etiqueta={nombre} valor={stock} maximo={maxCat} />)}
          </div>
        </div>
        <div className="col-12">
          <div className="table-responsive bg-ff-panel rounded p-2">
            <table className="table table-striped mb-0">
              <thead><tr><th>Producto</th><th>Vendidas</th><th>Ingresos</th><th>Stock</th><th>Valor stock</th></tr></thead>
              <tbody>
                {ventas.map(v => (
                  <tr key={v.codigo}>
                    <td>{v.nombre}</td><td>{v.unidades}</td><td>{plata(v.ingresos)}</td><td>{v.stock}</td><td>{plata(v.valorStock)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
      <Link to="/admin/productos" className="btn btn-outline-accent mt-3">Volver a productos</Link>
    </AdminLayout>
  )
}
