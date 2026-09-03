/*
 * MODAVIVA - logica de la pagina
 * Proyecto FullStack II - Duoc UC
 *
 * La pagina se conecta al backend (microservicios) que corre en localhost:8080
 * Si el backend no esta corriendo, mostramos datos de ejemplo para que la
 * pagina no se vea vacia (nos paso en la prueba xd)
 */

// direccion del API Gateway donde estan todos los microservicios
const API = "http://localhost:8080/api";

// datos de ejemplo por si el backend no esta disponible
const productosEjemplo = [
    { id: 1, nombre: "Polera Unisex", descripcion: "Polera de algodon 100%, varios colores", precio: 9990, stockDisponible: 50 },
    { id: 2, nombre: "Jeans Clasico", descripcion: "Jeans corte clasico, tela elastizada", precio: 18990, stockDisponible: 30 },
    { id: 3, nombre: "Zapatillas Urbanas", descripcion: "Zapatillas comodas para el dia a dia", precio: 24990, stockDisponible: 20 },
    { id: 4, nombre: "Parka Invierno", descripcion: "Parka con capucha y forro termico", precio: 32990, stockDisponible: 15 },
    { id: 5, nombre: "Vestido Floral", descripcion: "Vestido ligero ideal para la primavera", precio: 14990, stockDisponible: 25 },
    { id: 6, nombre: "Gorra Snapback", descripcion: "Gorra ajustable estilo urbano", precio: 7990, stockDisponible: 40 }
];

const ofertasEjemplo = [
    { id: 1, nombre: "Black Friday", descripcion: "Todo con descuento", descuentoPorcentaje: 30 },
    { id: 2, nombre: "Cyber Monday", descripcion: "Solo por 24 horas", descuentoPorcentaje: 20 },
    { id: 3, nombre: "Liquidacion", descripcion: "Ultimas unidades", descuentoPorcentaje: 50 }
];

// variable que guarda los productos que estan en el carrito
let carrito = [];

// --- CARGA INICIAL ---
document.addEventListener("DOMContentLoaded", function () {
    cargarProductos();
    cargarOfertas();
    // si hay algo guardado en el navegador, lo cargamos
    let guardado = localStorage.getItem("carrito");
    if (guardado) {
        carrito = JSON.parse(guardado);
        actualizarCarrito();
    }

    // eventos del carrito
    document.getElementById("btnCarrito").addEventListener("click", abrirCarrito);
    document.getElementById("cerrarCarrito").addEventListener("click", cerrarCarrito);
    document.getElementById("capaOscura").addEventListener("click", cerrarCarrito);
    document.getElementById("vaciarCarrito").addEventListener("click", vaciarCarrito);

    // envio del formulario de contacto
    document.getElementById("formContacto").addEventListener("submit", function (e) {
        e.preventDefault();
        alert("Mensaje enviado. Te contactaremos pronto :)");
        e.target.reset();
    });
});

// --- PRODUCTOS ---
function cargarProductos() {
    fetch(API + "/catalogo/productos")
        .then(function (resp) {
            return resp.json();
        })
        .then(function (lista) {
            // por cada producto que venga del backend, mostramos su tarjeta
            let html = "";
            lista.forEach(function (producto) {
                html += armarTarjeta(producto);
            });
            document.getElementById("listaProductos").innerHTML = html;
        })
        .catch(function () {
            // si no hay backend, mostramos los productos de ejemplo
            let html = "";
            productosEjemplo.forEach(function (producto) {
                html += armarTarjeta(producto);
            });
            document.getElementById("listaProductos").innerHTML = html;
        });
}

// arma el html de una tarjeta de producto
function armarTarjeta(p) {
    return `
        <div class="producto-card">
            <div class="imagen">&#128085;</div>
            <div class="info">
                <h3>${p.nombre}</h3>
                <p class="descripcion">${p.descripcion || "Sin descripcion"}</p>
                <p class="precio">$${formatearPrecio(p.precio)}</p>
                <p class="stock">Stock: ${p.stockDisponible != null ? p.stockDisponible : "?"} unidades</p>
                <button onclick="agregarAlCarrito(${p.id}, '${p.nombre}', ${p.precio})" class="btn-agregar">Agregar al carrito</button>
            </div>
        </div>`;
}

// --- OFERTAS ---
function cargarOfertas() {
    fetch(API + "/marketing/promociones/activas")
        .then(function (resp) {
            return resp.json();
        })
        .then(function (lista) {
            let html = "";
            lista.forEach(function (oferta) {
                html += `
                    <div class="oferta-card">
                        <h3>${oferta.nombre}</h3>
                        <p class="descuento">-${oferta.descuentoPorcentaje}%</p>
                        <p>${oferta.descripcion}</p>
                    </div>`;
            });
            document.getElementById("listaOfertas").innerHTML = html;
        })
        .catch(function () {
            let html = "";
            ofertasEjemplo.forEach(function (oferta) {
                html += `
                    <div class="oferta-card">
                        <h3>${oferta.nombre}</h3>
                        <p class="descuento">-${oferta.descuentoPorcentaje}%</p>
                        <p>${oferta.descripcion}</p>
                    </div>`;
            });
            document.getElementById("listaOfertas").innerHTML = html;
        });
}

// --- CARRITO ---
// agrega un producto al carrito (uno solo por click, si ya esta suma la cantidad)
function agregarAlCarrito(id, nombre, precio) {
    let encontrado = carrito.find(function (item) {
        return item.id === id;
    });

    if (encontrado) {
        encontrado.cantidad = encontrado.cantidad + 1;
    } else {
        carrito.push({ id: id, nombre: nombre, precio: precio, cantidad: 1 });
    }

    guardarCarrito();
    actualizarCarrito();
    alert(nombre + " agregado al carrito");
}

// guarda el carrito en el navegador para que no se pierda al recargar
function guardarCarrito() {
    localStorage.setItem("carrito", JSON.stringify(carrito));
}

// vuelve a dibujar el carrito y el contador
function actualizarCarrito() {
    // contador de la barra
    let totalItems = 0;
    carrito.forEach(function (item) {
        totalItems = totalItems + item.cantidad;
    });
    document.getElementById("contador").textContent = totalItems;

    // lista de items
    let contenedor = document.getElementById("itemsCarrito");
    if (carrito.length === 0) {
        contenedor.innerHTML = "<p>Tu carrito esta vacio</p>";
        document.getElementById("totalCarrito").textContent = "$0";
        return;
    }

    let html = "";
    let total = 0;
    carrito.forEach(function (item) {
        let subtotal = item.precio * item.cantidad;
        total = total + subtotal;
        html += `
            <div class="item-carrito">
                <span class="nombre">${item.nombre}</span>
                <span>x${item.cantidad}</span>
                <span>$${formatearPrecio(subtotal)}</span>
            </div>`;
    });
    contenedor.innerHTML = html;
    document.getElementById("totalCarrito").textContent = "$" + formatearPrecio(total);
}

function vaciarCarrito() {
    carrito = [];
    guardarCarrito();
    actualizarCarrito();
}

// --- PANEL DEL CARRITO ---
function abrirCarrito() {
    document.getElementById("panelCarrito").classList.add("abierto");
    document.getElementById("capaOscura").classList.add("visible");
}

function cerrarCarrito() {
    document.getElementById("panelCarrito").classList.remove("abierto");
    document.getElementById("capaOscura").classList.remove("visible");
}

// --- UTILIDADES ---
// formatea el numero para que se vea con separador de miles, ej: 12.345
function formatearPrecio(precio) {
    return precio.toLocaleString("es-CL");
}
