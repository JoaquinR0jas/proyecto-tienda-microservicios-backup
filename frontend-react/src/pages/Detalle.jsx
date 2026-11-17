import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { plata, imgProducto, nombreCategoria, precioFinal, enOferta } from '../datos.js'
import { obtenerProductoPorCodigo } from '../api.js'
import Cargando, { useCarga } from '../components/Cargando.jsx'
import { agregarConAviso } from '../alertas.js'

export default function Detalle() {
  const { codigo } = useParams()
  const [cant, setCant] = useState(1)
  const carga = useCarga(() => obtenerProductoPorCodigo(codigo), [codigo])
  const p = carga.datos && carga.datos.estado === "Activo" ? carga.datos : null

  useEffect(() => {
    setCant(1)
  }, [codigo])

  if (carga.cargando || carga.error) {
    return (
      <section className="container py-5">
        <Cargando cargando={carga.cargando} error={carga.error} onReintentar={carga.recargar} />
      </section>
    )
  }

  if (!p) {
    return (
      <section className="container py-5">
        <p className="text-muted text-center">Prenda no encontrada.</p>
        <div className="text-center"><Link to="/productos" className="btn btn-accent">Volver al catalogo</Link></div>
      </section>
    )
  }

  const agotado = p.stock <= 0

  function agregar() {
    agregarConAviso(p, cant)
  }

  return (
    <section className="container py-5">
      <div className="row g-4">
        <div className="col-md-5">
          <img src={imgProducto(p)} className="img-fluid rounded bg-ff-panel p-2" alt={p.nombre} />
        </div>
        <div className="col-md-7">
          <span className="badge bg-secondary mb-2">{nombreCategoria(p.categoriaId)}</span>
          <h1 className="h3">{p.nombre}</h1>
          <p className="text-muted">{p.descripcion || "Sin descripcion disponible."}</p>
          {enOferta(p) ? (
            <p className="display-6">
              <span className="precio-tachado fs-4 me-2">{plata(p.precio)}</span>
              <span className="text-danger">{plata(precioFinal(p))}</span>
              <span className="badge bg-danger fs-6 ms-2 align-middle">-{p.descuento}%</span>
            </p>
          ) : (
            <p className="display-6">{plata(p.precio)}</p>
          )}
          <p>{agotado ? <span className="badge bg-danger">Sin stock</span> : <span className="badge bg-success">Stock disponible: {p.stock}</span>}</p>
          <div className="d-flex align-items-center gap-3 my-3">
            <label className="form-label mb-0">Cantidad</label>
            <input
              type="number"
              className="form-control"
              style={{ width: 90 }}
              value={cant}
              min={1}
              max={Math.max(p.stock, 1)}
              disabled={agotado}
              onChange={e => setCant(Math.max(1, parseInt(e.target.value) || 1))}
            />
          </div>
          <button className="btn btn-accent btn-lg" disabled={agotado} onClick={agregar}>
            <i className="bi bi-bag-plus"></i> Añadir al carrito
          </button>
        </div>
      </div>
    </section>
  )
}
