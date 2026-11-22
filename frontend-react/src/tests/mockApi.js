// Backend falso para las pruebas: reemplaza fetch con un spy de Jasmine que responde
// como lo haria el API Gateway, asi las pruebas no necesitan los microservicios encendidos.

const p = (id, codigo, nombre, precio, descuento, stock, categoriaId, imagen, destacado) => ({
  id, codigo, nombre, descripcion: "", precio, descuento, stock, stockCritico: 2,
  categoriaId, imagen, estado: "ACTIVO", destacado,
  precioFinal: Math.round(precio * (100 - descuento) / 100)
})

export const PRODUCTOS = [
  p(1, "FF-001", "Polera Fresh Basica", 14990, 0, 15, 1, "productos/polera-basica.webp", true),
  p(2, "FF-002", "Poleron Cloud Hoodie", 29990, 20, 20, 2, "productos/polerones.png", true),
  p(3, "FF-003", "Jean Slim Fit", 24990, 0, 8, 3, "productos/pantalones.png", true),
  p(4, "FF-004", "Camisa Lino Flow", 21990, 0, 12, 4, "productos/camisas.png", true),
  p(5, "FF-005", "Polera Retro Graphic", 17990, 15, 10, 1, "productos/poleraretro.webp", false),
  p(6, "FF-006", "Zapatillas Urban Run", 39990, 0, 25, 5, "productos/zaptilla.png", true),
  p(7, "FF-007", "Cargo Street Pants", 27990, 30, 2, 3, "productos/pantalonescargo.jpg", false),
  p(8, "FF-008", "Gorra Fresh Cap", 12990, 10, 6, 6, "productos/gorrofresh.jpg", false)
]

export const CATEGORIAS = [
  { id: 1, nombre: "Poleras" }, { id: 2, nombre: "Polerones" }, { id: 3, nombre: "Jeans" },
  { id: 4, nombre: "Camisas" }, { id: 5, nombre: "Zapatillas" }, { id: 6, nombre: "Accesorios" }
]

function respuesta(datos, status = 200) {
  return Promise.resolve(new Response(datos === undefined ? "" : JSON.stringify(datos), { status }))
}

// Instala el backend falso. Llamar en el beforeEach de cada prueba.
export function mockApi() {
  return spyOn(window, "fetch").and.callFake((url, opciones = {}) => {
    const ruta = new URL(url).pathname
    const metodo = opciones.method || "GET"

    if (ruta === "/api/v1/catalogo/productos") return respuesta(PRODUCTOS)
    if (ruta === "/api/v1/catalogo/productos/ofertas") return respuesta(PRODUCTOS.filter(x => x.descuento > 0))
    if (ruta === "/api/v1/catalogo/categorias") return respuesta(CATEGORIAS)
    if (ruta === "/api/v1/usuarios/regiones" || ruta === "/api/v1/usuarios/comunas") return respuesta([])
    if (ruta === "/api/v1/contenido/articulos") return respuesta([])
    if (ruta === "/api/v1/contenido/mensajes" && metodo === "POST") {
      return respuesta({ id: 1, ...JSON.parse(opciones.body), fecha: new Date().toISOString() }, 201)
    }
    return respuesta("No encontrado", 404)
  })
}

// Cuantas veces se llamo a una ruta del backend con un metodo (ej: POST /api/v1/contenido/mensajes)
export function llamadasA(spy, ruta, metodo = "GET") {
  return spy.calls.allArgs().filter(([url, op = {}]) =>
    new URL(url).pathname === ruta && (op.method || "GET") === metodo
  ).length
}
