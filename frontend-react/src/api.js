// Conexion con el backend (microservicios) a traves del API Gateway.
// Cada funcion traduce los datos del backend al formato que ya usaban las paginas,
// asi las vistas no tienen que saber como se llaman los campos en Java.
import { API_URL } from './config.js'
import { getSesion, guardarSesion, actualizarToken, cerrarSesion, setReferencias } from './datos.js'

export class ApiError extends Error {
  constructor(status, mensaje) {
    super(mensaje)
    this.status = status
  }
}

// Hace la peticion y devuelve el JSON (o el texto) de la respuesta.
// Si el backend responde con error, lanza un ApiError con su mensaje.
// Si el token vencio, lo renueva una vez y repite la peticion.
export async function pedir(ruta, { metodo = "GET", cuerpo } = {}, reintentar = true) {
  const headers = {}
  if (cuerpo !== undefined) headers["Content-Type"] = "application/json"
  const sesion = getSesion()
  if (sesion && sesion.token) headers.Authorization = "Bearer " + sesion.token

  let respuesta
  try {
    respuesta = await fetch(API_URL + ruta, {
      method: metodo,
      headers,
      body: cuerpo === undefined ? undefined : JSON.stringify(cuerpo)
    })
  } catch {
    throw new ApiError(0, "No se pudo conectar con el servidor. Revisa que el backend este encendido.")
  }

  const texto = await respuesta.text()
  let datos = texto
  try {
    datos = texto ? JSON.parse(texto) : null
  } catch {
    // la respuesta era texto plano, por ejemplo "Producto eliminado correctamente."
  }

  // 401 = el token no sirve o vencio. Se intenta renovar y se repite la llamada.
  if (respuesta.status === 401 && reintentar && !ruta.startsWith("/api/v1/auth/")) {
    const habiaSesion = !!sesion
    if (habiaSesion && (await renovarToken())) {
      return pedir(ruta, { metodo, cuerpo }, false)
    }
    if (habiaSesion) {
      cerrarSesion()
      if (typeof window !== "undefined" && window.location.pathname !== "/login") {
        window.location.href = "/login?expirada=1"
      }
      throw new ApiError(401, "Tu sesion expiro. Vuelve a iniciar sesion.")
    }
  }

  if (!respuesta.ok) {
    let mensaje = typeof datos === "string" && datos ? datos : null
    if (!mensaje && respuesta.status === 400) mensaje = "Hay datos invalidos o incompletos."
    if (!mensaje && respuesta.status === 403) mensaje = "No tienes permisos para hacer esta accion."
    if (!mensaje) mensaje = "El servidor respondio con un error (" + respuesta.status + ")."
    throw new ApiError(respuesta.status, mensaje)
  }
  return datos
}

// Pide un token nuevo al auth-service sin tener que volver a escribir la clave
export async function renovarToken() {
  const sesion = getSesion()
  if (!sesion || !sesion.token) return false
  try {
    const respuesta = await fetch(API_URL + "/api/v1/auth/refresh", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ token: sesion.token })
    })
    if (!respuesta.ok) return false
    const datos = await respuesta.json()
    if (!datos.token) return false
    actualizarToken(datos.token)
    return true
  } catch {
    return false
  }
}

/* ==================== Traductores backend <-> frontend ==================== */

const ROL_A_ID = { ADMIN: 1, VENDEDOR: 2, CLIENTE: 3 }
const ID_A_ROL = { 1: "ADMIN", 2: "VENDEDOR", 3: "CLIENTE" }

const aEstado = e => (e === "ACTIVO" ? "Activo" : "Inactivo")
const deEstado = e => (e === "Inactivo" ? "INACTIVO" : "ACTIVO")

function aProducto(p) {
  return { ...p, estado: aEstado(p.estado) }
}

function deProducto(p) {
  return {
    codigo: p.codigo, nombre: p.nombre, descripcion: p.descripcion || null,
    precio: Number(p.precio), descuento: Number(p.descuento) || 0,
    stock: Number(p.stock), stockCritico: p.stockCritico === "" || p.stockCritico == null ? null : Number(p.stockCritico),
    categoriaId: Number(p.categoriaId), imagen: p.imagen || null,
    estado: deEstado(p.estado), destacado: !!p.destacado
  }
}

function aUsuario(u) {
  return {
    id: u.id, run: u.run, nombre: u.nombre, apellidos: u.apellidos,
    correo: u.email, fechaNacimiento: u.fechaNacimiento || "",
    rolId: ROL_A_ID[u.rol] || 3, regionId: u.regionId, comunaId: u.comunaId,
    direccion: u.direccion, estado: aEstado(u.estado)
  }
}

function deUsuario(u) {
  const datos = {
    run: u.run, nombre: u.nombre, apellidos: u.apellidos, email: u.correo,
    fechaNacimiento: u.fechaNacimiento || null,
    rol: ID_A_ROL[Number(u.rolId)] || "CLIENTE",
    regionId: Number(u.regionId), comunaId: Number(u.comunaId),
    direccion: u.direccion, estado: deEstado(u.estado)
  }
  // Si el admin no escribio una clave nueva, no se envia nada y el backend
  // mantiene la que ya estaba guardada.
  if (u.password) datos.password = u.password
  return datos
}

const TIPO_ENTREGA = { DESPACHO: "Despacho a domicilio", RETIRO: "Retiro en tienda" }
const capitalizar = t => (t ? t.charAt(0) + t.slice(1).toLowerCase() : t)

// Un pedido del backend con la misma forma que tenian las "ordenes" del frontend
function aOrden(p) {
  return {
    id: p.pedidoId,
    numero: p.numero,
    usuarioId: p.usuarioId,
    fecha: p.fechaCreacion,
    estado: p.estado === "PAGADO" ? "Pagada" : p.estado === "RECHAZADO" ? "Rechazada" : "Pendiente",
    motivo: p.motivoRechazo,
    folioBoleta: p.folioBoleta,
    cliente: {
      run: p.cliente?.run || "", nombre: p.cliente?.nombre || "",
      apellidos: p.cliente?.apellidos || "", correo: p.cliente?.email || ""
    },
    envio: {
      tipo: TIPO_ENTREGA[p.entrega?.tipo] || p.entrega?.tipo,
      calle: p.entrega?.calle || "", depto: p.entrega?.depto || "",
      regionId: p.entrega?.regionId, comunaId: p.entrega?.comunaId,
      indicaciones: p.entrega?.indicaciones || "", medio: capitalizar(p.medioPago)
    },
    items: (p.items || []).map(i => ({
      productoId: i.productoId, codigo: i.codigo, nombre: i.nombre,
      imagen: i.imagen, precio: i.precioUnitario, cantidad: i.cantidad
    })),
    subtotal: p.subtotal, costoEnvio: p.costoEnvio, total: p.total
  }
}

function aMensaje(m) {
  return { id: m.id, nombre: m.nombre, correo: m.email, comentario: m.comentario, fecha: m.fecha }
}

/* ==================== Autenticacion (auth-service) ==================== */

// Inicia sesion, guarda el token y los datos del usuario en el navegador
export async function iniciarSesion(correo, password) {
  const r = await pedir("/api/v1/auth/login", { metodo: "POST", cuerpo: { email: correo, password } })
  const sesion = {
    token: r.token,
    id: r.usuario.id,
    run: r.usuario.run,
    nombre: r.usuario.nombre,
    apellidos: r.usuario.apellidos,
    correo: r.usuario.email,
    rolId: ROL_A_ID[r.usuario.rol] || 3
  }
  guardarSesion(sesion)
  return sesion
}

/* ==================== Referencias (categorias, regiones, comunas) ==================== */

export async function cargarReferencias() {
  const [categorias, regiones, comunas] = await Promise.all([
    listarCategorias(), listarRegiones(), listarComunas()
  ])
  setReferencias({ categorias, regiones, comunas })
}

/* ==================== ms-catalogo ==================== */

export async function listarProductos({ soloActivos = false } = {}) {
  const lista = await pedir("/api/v1/catalogo/productos" + (soloActivos ? "?soloActivos=true" : ""))
  return lista.map(aProducto)
}

export async function listarOfertas() {
  return (await pedir("/api/v1/catalogo/productos/ofertas")).map(aProducto)
}

export async function listarCriticos() {
  return (await pedir("/api/v1/catalogo/productos/criticos")).map(aProducto)
}

// devuelve null si el producto no existe
export async function obtenerProductoPorCodigo(codigo) {
  try {
    return aProducto(await pedir("/api/v1/catalogo/productos/codigo/" + encodeURIComponent(codigo)))
  } catch (e) {
    if (e.status === 404) return null
    throw e
  }
}

export async function crearProducto(producto) {
  return aProducto(await pedir("/api/v1/catalogo/productos", { metodo: "POST", cuerpo: deProducto(producto) }))
}

export async function actualizarProducto(id, producto) {
  return aProducto(await pedir("/api/v1/catalogo/productos/" + id, { metodo: "PUT", cuerpo: deProducto(producto) }))
}

export function eliminarProducto(id) {
  return pedir("/api/v1/catalogo/productos/" + id, { metodo: "DELETE" })
}

export function listarCategorias() {
  return pedir("/api/v1/catalogo/categorias")
}

export function crearCategoria(categoria) {
  return pedir("/api/v1/catalogo/categorias", { metodo: "POST", cuerpo: categoria })
}

export function actualizarCategoria(id, categoria) {
  return pedir("/api/v1/catalogo/categorias/" + id, { metodo: "PUT", cuerpo: categoria })
}

export function eliminarCategoria(id) {
  return pedir("/api/v1/catalogo/categorias/" + id, { metodo: "DELETE" })
}

/* ==================== ms-usuarios ==================== */

export async function listarUsuarios() {
  return (await pedir("/api/v1/usuarios")).map(aUsuario)
}

export async function obtenerUsuario(id) {
  try {
    return aUsuario(await pedir("/api/v1/usuarios/" + id))
  } catch (e) {
    if (e.status === 404) return null
    throw e
  }
}

// las rutas del admin usan el RUN, se busca en la lista de usuarios
export async function obtenerUsuarioPorRun(run) {
  return (await listarUsuarios()).find(u => u.run === run) || null
}

export async function crearUsuario(usuario) {
  return aUsuario(await pedir("/api/v1/usuarios", { metodo: "POST", cuerpo: deUsuario(usuario) }))
}

export async function actualizarUsuario(id, usuario) {
  return aUsuario(await pedir("/api/v1/usuarios/" + id, { metodo: "PUT", cuerpo: deUsuario(usuario) }))
}

export function eliminarUsuario(id) {
  return pedir("/api/v1/usuarios/" + id, { metodo: "DELETE" })
}

export function listarRegiones() {
  return pedir("/api/v1/usuarios/regiones")
}

export function listarComunas() {
  return pedir("/api/v1/usuarios/comunas")
}

/* ==================== ms-pedidos (y ms-pagos a traves de ms-pedidos) ==================== */

// Envia el carrito y los datos del checkout. El backend cobra y responde la orden Pagada o Rechazada.
export async function comprar({ usuarioId, cliente, envio, items, simularRechazo }) {
  const pedido = await pedir("/api/v1/pedidos", {
    metodo: "POST",
    cuerpo: {
      usuarioId,
      cliente: { run: cliente.run || null, nombre: cliente.nombre, apellidos: cliente.apellidos, email: cliente.correo },
      entrega: {
        tipo: envio.tipo === "Retiro en tienda" ? "RETIRO" : "DESPACHO",
        calle: envio.calle || null, depto: envio.depto || null,
        regionId: envio.regionId || null, comunaId: envio.comunaId || null,
        indicaciones: envio.indicaciones || null
      },
      medioPago: (envio.medio || "Debito").toUpperCase(),
      simularRechazo: !!simularRechazo,
      items: items.map(i => ({ productoId: i.productoId, cantidad: i.cantidad }))
    }
  })
  return aOrden(pedido)
}

export async function listarOrdenes() {
  return (await pedir("/api/v1/pedidos")).map(aOrden)
}

export async function ordenesDeUsuario(usuarioId) {
  return (await pedir("/api/v1/pedidos/usuario/" + usuarioId)).map(aOrden)
}

export async function obtenerOrden(id) {
  try {
    return aOrden(await pedir("/api/v1/pedidos/" + id))
  } catch (e) {
    if (e.status === 404) return null
    throw e
  }
}

/* ==================== ms-contenido ==================== */

export function listarArticulos({ incluirBorradores = false } = {}) {
  return pedir("/api/v1/contenido/articulos" + (incluirBorradores ? "?incluirBorradores=true" : ""))
}

export async function obtenerArticulo(slug) {
  try {
    return await pedir("/api/v1/contenido/articulos/" + encodeURIComponent(slug))
  } catch (e) {
    if (e.status === 404) return null
    throw e
  }
}

export async function listarMensajes() {
  return (await pedir("/api/v1/contenido/mensajes")).map(aMensaje)
}

export async function enviarMensaje({ nombre, correo, comentario }) {
  return aMensaje(await pedir("/api/v1/contenido/mensajes", {
    metodo: "POST",
    cuerpo: { nombre, email: correo, comentario }
  }))
}

export function eliminarMensaje(id) {
  return pedir("/api/v1/contenido/mensajes/" + id, { metodo: "DELETE" })
}
