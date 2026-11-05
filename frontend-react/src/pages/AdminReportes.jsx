import { useState } from 'react'
import { KEYS, getColeccion, plata, nombreRegion, fechaCorta } from '../datos.js'
import { AdminLayout, useAdmin } from '../components/AdminMenu.jsx'
import Barra from '../components/Barra.jsx'
import { aviso } from '../alertas.js'

function agrupar(lista, clave, valor) {
  const r = {}
  lista.forEach(x => {
    const k = clave(x)
    r[k] = (r[k] || 0) + valor(x)
  })
  return Object.entries(r)
}

function isoLocal(fecha = new Date()) {
  const d = new Date(fecha)
  d.setMinutes(d.getMinutes() - d.getTimezoneOffset())
  return d.toISOString().slice(0, 10)
}

function diaDeOrden(orden) {
  return isoLocal(new Date(orden.fecha))
}

function fechaLegible(iso) {
  if (!iso) return "-"
  return new Date(iso + "T00:00:00").toLocaleDateString("es-CL", { day: "2-digit", month: "long", year: "numeric" })
}

function mesLegible(mes) {
  if (!mes) return "-"
  const [anio, n] = mes.split("-").map(Number)
  const texto = new Date(anio, n - 1, 1).toLocaleDateString("es-CL", { month: "long", year: "numeric" })
  return texto.charAt(0).toUpperCase() + texto.slice(1)
}

function finDeMes(mes) {
  const [anio, n] = mes.split("-").map(Number)
  return mes + "-" + String(new Date(anio, n, 0).getDate()).padStart(2, "0")
}

const TIPOS_INFORME = [
  { id: "diario", texto: "Diario (un dia)" },
  { id: "rango", texto: "Rango de fechas" },
  { id: "mensual", texto: "Mensual" },
  { id: "todo", texto: "Historial completo" }
]

export default function AdminReportes() {
  const sesion = useAdmin(true)

  const hoy = isoLocal()
  const [tipo, setTipo] = useState("diario")
  const [dia, setDia] = useState(hoy)
  const [desde, setDesde] = useState(hoy.slice(0, 8) + "01")
  const [hasta, setHasta] = useState(hoy)
  const [mes, setMes] = useState(hoy.slice(0, 7))
  const [incluirDetalle, setIncluirDetalle] = useState(true)
  const [informe, setInforme] = useState(null)

  const ordenes = getColeccion(KEYS.ordenes)
  const pagadas = ordenes.filter(o => o.estado === "Pagada")
  const rechazadas = ordenes.length - pagadas.length
  const ventas = pagadas.reduce((s, o) => s + o.total, 0)
  const ticket = pagadas.length ? Math.round(ventas / pagadas.length) : 0
  const unidades = pagadas.reduce((s, o) => s + o.items.reduce((a, i) => a + i.cantidad, 0), 0)

  const porMes = agrupar(pagadas, o => o.fecha.slice(0, 7), o => o.total).sort()
  const porRegion = agrupar(pagadas.filter(o => o.envio.regionId), o => nombreRegion(o.envio.regionId), o => o.total)
  const porEntrega = agrupar(pagadas, o => o.envio.tipo, () => 1)
  const porCliente = agrupar(pagadas, o => o.cliente.nombre + " " + o.cliente.apellidos, o => o.total)
    .sort((a, b) => b[1] - a[1]).slice(0, 5)

  const max = lista => Math.max(0, ...lista.map(x => x[1]))

  if (!sesion) return null

  function generar(e) {
    e.preventDefault()

    let inicio = ""
    let fin = ""
    let etiqueta = ""
    let periodo = ""

    if (tipo === "diario") {
      if (!dia) return aviso("Falta la fecha", "Selecciona el dia del informe.", "warning")
      inicio = fin = dia
      etiqueta = "Informe diario de ventas - " + fechaLegible(dia)
    } else if (tipo === "rango") {
      if (!desde || !hasta) return aviso("Faltan las fechas", "Selecciona la fecha de inicio y la de termino.", "warning")
      if (desde > hasta) return aviso("Rango invalido", "La fecha de inicio no puede ser posterior a la fecha de termino.", "warning")
      inicio = desde
      fin = hasta
      etiqueta = "Informe de ventas del " + fechaLegible(desde) + " al " + fechaLegible(hasta)
    } else if (tipo === "mensual") {
      if (!mes) return aviso("Falta el mes", "Selecciona el mes del informe.", "warning")
      inicio = mes + "-01"
      fin = finDeMes(mes)
      etiqueta = "Informe mensual de ventas - " + mesLegible(mes)
    } else {
      inicio = "0000-01-01"
      fin = "9999-12-31"
      etiqueta = "Historial completo de ventas"
    }

    periodo = tipo === "todo" ? "Historial completo" : (inicio === fin ? fechaLegible(inicio) : fechaLegible(inicio) + " al " + fechaLegible(fin))

    const delPeriodo = ordenes
      .filter(o => {
        const f = diaDeOrden(o)
        return f >= inicio && f <= fin
      })
      .sort((a, b) => b.fecha.localeCompare(a.fecha))

    const pagadasPeriodo = delPeriodo.filter(o => o.estado === "Pagada")
    const rechazadasPeriodo = delPeriodo.filter(o => o.estado !== "Pagada")
    const ventasPeriodo = pagadasPeriodo.reduce((s, o) => s + o.total, 0)

    const mapaProductos = {}
    pagadasPeriodo.forEach(o => o.items.forEach(i => {
      const p = mapaProductos[i.codigo] || (mapaProductos[i.codigo] = {
        codigo: i.codigo, nombre: i.nombre, cantidad: 0, total: 0
      })
      p.cantidad += i.cantidad
      p.total += i.precio * i.cantidad
    }))
    const productos = Object.values(mapaProductos).sort((a, b) => b.cantidad - a.cantidad || b.total - a.total)

    const porDia = agrupar(pagadasPeriodo, o => diaDeOrden(o), o => o.total).sort((a, b) => a[0].localeCompare(b[0]))

    setInforme({
      etiqueta,
      periodo,
      generadoEn: new Date(),
      generadoPor: sesion.nombre + " " + sesion.apellidos,
      incluirDetalle,
      ordenes: delPeriodo,
      resumen: {
        ventas: ventasPeriodo,
        pagadas: pagadasPeriodo.length,
        rechazadas: rechazadasPeriodo.length,
        ticket: pagadasPeriodo.length ? Math.round(ventasPeriodo / pagadasPeriodo.length) : 0,
        unidades: pagadasPeriodo.reduce((s, o) => s + o.items.reduce((a, i) => a + i.cantidad, 0), 0)
      },
      productos,
      porDia
    })

    setTimeout(() => document.getElementById("informe-generado")?.scrollIntoView({ behavior: "smooth", block: "start" }), 0)
  }

  function imprimir() {
    if (!informe) return
    const tituloAnterior = document.title
    document.title = informe.etiqueta.replace(/[\\/:*?"<>|]/g, "-")
    window.print()
    document.title = tituloAnterior
  }

  return (
    <AdminLayout>
      <h1 className="section-title no-print">Reportes</h1>

      <div className="card p-4 mb-4 no-print">
        <div className="d-flex flex-wrap justify-content-between align-items-start gap-2 mb-3">
          <div>
            <h2 className="h5 mb-1"><i className="bi bi-file-earmark-bar-graph me-2"></i>Generador de informes</h2>
            <p className="small text-muted mb-0">Elige el periodo y genera un informe de ventas listo para imprimir o guardar en PDF.</p>
          </div>
        </div>

        <form className="row g-3 align-items-end" onSubmit={generar}>
          <div className="col-md-4 col-xl-3">
            <label className="form-label">Tipo de informe</label>
            <select className="form-select" value={tipo} onChange={e => setTipo(e.target.value)}>
              {TIPOS_INFORME.map(t => <option key={t.id} value={t.id}>{t.texto}</option>)}
            </select>
          </div>

          {tipo === "diario" && (
            <div className="col-md-4 col-xl-3">
              <label className="form-label">Dia del informe</label>
              <input type="date" className="form-control" value={dia} onChange={e => setDia(e.target.value)} />
            </div>
          )}

          {tipo === "rango" && (
            <>
              <div className="col-md-4 col-xl-3">
                <label className="form-label">Desde</label>
                <input type="date" className="form-control" value={desde} onChange={e => setDesde(e.target.value)} />
              </div>
              <div className="col-md-4 col-xl-3">
                <label className="form-label">Hasta</label>
                <input type="date" className="form-control" value={hasta} onChange={e => setHasta(e.target.value)} />
              </div>
            </>
          )}

          {tipo === "mensual" && (
            <div className="col-md-4 col-xl-3">
              <label className="form-label">Mes del informe</label>
              <input type="month" className="form-control" value={mes} onChange={e => setMes(e.target.value)} />
            </div>
          )}

          <div className="col-md-4 col-xl-3">
            <div className="form-check mb-2">
              <input className="form-check-input" type="checkbox" id="detalleInforme" checked={incluirDetalle} onChange={e => setIncluirDetalle(e.target.checked)} />
              <label className="form-check-label" htmlFor="detalleInforme">Incluir detalle de ordenes y productos</label>
            </div>
          </div>

          <div className="col-md-4 col-xl-3 d-flex gap-2">
            <button type="submit" className="btn btn-accent"><i className="bi bi-magic me-1"></i>Generar informe</button>
            {informe && <button type="button" className="btn btn-outline-dark" onClick={() => setInforme(null)}>Limpiar</button>}
          </div>
        </form>
      </div>

      {informe && (
        <div className="informe bg-white rounded border p-4 mb-4" id="informe-generado">
          <div className="d-flex flex-wrap justify-content-between align-items-start gap-2 border-bottom pb-3 mb-3">
            <div>
              <div className="brand-text h5 mb-1">FRESH FITS</div>
              <h2 className="h4 mb-0">{informe.etiqueta}</h2>
              <p className="small text-muted mb-0">Periodo: {informe.periodo}</p>
            </div>
            <div className="text-md-end small text-muted">
              <div>Generado por: <strong>{informe.generadoPor}</strong></div>
              <div>Fecha de generacion: {fechaCorta(informe.generadoEn.toISOString())}</div>
            </div>
          </div>

          <div className="row g-3 mb-4">
            <div className="col-6 col-md-4 col-xl">
              <div className="informe-dato"><small>Ventas del periodo</small><strong>{plata(informe.resumen.ventas)}</strong></div>
            </div>
            <div className="col-6 col-md-4 col-xl">
              <div className="informe-dato"><small>Ordenes pagadas</small><strong>{informe.resumen.pagadas}</strong></div>
            </div>
            <div className="col-6 col-md-4 col-xl">
              <div className="informe-dato"><small>Pagos rechazados</small><strong>{informe.resumen.rechazadas}</strong></div>
            </div>
            <div className="col-6 col-md-4 col-xl">
              <div className="informe-dato"><small>Ticket promedio</small><strong>{plata(informe.resumen.ticket)}</strong></div>
            </div>
            <div className="col-6 col-md-4 col-xl">
              <div className="informe-dato"><small>Unidades vendidas</small><strong>{informe.resumen.unidades}</strong></div>
            </div>
          </div>

          {informe.incluirDetalle && (
            <>
              {informe.porDia.length > 1 && (
                <div className="informe-bloque mb-4">
                  <h3 className="h6">Ventas por dia</h3>
                  <div className="table-responsive">
                    <table className="table table-sm table-bordered align-middle mb-0">
                      <thead><tr><th>Dia</th><th className="text-end">Ventas</th></tr></thead>
                      <tbody>
                        {informe.porDia.map(([f, total]) => (
                          <tr key={f}><td>{fechaLegible(f)}</td><td className="text-end">{plata(total)}</td></tr>
                        ))}
                      </tbody>
                      <tfoot><tr className="fw-bold"><td>Total</td><td className="text-end">{plata(informe.resumen.ventas)}</td></tr></tfoot>
                    </table>
                  </div>
                </div>
              )}

              <div className="informe-bloque mb-4">
                <h3 className="h6">Detalle de ordenes</h3>
                <div className="table-responsive">
                  <table className="table table-sm table-bordered align-middle mb-0">
                    <thead>
                      <tr>
                        <th>N. orden</th><th>Fecha</th><th>Cliente</th><th>Entrega</th>
                        <th>Medio de pago</th><th>Estado</th><th className="text-end">Total</th>
                      </tr>
                    </thead>
                    <tbody>
                      {informe.ordenes.length === 0 && (
                        <tr><td colSpan="7" className="text-center text-muted">No hay ordenes registradas en el periodo.</td></tr>
                      )}
                      {informe.ordenes.map(o => (
                        <tr key={o.id}>
                          <td>#{o.numero}</td>
                          <td>{fechaCorta(o.fecha)}</td>
                          <td>{o.cliente.nombre} {o.cliente.apellidos}</td>
                          <td>{o.envio.tipo}</td>
                          <td>{o.envio.medio || "-"}</td>
                          <td>{o.estado}</td>
                          <td className="text-end">{plata(o.total)}</td>
                        </tr>
                      ))}
                    </tbody>
                    <tfoot>
                      <tr className="fw-bold"><td colSpan="6">Total pagado en el periodo</td><td className="text-end">{plata(informe.resumen.ventas)}</td></tr>
                    </tfoot>
                  </table>
                </div>
              </div>

              <div className="informe-bloque mb-4">
                <h3 className="h6">Productos vendidos</h3>
                <div className="table-responsive">
                  <table className="table table-sm table-bordered align-middle mb-0">
                    <thead>
                      <tr>
                        <th>Codigo</th>
                        <th>Producto</th>
                        <th className="text-end">Unidades vendidas</th>
                        <th className="text-end">Precio promedio</th>
                        <th className="text-end">Total vendido</th>
                      </tr>
                    </thead>
                    <tbody>
                      {informe.productos.length === 0 && (
                        <tr><td colSpan="5" className="text-center text-muted">No hay productos vendidos en el periodo.</td></tr>
                      )}
                      {informe.productos.map(p => (
                        <tr key={p.codigo}>
                          <td>{p.codigo}</td>
                          <td>{p.nombre}</td>
                          <td className="text-end">{p.cantidad}</td>
                          <td className="text-end">{plata(p.cantidad ? Math.round(p.total / p.cantidad) : 0)}</td>
                          <td className="text-end">{plata(p.total)}</td>
                        </tr>
                      ))}
                    </tbody>
                    <tfoot>
                      <tr className="fw-bold">
                        <td colSpan="2">Totales</td>
                        <td className="text-end">{informe.productos.reduce((s, p) => s + p.cantidad, 0)}</td>
                        <td></td>
                        <td className="text-end">{plata(informe.productos.reduce((s, p) => s + p.total, 0))}</td>
                      </tr>
                    </tfoot>
                  </table>
                </div>
                <p className="small text-muted mb-0 mt-2">
                  Se muestra la cantidad total de unidades por producto y el total recaudado por cada uno, considerando solo ordenes pagadas.
                </p>
              </div>
            </>
          )}

          <p className="small text-muted mb-0">
            Las ventas y los productos consideran solo las ordenes en estado &quot;Pagada&quot;. Los pagos rechazados se informan por separado y no suman al total.
          </p>

          <div className="d-flex flex-wrap gap-2 mt-3 no-print">
            <button className="btn btn-danger" onClick={imprimir}><i className="bi bi-printer me-1"></i>Imprimir / Guardar PDF</button>
            <button className="btn btn-outline-accent" onClick={() => setInforme(null)}>Cerrar informe</button>
          </div>
        </div>
      )}

      <div className="no-print">
        <div className="row g-3 mb-4">
          <div className="col-6 col-md-3"><div className="card p-3"><small className="text-muted">Ventas totales</small><span className="fs-5 fw-bold">{plata(ventas)}</span></div></div>
          <div className="col-6 col-md-3"><div className="card p-3"><small className="text-muted">Ordenes pagadas</small><span className="fs-5 fw-bold">{pagadas.length}</span></div></div>
          <div className="col-6 col-md-3"><div className="card p-3"><small className="text-muted">Pagos rechazados</small><span className="fs-5 fw-bold text-danger">{rechazadas}</span></div></div>
          <div className="col-6 col-md-3"><div className="card p-3"><small className="text-muted">Ticket promedio</small><span className="fs-5 fw-bold">{plata(ticket)}</span></div></div>
        </div>

        <div className="row g-4">
          <div className="col-lg-6">
            <div className="card p-4 h-100">
              <h2 className="h6 mb-3">Ventas por mes</h2>
              {porMes.length === 0 && <p className="text-muted small">Sin ventas aun.</p>}
              {porMes.map(([mes, total]) => <Barra key={mes} etiqueta={mes} valor={total} maximo={max(porMes)} texto={plata(total)} />)}
            </div>
          </div>
          <div className="col-lg-6">
            <div className="card p-4 h-100">
              <h2 className="h6 mb-3">Ventas por region</h2>
              {porRegion.map(([reg, total]) => <Barra key={reg} etiqueta={reg} valor={total} maximo={max(porRegion)} texto={plata(total)} />)}
            </div>
          </div>
          <div className="col-lg-6">
            <div className="card p-4 h-100">
              <h2 className="h6 mb-3">Mejores clientes</h2>
              {porCliente.map(([cli, total]) => <Barra key={cli} etiqueta={cli} valor={total} maximo={max(porCliente)} texto={plata(total)} />)}
            </div>
          </div>
          <div className="col-lg-6">
            <div className="card p-4 h-100">
              <h2 className="h6 mb-3">Tipo de entrega</h2>
              {porEntrega.map(([tipo, n]) => <Barra key={tipo} etiqueta={tipo} valor={n} maximo={max(porEntrega)} texto={n + " ordenes"} />)}
              <p className="small text-muted mt-3 mb-0">Unidades vendidas en total: {unidades}</p>
            </div>
          </div>
        </div>
      </div>
    </AdminLayout>
  )
}
