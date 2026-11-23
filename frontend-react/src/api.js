// Conexion con el backend (microservicios) a traves del API Gateway.
// Usamos axios para no repetir la configuracion en cada llamada: la URL base,
// el token y el manejo del 401 quedan en un solo lugar.
// Cada funcion traduce los datos del backend al formato que ya usaban las paginas,
// asi las vistas no tienen que saber como se llaman los campos en Java.
import axios from 'axios'
import { API_URL } from './config.js'
import { getSesion, guardarSesion, actualizarToken, cerrarSesion, setReferencias } from './datos.js'

export class ApiError extends Error {
  constructor(status, mensaje) {
    super(mensaje)
    this.status = status
  }
}

// Instancia de axios apuntando al gateway. Se exporta para que las pruebas
// puedan reemplazar sus respuestas por un backend falso.
export const http = axios.create({
  baseURL: API_URL,
  headers: { "Content-Type": "application/json" }
})

// Antes de cada peticion se agrega el token de la sesion guardada
http.interceptors.request.use((config) => {
  const sesion = getSesion()
  if (sesion && sesion.token) {
    config.headers.Authorization = "Bearer " + sesion.token
  }
  return config
})

// Si el backend responde 401 (token vencido), se pide uno nuevo y se repite
// la peticion una sola vez. Si no se puede renovar, se cierra la sesion.
http.interceptors.response.use(
  (respuesta) => respuesta,
  async (error) => {
    const original = error.config
    const status = error.response ? error.response.status : 0
    const esRutaDeAuth = original && original.url && original.url.startsWith("/api/v1/auth/")

    if (status === 401 && original && !original._reintentado && !esRutaDeAuth) {
      const habiaSesion = !!getSesion()
      if (habiaSesion && (await renovarToken())) {
        original._reintentado = true
        return http(original)
      }
      if (habiaSesion) {
        cerrarSesion()
        if (typeof window !== "undefined" && window.location.pathname !== "/login") {
          window.location.href = "/login?expirada=1"
        }
        throw new ApiError(401, "Tu sesion expiro. Vuelve a iniciar sesion.")
      }
    }
    return Promise.reject(error)
  }
)

// Hace la peticion y devuelve el JSON (o el texto) de la respuesta.
// Si el backend responde con error, lanza un ApiError con su mensaje.
export async function pedir(ruta, { metodo = "GET", cuerpo } = {}) {
  try {
    const respuesta = await http.request({ url: ruta, method: metodo, data: cuerpo })
    return respuesta.data
  } catch (error) {
    // El error puede venir del interceptor (sesion vencida) o de axios
    if (error instanceof ApiError) throw error
    if (!error.response) {
      throw new ApiError(0, "No se pudo conectar con el servidor. Revisa que el backend este encendido.")
    }

    const status = error.response.status
    const datos = error.response.data
    let mensaje = typeof datos === "string" && datos ? datos : null
    if (!mensaje && status === 400) mensaje = "Hay datos invalidos o incompletos."
    if (!mensaje && status === 403) mensaje = "No tienes permisos para hacer esta accion."
    if (!mensaje) mensaje = "El servidor respondio con un error (" + status + ")."
    throw new ApiError(status, mensaje)
  }
}

// Pide un token nuevo al auth-service sin tener que volver a escribir la clave.
// Se usa axios directo para no entrar de nuevo al interceptor del 401.
export async function renovarToken() {
  const sesion = getSesion()
  if (!sesion || !sesion.token) return false
  try {
    const respuesta = await axios.post(API_URL + "/api/v1/auth/refresh", { token: sesion.token })
    if (!respuesta.data || !respuesta.data.token) return false
    actualizarToken(respuesta.data.token)
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
