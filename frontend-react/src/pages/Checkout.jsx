import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { KEYS, getColeccion, getSesion, plata, imgProducto, detalleCarrito, costoEnvio, registrarOrden } from '../datos.js'
import { correoOk, textoOk } from '../validaciones.js'

const VACIO = {
  nombre: "", apellidos: "", correo: "",
  tipo: "Despacho a domicilio", calle: "", depto: "", region: "", comuna: "", indicaciones: "",
  medio: "Debito", simularError: false
}

export default function Checkout() {
  const nav = useNavigate()
  const [detalle] = useState(() => detalleCarrito())
  const [regiones] = useState(() => getColeccion(KEYS.regiones))
  const [comunas, setComunas] = useState([])
  const [errores, setErrores] = useState({})
  const [sesion] = useState(() => getSesion())
  const [form, setForm] = useState(() => {
    // si el usuario inicio sesion se rellenan sus datos automaticamente
    if (!sesion) return VACIO
    const u = getColeccion(KEYS.usuarios).find(x => x.run === sesion.run)
    if (!u) return VACIO
    return {
      ...VACIO, nombre: u.nombre, apellidos: u.apellidos, correo: u.correo,
      calle: u.direccion || "", region: u.regionId || "", comuna: u.comunaId || ""
    }
  })

  useEffect(() => {
    setComunas(form.region ? getColeccion(KEYS.comunas).filter(c => c.regionId === Number(form.region)) : [])
  }, [form.region])

  function cambiar(e) {
    const { name, value, type, checked } = e.target
    const nuevo = { ...form, [name]: type === "checkbox" ? checked : value }
    if (name === "region") nuevo.comuna = ""
    setForm(nuevo)
  }

  function validar() {
    const e = {}
    if (!textoOk(form.nombre, 50)) e.nombre = "Ingresa tu nombre"
    if (!textoOk(form.apellidos, 100)) e.apellidos = "Ingresa tus apellidos"
    if (!correoOk(form.correo)) e.correo = "Correo invalido (@duoc.cl, @profesor.duoc.cl o @gmail.com)"
    if (form.tipo === "Despacho a domicilio") {
      if (!textoOk(form.calle, 300)) e.calle = "Ingresa la calle y numero"
      if (!form.region) e.region = "Selecciona una region"
      if (!form.comuna) e.comuna = "Selecciona una comuna"
    }
    setErrores(e)
    return Object.keys(e).length === 0
  }

  function pagar(e) {
    e.preventDefault()
    if (!validar()) return

    // el pago se rechaza si alguien compro el stock antes o si se simula un rechazo del banco
    let motivo = ""
    const prods = getColeccion(KEYS.productos)
    const sinStock = detalle.items.find(i => {
      const p = prods.find(x => x.codigo === i.codigo)
      return !p || p.stock < i.cantidad
    })
    if (sinStock) motivo = "No hay stock suficiente de " + sinStock.nombre
    else if (form.simularError) motivo = "El medio de pago fue rechazado por el banco"

    const orden = registrarOrden({
      cliente: { run: sesion ? sesion.run : "", nombre: form.nombre.trim(), apellidos: form.apellidos.trim(), correo: form.correo.trim() },
      envio: {
        tipo: form.tipo, calle: form.calle.trim(), depto: form.depto.trim(),
        regionId: Number(form.region) || null, comunaId: Number(form.comuna) || null,
        indicaciones: form.indicaciones.trim(), medio: form.medio
      },
      items: detalle.items,
      subtotal: detalle.subtotal,
      estado: motivo ? "Rechazada" : "Pagada"
    })

    if (motivo) nav("/compra/error/" + orden.id, { state: { motivo } })
    else nav("/compra/exitosa/" + orden.id)
  }

  if (detalle.items.length === 0) {
    return (
      <section className="container py-5 text-center">
        <p className="text-muted">No tienes productos en el carrito.</p>
        <Link to="/productos" className="btn btn-accent">Ver catalogo</Link>
      </section>
    )
  }

  const envio = costoEnvio(form.tipo, detalle.subtotal)
  const total = detalle.subtotal + envio

  function campo(name) {
    return "form-control" + (errores[name] ? " is-invalid" : "")
  }

  return (
    <section className="container py-5">
      <div className="row justify-content-center">
        <div className="col-lg-9">
          <form onSubmit={pagar} className="bg-ff-panel rounded p-4" noValidate>
            <div className="d-flex flex-wrap justify-content-between align-items-start gap-2 mb-3">
              <div>
                <h1 className="h4 mb-0">Carrito de compra</h1>
                <p className="small text-muted mb-0">Completa la siguiente informacion</p>
              </div>
              <span className="btn btn-accent pe-none">Total a pagar: {plata(total)}</span>
            </div>

            <div className="table-responsive mb-4">
              <table className="table table-sm align-middle">
                <thead><tr><th>Imagen</th><th>Nombre</th><th>Precio</th><th>Cantidad</th><th className="text-end">Subtotal</th></tr></thead>
                <tbody>
                  {detalle.items.map(i => (
                    <tr key={i.codigo}>
                      <td><img src={imgProducto(i)} alt={i.nombre} width="48" className="rounded" /></td>
                      <td>{i.nombre}</td>
                      <td>{plata(i.precio)}</td>
                      <td>{i.cantidad}</td>
                      <td className="text-end">{plata(i.precio * i.cantidad)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>

            <h2 className="h5">Informacion del cliente</h2>
            {sesion
              ? <p className="small text-muted">Cargamos tus datos de la cuenta, puedes modificarlos.</p>
              : <p className="small text-muted">Completa tus datos o <Link to="/login">inicia sesion</Link> para rellenarlos automaticamente.</p>}
            <div className="row g-3 mb-4">
              <div className="col-md-6">
                <label className="form-label">Nombre*</label>
                <input name="nombre" className={campo("nombre")} value={form.nombre} onChange={cambiar} />
                <div className="invalid-feedback">{errores.nombre}</div>
              </div>
              <div className="col-md-6">
                <label className="form-label">Apellidos*</label>
                <input name="apellidos" className={campo("apellidos")} value={form.apellidos} onChange={cambiar} />
                <div className="invalid-feedback">{errores.apellidos}</div>
              </div>
              <div className="col-md-6">
                <label className="form-label">Correo*</label>
                <input name="correo" type="email" className={campo("correo")} value={form.correo} onChange={cambiar} />
                <div className="invalid-feedback">{errores.correo}</div>
              </div>
            </div>

            <h2 className="h5">Opciones de entrega</h2>
            <div className="d-flex flex-wrap gap-4 mb-3">
              {["Despacho a domicilio", "Retiro en tienda"].map(t => (
                <div className="form-check" key={t}>
                  <input className="form-check-input" type="radio" name="tipo" id={t} value={t} checked={form.tipo === t} onChange={cambiar} />
                  <label className="form-check-label" htmlFor={t}>
                    {t} <span className="text-muted small">({costoEnvio(t, detalle.subtotal) ? plata(costoEnvio(t, detalle.subtotal)) : "gratis"})</span>
                  </label>
                </div>
              ))}
            </div>

            {form.tipo === "Despacho a domicilio" && (
              <>
                <h2 className="h5">Direccion de entrega de los productos</h2>
                <p className="small text-muted">Ingresa la direccion de forma detallada</p>
                <div className="row g-3 mb-4">
                  <div className="col-md-6">
                    <label className="form-label">Calle*</label>
                    <input name="calle" className={campo("calle")} value={form.calle} onChange={cambiar} />
                    <div className="invalid-feedback">{errores.calle}</div>
                  </div>
                  <div className="col-md-6">
                    <label className="form-label">Departamento (opcional)</label>
                    <input name="depto" className="form-control" placeholder="Ej: 603" value={form.depto} onChange={cambiar} />
                  </div>
                  <div className="col-md-6">
                    <label className="form-label">Region*</label>
                    <select name="region" className={"form-select" + (errores.region ? " is-invalid" : "")} value={form.region} onChange={cambiar}>
                      <option value="">Seleccione...</option>
                      {regiones.map(r => <option key={r.id} value={r.id}>{r.nombre}</option>)}
                    </select>
                    <div className="invalid-feedback">{errores.region}</div>
                  </div>
                  <div className="col-md-6">
                    <label className="form-label">Comuna*</label>
                    <select name="comuna" className={"form-select" + (errores.comuna ? " is-invalid" : "")} value={form.comuna} onChange={cambiar}>
                      <option value="">Seleccione...</option>
                      {comunas.map(c => <option key={c.id} value={c.id}>{c.nombre}</option>)}
                    </select>
                    <div className="invalid-feedback">{errores.comuna}</div>
                  </div>
                  <div className="col-12">
                    <label className="form-label">Indicaciones para la entrega (opcional)</label>
                    <textarea name="indicaciones" className="form-control" rows="2" placeholder="Ej: entre calles, color del edificio, no tiene timbre." value={form.indicaciones} onChange={cambiar}></textarea>
                  </div>
                </div>
              </>
            )}

            <h2 className="h5">Medio de pago</h2>
            <div className="row g-3 align-items-end mb-4">
              <div className="col-md-6">
                <select name="medio" className="form-select" value={form.medio} onChange={cambiar}>
                  <option>Debito</option>
                  <option>Credito</option>
                  <option>Transferencia</option>
                </select>
              </div>
              <div className="col-md-6">
                <div className="form-check">
                  <input className="form-check-input" type="checkbox" id="simularError" name="simularError" checked={form.simularError} onChange={cambiar} />
                  <label className="form-check-label small text-muted" htmlFor="simularError">Simular pago rechazado (demo)</label>
                </div>
              </div>
            </div>

            <div className="d-flex flex-wrap justify-content-between gap-2">
              <Link to="/carrito" className="btn btn-outline-accent">Volver al carrito</Link>
              <button type="submit" className="btn btn-success">Pagar ahora {plata(total)}</button>
            </div>
          </form>
        </div>
      </div>
    </section>
  )
}
