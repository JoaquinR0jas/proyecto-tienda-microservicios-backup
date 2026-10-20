import { Link } from 'react-router-dom'
import { plata, imgProducto, nombreCategoria, precioFinal, enOferta } from '../datos.js'

function Tarjeta({ p, onAgregar }) {
  const agotado = p.stock <= 0
  const oferta = enOferta(p)
  return (
    <div className="col-sm-6 col-lg-3">
      <div className="card h-100 card-ff-hover position-relative">
        {oferta && <span className="badge bg-danger badge-oferta">-{p.descuento}%</span>}
        <img src={imgProducto(p)} className="card-img-top" alt={p.nombre} />
        <div className="card-body d-flex flex-column">
          <span className="badge bg-secondary align-self-start mb-2">{nombreCategoria(p.categoriaId)}</span>
          <h3 className="h6">{p.nombre}</h3>
          <p className="fw-bold mb-2">
            {oferta && <span className="precio-tachado me-2">{plata(p.precio)}</span>}
            <span className={oferta ? "text-danger" : ""}>{plata(precioFinal(p))}</span>
          </p>
          <p className="small text-muted mb-3">
            {agotado ? <span className="badge bg-danger">Sin stock</span> : "Stock: " + p.stock}
          </p>
          <div className="mt-auto d-flex gap-2">
            <Link to={"/producto/" + p.codigo} className="btn btn-outline-accent btn-sm flex-fill">Ver detalle</Link>
            <button className="btn btn-accent btn-sm flex-fill" disabled={agotado} onClick={() => onAgregar(p.codigo)}>
              <i className="bi bi-bag-plus"></i>
            </button>
          </div>
        </div>
      </div>
    </div>
  )
}

export default Tarjeta
