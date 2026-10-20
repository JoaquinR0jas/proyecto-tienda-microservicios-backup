// datos iniciales de la tienda, los mismos que teniamos en el flask
// se guardan en localstorage la primera vez que se abre la pagina

export const KEYS = {
  usuarios: "ff_usuarios",
  productos: "ff_productos",
  categorias: "ff_categorias",
  regiones: "ff_regiones",
  comunas: "ff_comunas",
  roles: "ff_roles",
  carrito: "ff_carrito",
  contactos: "ff_contactos",
  blog: "ff_blog",
  ordenes: "ff_ordenes",
  sesion: "ff_sesion"
}

const VERSION = "8"

// costo de despacho, gratis sobre cierto monto
export const COSTO_DESPACHO = 3990
export const DESPACHO_GRATIS_DESDE = 50000

export function getColeccion(clave) {
  try {
    return JSON.parse(localStorage.getItem(clave)) || []
  } catch {
    return []
  }
}

export function setColeccion(clave, datos) {
  localStorage.setItem(clave, JSON.stringify(datos))
}

export function plata(valor) {
  return "$" + Number(valor).toLocaleString("es-CL")
}

export function imgProducto(producto) {
  if (!producto.imagen) return "/img/logo.svg"
  // imagen subida desde el computador (guardada como data url)
  if (producto.imagen.startsWith("data:")) return producto.imagen
  return "/img/" + producto.imagen
}

export function initDatos() {
  if (localStorage.getItem("ff_datos_version") !== VERSION) {
    Object.values(KEYS).forEach(k => localStorage.removeItem(k))
    localStorage.setItem("ff_datos_version", VERSION)
  }

  if (!localStorage.getItem(KEYS.roles)) {
    setColeccion(KEYS.roles, [
      { id: 1, nombre: "Administrador" },
      { id: 2, nombre: "Vendedor" },
      { id: 3, nombre: "Cliente" }
    ])
  }

  if (!localStorage.getItem(KEYS.categorias)) {
    setColeccion(KEYS.categorias, [
      { id: 1, nombre: "Poleras", descripcion: "Poleras basicas y estampadas para todos los dias." },
      { id: 2, nombre: "Polerones", descripcion: "Polerones oversized y con capucha." },
      { id: 3, nombre: "Jeans", descripcion: "Jeans y pantalones de distintos cortes." },
      { id: 4, nombre: "Camisas", descripcion: "Camisas frescas para cualquier ocasion." },
      { id: 5, nombre: "Zapatillas", descripcion: "Zapatillas urbanas y deportivas." },
      { id: 6, nombre: "Accesorios", descripcion: "Gorras y complementos para tu look." }
    ])
  }

  if (!localStorage.getItem(KEYS.regiones)) {
    setColeccion(KEYS.regiones, [
      { id: 1, nombre: "Region Metropolitana" },
      { id: 2, nombre: "Valparaiso" },
      { id: 3, nombre: "Biobio" }
    ])
  }

  if (!localStorage.getItem(KEYS.comunas)) {
    setColeccion(KEYS.comunas, [
      { id: 1, regionId: 1, nombre: "Santiago" },
      { id: 2, regionId: 1, nombre: "Providencia" },
      { id: 3, regionId: 1, nombre: "Maipu" },
      { id: 4, regionId: 2, nombre: "Valparaiso" },
      { id: 5, regionId: 2, nombre: "Vina del Mar" },
      { id: 6, regionId: 3, nombre: "Concepcion" },
      { id: 7, regionId: 3, nombre: "Talcahuano" }
    ])
  }

  if (!localStorage.getItem(KEYS.usuarios)) {
    setColeccion(KEYS.usuarios, [
      {
        run: "123456785", nombre: "Admin", apellidos: "FreshFits",
        correo: "admin@gmail.com", password: "admin123",
        fechaNacimiento: "1990-01-01", rolId: 1,
        regionId: 1, comunaId: 1, direccion: "Av. Principal 123",
        estado: "Activo"
      },
      {
        run: "987654325", nombre: "Vendedor", apellidos: "FreshFits",
        correo: "vendedor@gmail.com", password: "vend1234",
        fechaNacimiento: "1992-05-14", rolId: 2,
        regionId: 1, comunaId: 2, direccion: "Calle Venta 456",
        estado: "Activo"
      },
      {
        run: "112223339", nombre: "Camila", apellidos: "Soto Perez",
        correo: "camila@gmail.com", password: "cliente1",
        fechaNacimiento: "1998-03-22", rolId: 3,
        regionId: 2, comunaId: 4, direccion: "Los Aromos 789",
        estado: "Activo"
      },
      {
        run: "201113334", nombre: "Diego", apellidos: "Fernandez Rojas",
        correo: "diego@duoc.cl", password: "diego123",
        fechaNacimiento: "2001-11-09", rolId: 3,
        regionId: 3, comunaId: 6, direccion: "Las Rosas 321",
        estado: "Activo"
      }
    ])
  }

  // descuento es un porcentaje, si es mayor a 0 el producto sale en ofertas
  if (!localStorage.getItem(KEYS.productos)) {
    setColeccion(KEYS.productos, [
      { codigo: "FF-001", nombre: "Polera Fresh Basica", descripcion: "Polera de algodon 100%, corte relajado y comodo para todos los dias.", precio: 14990, descuento: 0, stock: 15, stockCritico: 3, categoriaId: 1, imagen: "productos/polera-basica.webp", estado: "Activo", destacado: true },
      { codigo: "FF-002", nombre: "Poleron Cloud Hoodie", descripcion: "Poleron oversized con capucha, bolsillo canguro y tela afelpada.", precio: 29990, descuento: 20, stock: 20, stockCritico: 5, categoriaId: 2, imagen: "productos/polerones.png", estado: "Activo", destacado: true },
      { codigo: "FF-003", nombre: "Jean Slim Fit", descripcion: "Jean de corte slim, denim lavado medio, elastico y muy comodo.", precio: 24990, descuento: 0, stock: 8, stockCritico: 2, categoriaId: 3, imagen: "productos/pantalones.png", estado: "Activo", destacado: true },
      { codigo: "FF-004", nombre: "Camisa Lino Flow", descripcion: "Camisa manga corta de lino, fresca y perfecta para el verano.", precio: 21990, descuento: 0, stock: 12, stockCritico: 4, categoriaId: 4, imagen: "productos/camisas.png", estado: "Activo", destacado: true },
      { codigo: "FF-005", nombre: "Polera Retro Graphic", descripcion: "Polera con grafico retro, estilo vintage y tela premium.", precio: 17990, descuento: 15, stock: 10, stockCritico: 3, categoriaId: 1, imagen: "productos/poleraretro.webp", estado: "Activo", destacado: false },
      { codigo: "FF-006", nombre: "Zapatillas Urban Run", descripcion: "Zapatillas urbanas ligeras, ideales para tu look del dia a dia.", precio: 39990, descuento: 0, stock: 25, stockCritico: 5, categoriaId: 5, imagen: "productos/zaptilla.png", estado: "Activo", destacado: true },
      { codigo: "FF-007", nombre: "Cargo Street Pants", descripcion: "Pantalon cargo estilo militar, con bolsillos y corte streetwear.", precio: 27990, descuento: 30, stock: 2, stockCritico: 3, categoriaId: 3, imagen: "productos/pantalonescargo.jpg", estado: "Activo", destacado: false },
      { codigo: "FF-008", nombre: "Gorra Fresh Cap", descripcion: "Gorra con logo bordado, ajuste regulable y tela transpirable.", precio: 12990, descuento: 10, stock: 6, stockCritico: 2, categoriaId: 6, imagen: "productos/gorrofresh.jpg", estado: "Activo", destacado: false }
    ])
  }

  if (!localStorage.getItem(KEYS.blog)) {
    setColeccion(KEYS.blog, [
      { id: 1, titulo: "FreshFits abre sus puertas", resumen: "Nace una nueva tienda online de ropa moderna pensada para todos.", imagen: "blog/blooms.png", fecha: "2026-08-01", slug: "detalle-1" },
      { id: 2, titulo: "Los lanzamientos mas esperados de la temporada", resumen: "Repasamos las prendas que marcaran tendencia en los proximos meses.", imagen: "blog/lanzamientoesperados.png", fecha: "2026-08-10", slug: "detalle-2" },
      { id: 3, titulo: "5 consejos para un look streetwear", resumen: "Ideas simples para armar outfits fresh sin complicaciones.", imagen: "blog/5consejos.png", fecha: "2026-08-20", slug: "detalle-3" }
    ])
  }

  if (!localStorage.getItem(KEYS.carrito)) {
    setColeccion(KEYS.carrito, [])
  }

  if (!localStorage.getItem(KEYS.contactos)) {
    setColeccion(KEYS.contactos, [])
  }

  // algunas ordenes de ejemplo para que el panel admin tenga datos
  if (!localStorage.getItem(KEYS.ordenes)) {
    setColeccion(KEYS.ordenes, [
      {
        id: 1, numero: "20260901", fecha: "2026-09-01T12:30:00", estado: "Pagada",
        cliente: { run: "112223339", nombre: "Camila", apellidos: "Soto Perez", correo: "camila@gmail.com" },
        envio: { tipo: "Despacho a domicilio", calle: "Los Aromos 789", depto: "", regionId: 2, comunaId: 4, indicaciones: "" },
        items: [
          { codigo: "FF-001", nombre: "Polera Fresh Basica", imagen: "productos/polera-basica.webp", precio: 14990, cantidad: 2 },
          { codigo: "FF-003", nombre: "Jean Slim Fit", imagen: "productos/pantalones.png", precio: 24990, cantidad: 1 }
        ],
        subtotal: 54970, costoEnvio: 0, total: 54970
      },
      {
        id: 2, numero: "20260915", fecha: "2026-09-15T18:05:00", estado: "Pagada",
        cliente: { run: "201113334", nombre: "Diego", apellidos: "Fernandez Rojas", correo: "diego@duoc.cl" },
        envio: { tipo: "Despacho a domicilio", calle: "Las Rosas 321", depto: "Depto 12", regionId: 3, comunaId: 6, indicaciones: "Dejar en conserjeria" },
        items: [
          { codigo: "FF-006", nombre: "Zapatillas Urban Run", imagen: "productos/zaptilla.png", precio: 39990, cantidad: 1 }
        ],
        subtotal: 39990, costoEnvio: 3990, total: 43980
      },
      {
        id: 3, numero: "20261003", fecha: "2026-10-03T10:45:00", estado: "Rechazada",
        cliente: { run: "112223339", nombre: "Camila", apellidos: "Soto Perez", correo: "camila@gmail.com" },
        envio: { tipo: "Retiro en tienda", calle: "", depto: "", regionId: 2, comunaId: 4, indicaciones: "" },
        items: [
          { codigo: "FF-002", nombre: "Poleron Cloud Hoodie", imagen: "productos/polerones.png", precio: 23992, cantidad: 1 }
        ],
        subtotal: 23992, costoEnvio: 0, total: 23992
      }
    ])
  }
}

export function getSesion() {
  try {
    return JSON.parse(localStorage.getItem(KEYS.sesion)) || null
  } catch {
    return null
  }
}

export function nombreCategoria(id) {
  const c = getColeccion(KEYS.categorias).find(x => x.id === Number(id))
  return c ? c.nombre : "Sin categoria"
}

export function nombreRegion(id) {
  const r = getColeccion(KEYS.regiones).find(x => x.id === Number(id))
  return r ? r.nombre : "-"
}

export function nombreComuna(id) {
  const c = getColeccion(KEYS.comunas).find(x => x.id === Number(id))
  return c ? c.nombre : "-"
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

export function siguienteId(lista) {
  return lista.reduce((max, x) => Math.max(max, x.id || 0), 0) + 1
}

// agrega un producto al carrito, devuelve un mensaje de error o null si salio bien
export function agregarAlCarrito(codigo, cantidad = 1) {
  const prod = getColeccion(KEYS.productos).find(p => p.codigo === codigo)
  if (!prod || prod.stock <= 0) return "Producto sin stock"
  const carrito = getColeccion(KEYS.carrito)
  const item = carrito.find(i => i.codigo === codigo)
  const actual = item ? item.cantidad : 0
  if (actual + cantidad > prod.stock) return "Solo hay " + prod.stock + " unidades disponibles"
  if (item) item.cantidad += cantidad
  else carrito.push({ codigo, cantidad })
  setColeccion(KEYS.carrito, carrito)
  window.dispatchEvent(new Event("carrito-cambio"))
  return null
}

// arma el detalle del carrito con los datos actuales de cada producto
export function detalleCarrito() {
  const prods = getColeccion(KEYS.productos)
  const items = []
  getColeccion(KEYS.carrito).forEach(i => {
    const p = prods.find(x => x.codigo === i.codigo)
    if (p) items.push({ codigo: p.codigo, nombre: p.nombre, imagen: p.imagen, precio: precioFinal(p), cantidad: i.cantidad, stock: p.stock })
  })
  const subtotal = items.reduce((s, i) => s + i.precio * i.cantidad, 0)
  return { items, subtotal }
}

export function costoEnvio(tipo, subtotal) {
  if (tipo !== "Despacho a domicilio") return 0
  return subtotal >= DESPACHO_GRATIS_DESDE ? 0 : COSTO_DESPACHO
}

// registra la orden; si fue pagada descuenta el stock y vacia el carrito
export function registrarOrden({ cliente, envio, items, subtotal, estado }) {
  const ordenes = getColeccion(KEYS.ordenes)
  const id = siguienteId(ordenes)
  const ahora = new Date()
  const numero = ahora.toISOString().slice(0, 10).replaceAll("-", "") + String(id).padStart(3, "0")
  const envioCosto = costoEnvio(envio.tipo, subtotal)
  const orden = {
    id, numero, fecha: ahora.toISOString(), estado,
    cliente, envio,
    items: items.map(i => ({ codigo: i.codigo, nombre: i.nombre, imagen: i.imagen, precio: i.precio, cantidad: i.cantidad })),
    subtotal, costoEnvio: envioCosto, total: subtotal + envioCosto
  }
  ordenes.push(orden)
  setColeccion(KEYS.ordenes, ordenes)

  if (estado === "Pagada") {
    const prods = getColeccion(KEYS.productos)
    orden.items.forEach(i => {
      const p = prods.find(x => x.codigo === i.codigo)
      if (p) p.stock = Math.max(0, p.stock - i.cantidad)
    })
    setColeccion(KEYS.productos, prods)
    setColeccion(KEYS.carrito, [])
    window.dispatchEvent(new Event("carrito-cambio"))
  }
  return orden
}

export function ordenesDeUsuario(usuario) {
  return getColeccion(KEYS.ordenes).filter(o =>
    o.cliente.run === usuario.run || o.cliente.correo.toLowerCase() === usuario.correo.toLowerCase()
  )
}

export function fechaCorta(iso) {
  return new Date(iso).toLocaleString("es-CL", { dateStyle: "short", timeStyle: "short" })
}
