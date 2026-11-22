import { Link, useParams } from 'react-router-dom'
import { plata, imgProducto, nombreCategoria, precioFinal } from '../datos.js'
import { obtenerProductoPorCodigo, listarOrdenes } from '../api.js'
import Cargando, { useCarga } from '../components/Cargando.jsx'
import { AdminLayout, useAdmin } from '../components/AdminMenu.jsx'

export default function AdminProductoVer() {
  const { codigo } = useParams()
  const sesion = useAdmin()
  // el producto viene de ms-catalogo y las ventas de ms-pedidos
  const carga = useCarga(async () => {
    const [producto, ordenes] = await Promise.all([obtenerProductoPorCodigo(codigo), listarOrdenes()])
    return { producto, ordenes }
  }, [codigo])
  if (!sesion) return null

  const p = carga.datos ? carga.datos.producto : null
  // unidades vendidas segun las ordenes pagadas
  const vendidas = (carga.datos ? carga.datos.ordenes : [])
    .filter(o => o.estado === "Pagada")
    .reduce((s, o) => s + o.items.filter(i => i.codigo === codigo).reduce((a, i) => a + i.cantidad, 0), 0)

  return (
    <AdminLayout>
      <Cargando cargando={carga.cargando} error={carga.error} onReintentar={carga.recargar} />
      {carga.cargando || carga.error ? null : !p ? (
        <p className="text-muted">Producto no encontrado. <Link to="/admin/productos">Volver</Link></p>
      ) : (
        <>
          <h1 className="section-title">Mostrar producto</h1>
          <div className="bg-ff-panel rounded p-4">
            <div className="row g-4">
              <div className="col-md-4">
                <img src={imgProducto(p)} alt={p.nombre} className="img-fluid rounded" />
              </div>
              <div className="col-md-8">
                <h2 className="h4">{p.nombre}</h2>
                <p className="text-muted">{p.descripcion}</p>
                <dl className="row mb-0">
                  <dt className="col-sm-4">Codigo</dt><dd className="col-sm-8">{p.codigo}</dd>
                  <dt className="col-sm-4">Categoria</dt><dd className="col-sm-8">{nombreCategoria(p.categoriaId)}</dd>
                  <dt className="col-sm-4">Precio</dt><dd className="col-sm-8">{plata(p.precio)}</dd>
                  <dt className="col-sm-4">Descuento</dt><dd className="col-sm-8">{p.descuento || 0}% (precio final {plata(precioFinal(p))})</dd>
                  <dt className="col-sm-4">Stock</dt>
                  <dd className={"col-sm-8" + (p.stockCritico != null && p.stock <= p.stockCritico ? " stock-critico" : "")}>{p.stock} (critico: {p.stockCritico ?? "-"})</dd>
                  <dt className="col-sm-4">Unidades vendidas</dt><dd className="col-sm-8">{vendidas}</dd>
                  <dt className="col-sm-4">Estado</dt><dd className="col-sm-8">{p.estado}</dd>
                  <dt className="col-sm-4">Destacado</dt><dd className="col-sm-8">{p.destacado ? "Si" : "No"}</dd>
                </dl>
              </div>
            </div>
            <div className="d-flex gap-2 mt-4">
              {sesion.rolId === 1 && <Link to={"/admin/productos/" + p.codigo} className="btn btn-accent"><i className="bi bi-pencil"></i> Editar producto</Link>}
              <Link to="/admin/productos" className="btn btn-outline-accent">Volver</Link>
            </div>
          </div>
        </>
      )}
    </AdminLayout>
  )
}
