// Datos que se quedan en el navegador: la sesion y el carrito.
// Todo lo demas (productos, usuarios, ordenes, blog...) viene del backend, ver api.js

export const KEYS = {
  carrito: "ff_carrito",
  sesion: "ff_sesion"
}

// costo de despacho, gratis sobre cierto monto (las mismas reglas que usa ms-pedidos)
export const COSTO_DESPACHO = 3990
export const DESPACHO_GRATIS_DESDE = 50000

export const ROLES = [
  { id: 1, nombre: "Administrador" },
  { id: 2, nombre: "Vendedor" },
  { id: 3, nombre: "Cliente" }
]

export function nombreRol(rolId) {
  const r = ROLES.find(x => x.id === Number(rolId))
  return r ? r.nombre : ""
}

function leer(clave, porDefecto) {
  try {
    return JSON.parse(localStorage.getItem(clave)) ?? porDefecto
  } catch {
    return porDefecto
  }
}

/* ---------- Sesion (la guarda el login con el token del auth-service) ---------- */

export function getSesion() {
  return leer(KEYS.sesion, null)
}

export function guardarSesion(sesion) {
  localStorage.setItem(KEYS.sesion, JSON.stringify(sesion))
  window.dispatchEvent(new Event("sesion-cambio"))
}

export function cerrarSesion() {
  localStorage.removeItem(KEYS.sesion)
  window.dispatchEvent(new Event("sesion-cambio"))
}

/* ---------- Carrito: vive en el navegador, se puede usar sin iniciar sesion ---------- */

export function getCarrito() {
  return leer(KEYS.carrito, [])
}

export function setCarrito(carrito) {
  localStorage.setItem(KEYS.carrito, JSON.stringify(carrito))
  window.dispatchEvent(new Event("carrito-cambio"))
}

// agrega un producto al carrito, devuelve un mensaje de error o null si salio bien
export function agregarAlCarrito(producto, cantidad = 1) {
  if (!producto || producto.stock <= 0) return "Producto sin stock"
  const carrito = getCarrito()
  const item = carrito.find(i => i.codigo === producto.codigo)
  const actual = item ? item.cantidad : 0
  if (actual + cantidad > producto.stock) return "Solo hay " + producto.stock + " unidades disponibles"
  if (item) item.cantidad += cantidad
  else carrito.push({ productoId: producto.id, codigo: producto.codigo, cantidad })
  setCarrito(carrito)
  return null
}

// arma el detalle del carrito con los datos actuales de cada producto (traidos del backend)
export function detalleCarrito(productos) {
  const items = []
  getCarrito().forEach(i => {
    const p = productos.find(x => x.codigo === i.codigo)
    if (p) items.push({ productoId: p.id, codigo: p.codigo, nombre: p.nombre, imagen: p.imagen, precio: precioFinal(p), cantidad: i.cantidad, stock: p.stock })
  })
  const subtotal = items.reduce((s, i) => s + i.precio * i.cantidad, 0)
  return { items, subtotal }
}

export function costoEnvio(tipo, subtotal) {
  if (tipo !== "Despacho a domicilio") return 0
  return subtotal >= DESPACHO_GRATIS_DESDE ? 0 : COSTO_DESPACHO
}

/* ---------- Referencias: categorias, regiones y comunas, se cargan una vez al abrir la pagina ---------- */

const referencias = { categorias: [], regiones: [], comunas: [] }

export function setReferencias(datos) {
  Object.assign(referencias, datos)
}

export function getReferencias() {
  return referencias
}

export function nombreCategoria(id) {
  const c = referencias.categorias.find(x => x.id === Number(id))
  return c ? c.nombre : "Sin categoria"
}

export function nombreRegion(id) {
  const r = referencias.regiones.find(x => x.id === Number(id))
  return r ? r.nombre : "-"
}

export function nombreComuna(id) {
  const c = referencias.comunas.find(x => x.id === Number(id))
  return c ? c.nombre : "-"
}

/* ---------- Formatos ---------- */

export function plata(valor) {
  return "$" + Number(valor).toLocaleString("es-CL")
}

export function imgProducto(producto) {
  if (!producto.imagen) return "/img/logo.svg"
  // imagen subida desde el computador (guardada como data url)
  if (producto.imagen.startsWith("data:")) return producto.imagen
  return "/img/" + producto.imagen
}

// precio con el descuento aplicado
export function precioFinal(producto) {
  const d = Number(producto.descuento) || 0
  if (d <= 0) return producto.precio
  return Math.round(producto.precio * (100 - d) / 100)
}

export function enOferta(producto) {
  return (Number(producto.descuento) || 0) > 0
}

export function fechaCorta(iso) {
  return new Date(iso).toLocaleString("es-CL", { dateStyle: "short", timeStyle: "short" })
}
