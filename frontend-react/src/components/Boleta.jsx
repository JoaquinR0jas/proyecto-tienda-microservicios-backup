import { plata, imgProducto, nombreRegion, nombreComuna, fechaCorta } from '../datos.js'

// detalle de una orden en modo solo lectura (se usa en compra exitosa, pago con error y en el admin)
export default function Boleta({ orden }) {
  const { cliente, envio } = orden
  const despacho = envio.tipo === "Despacho a domicilio"

  return (
    <div className="boleta">
      <div className="d-flex flex-wrap justify-content-between small text-muted mb-3">
        <span>Fecha: {fechaCorta(orden.fecha)}</span>
        <span>Codigo orden: ORDER{String(orden.id).padStart(5, "0")}</span>
      </div>

      <h3 className="h6">Datos del cliente</h3>
      <div className="row g-2 mb-3">
        <div className="col-md-4"><label className="form-label small mb-0">Nombre</label><input className="form-control" value={cliente.nombre} readOnly /></div>
        <div className="col-md-4"><label className="form-label small mb-0">Apellidos</label><input className="form-control" value={cliente.apellidos} readOnly /></div>
        <div className="col-md-4"><label className="form-label small mb-0">Correo</label><input className="form-control" value={cliente.correo} readOnly /></div>
      </div>

      <h3 className="h6">Entrega: {envio.tipo}</h3>
      {despacho ? (
        <div className="row g-2 mb-3">
          <div className="col-md-6"><label className="form-label small mb-0">Calle</label><input className="form-control" value={envio.calle} readOnly /></div>
          <div className="col-md-6"><label className="form-label small mb-0">Departamento</label><input className="form-control" value={envio.depto || "-"} readOnly /></div>
          <div className="col-md-6"><label className="form-label small mb-0">Region</label><input className="form-control" value={nombreRegion(envio.regionId)} readOnly /></div>
          <div className="col-md-6"><label className="form-label small mb-0">Comuna</label><input className="form-control" value={nombreComuna(envio.comunaId)} readOnly /></div>
          {envio.indicaciones && (
            <div className="col-12"><label className="form-label small mb-0">Indicaciones</label><textarea className="form-control" value={envio.indicaciones} readOnly /></div>
          )}
        </div>
      ) : (
        <p className="small text-muted">Retiro en tienda FreshFits, Av. Principal 123, Santiago.</p>
      )}

      <div className="table-responsive">
        <table className="table table-sm align-middle">
          <thead>
            <tr><th>Imagen</th><th>Nombre</th><th>Precio</th><th>Cantidad</th><th className="text-end">Subtotal</th></tr>
          </thead>
          <tbody>
            {orden.items.map(i => (
              <tr key={i.codigo}>
                <td><img src={imgProducto(i)} alt={i.nombre} width="48" className="rounded" /></td>
                <td>{i.nombre}</td>
                <td>{plata(i.precio)}</td>
                <td>{i.cantidad}</td>
                <td className="text-end">{plata(i.precio * i.cantidad)}</td>
              </tr>
            ))}
          </tbody>
          <tfoot>
            <tr><td colSpan="4" className="text-end">Subtotal</td><td className="text-end">{plata(orden.subtotal)}</td></tr>
            <tr><td colSpan="4" className="text-end">Envio</td><td className="text-end">{orden.costoEnvio ? plata(orden.costoEnvio) : "Gratis"}</td></tr>
            <tr><td colSpan="4" className="text-end fw-bold">Total</td><td className="text-end fw-bold">{plata(orden.total)}</td></tr>
          </tfoot>
        </table>
      </div>
    </div>
  )
}
