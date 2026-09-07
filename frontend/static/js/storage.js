/* FreshFits - storage.js
   Maneja las colecciones de localStorage (base de datos simulada),
   la sesion del usuario y la barra de navegacion comun a todas las paginas. */

const NEXO_KEYS = {
    usuarios: "ff_usuarios",
    productos: "ff_productos",
    categorias: "ff_categorias",
    regiones: "ff_regiones",
    comunas: "ff_comunas",
    roles: "ff_roles",
    carrito: "ff_carrito",
    contactos: "ff_contactos",
    blog: "ff_blog",
    sesion: "ff_sesion"
};

function obtenerColeccion(clave) {
    return JSON.parse(localStorage.getItem(clave)) || [];
}

function guardarColeccion(clave, datos) {
    localStorage.setItem(clave, JSON.stringify(datos));
}

/* ---------- Datos iniciales de prueba ---------- */

/* Version de los datos semilla. Al cambiarla, se re-siembran todas las
   colecciones en la proxima carga (borra el localStorage del sitio). */
const FF_DATOS_VERSION = "7";

function inicializarDatos() {
    if (localStorage.getItem("ff_datos_version") !== FF_DATOS_VERSION) {
        Object.keys(NEXO_KEYS).forEach(function (clave) {
            localStorage.removeItem(NEXO_KEYS[clave]);
        });
        localStorage.setItem("ff_datos_version", FF_DATOS_VERSION);
    }

    if (!localStorage.getItem(NEXO_KEYS.roles)) {
        guardarColeccion(NEXO_KEYS.roles, [
            { id: 1, nombre: "Administrador" },
            { id: 2, nombre: "Vendedor" },
            { id: 3, nombre: "Cliente" }
        ]);
    }

    if (!localStorage.getItem(NEXO_KEYS.categorias)) {
        guardarColeccion(NEXO_KEYS.categorias, [
            { id: 1, nombre: "Poleras" },
            { id: 2, nombre: "Polerones" },
            { id: 3, nombre: "Jeans" },
            { id: 4, nombre: "Camisas" },
            { id: 5, nombre: "Zapatillas" },
            { id: 6, nombre: "Accesorios" }
        ]);
    }

    if (!localStorage.getItem(NEXO_KEYS.regiones)) {
        guardarColeccion(NEXO_KEYS.regiones, [
            { id: 1, nombre: "Region Metropolitana" },
            { id: 2, nombre: "Valparaiso" },
            { id: 3, nombre: "Biobio" }
        ]);
    }

    if (!localStorage.getItem(NEXO_KEYS.comunas)) {
        guardarColeccion(NEXO_KEYS.comunas, [
            { id: 1, regionId: 1, nombre: "Santiago" },
            { id: 2, regionId: 1, nombre: "Providencia" },
            { id: 3, regionId: 1, nombre: "Maipu" },
            { id: 4, regionId: 2, nombre: "Valparaiso" },
            { id: 5, regionId: 2, nombre: "Vina del Mar" },
            { id: 6, regionId: 3, nombre: "Concepcion" },
            { id: 7, regionId: 3, nombre: "Talcahuano" }
        ]);
    }

    if (!localStorage.getItem(NEXO_KEYS.usuarios)) {
        guardarColeccion(NEXO_KEYS.usuarios, [
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
        ]);
    }

    if (!localStorage.getItem(NEXO_KEYS.productos)) {
        guardarColeccion(NEXO_KEYS.productos, [
            { codigo: "FF-001", nombre: "Polera Fresh Basica", descripcion: "Polera de algodon 100%, corte relajado y comodo para todos los dias.", precio: 14990, stock: 15, stockCritico: 3, categoriaId: 1, imagen: "productos/polera-basica.webp", estado: "Activo", destacado: true },
            { codigo: "FF-002", nombre: "Poleron Cloud Hoodie", descripcion: "Poleron oversized con capucha, bolsillo canguro y tela afelpada.", precio: 29990, stock: 20, stockCritico: 5, categoriaId: 2, imagen: "productos/polerones.png", estado: "Activo", destacado: true },
            { codigo: "FF-003", nombre: "Jean Slim Fit", descripcion: "Jean de corte slim, denim lavado medio, elastico y muy comodo.", precio: 24990, stock: 8, stockCritico: 2, categoriaId: 3, imagen: "productos/pantalones.png", estado: "Activo", destacado: true },
            { codigo: "FF-004", nombre: "Camisa Lino Flow", descripcion: "Camisa manga corta de lino, fresca y perfecta para el verano.", precio: 21990, stock: 12, stockCritico: 4, categoriaId: 4, imagen: "productos/camisas.png", estado: "Activo", destacado: true },
            { codigo: "FF-005", nombre: "Polera Retro Graphic", descripcion: "Polera con grafico retro, estilo vintage y tela premium.", precio: 17990, stock: 10, stockCritico: 3, categoriaId: 1, imagen: "productos/poleraretro.webp", estado: "Activo", destacado: false },
            { codigo: "FF-006", nombre: "Zapatillas Urban Run", descripcion: "Zapatillas urbanas ligeras, ideales para tu look del dia a dia.", precio: 39990, stock: 25, stockCritico: 5, categoriaId: 5, imagen: "productos/zaptilla.png", estado: "Activo", destacado: true },
            { codigo: "FF-007", nombre: "Cargo Street Pants", descripcion: "Pantalon cargo estilo militar, con bolsillos y corte streetwear.", precio: 27990, stock: 2, stockCritico: 3, categoriaId: 3, imagen: "productos/pantalonescargo.jpg", estado: "Activo", destacado: false },
            { codigo: "FF-008", nombre: "Gorra Fresh Cap", descripcion: "Gorra con logo bordado, ajuste regulable y tela transpirable.", precio: 12990, stock: 6, stockCritico: 2, categoriaId: 6, imagen: "productos/gorrofresh.jpg", estado: "Activo", destacado: false }
        ]);
    }

    if (!localStorage.getItem(NEXO_KEYS.blog)) {
        guardarColeccion(NEXO_KEYS.blog, [
            { id: 1, titulo: "FreshFits abre sus puertas", resumen: "Nace una nueva tienda online de ropa moderna pensada para todos.", imagen: "blog/blooms.png", fecha: "2026-08-01", slug: "detalle-1" },
            { id: 2, titulo: "Los lanzamientos mas esperados de la temporada", resumen: "Repasamos las prendas que marcaran tendencia en los proximos meses.", imagen: "blog/lanzamientoesperados.png", fecha: "2026-08-10", slug: "detalle-2" },
            { id: 3, titulo: "5 consejos para un look streetwear", resumen: "Ideas simples para armar outfits fresh sin complicaciones.", imagen: "blog/5consejos.png", fecha: "2026-08-20", slug: "detalle-3" }
        ]);
    }

    if (!localStorage.getItem(NEXO_KEYS.carrito)) {
        guardarColeccion(NEXO_KEYS.carrito, []);
    }

    if (!localStorage.getItem(NEXO_KEYS.contactos)) {
        guardarColeccion(NEXO_KEYS.contactos, []);
    }
}

/* ---------- Sesion ---------- */

function obtenerSesion() {
    return JSON.parse(localStorage.getItem(NEXO_KEYS.sesion)) || null;
}

function guardarSesion(usuario) {
    localStorage.setItem(NEXO_KEYS.sesion, JSON.stringify(usuario));
}

function cerrarSesion() {
    localStorage.removeItem(NEXO_KEYS.sesion);
    window.location.href = "/login";
}

function obtenerNombreRol(rolId) {
    const rol = obtenerColeccion(NEXO_KEYS.roles).find(function (r) { return r.id === rolId; });
    return rol ? rol.nombre : "";
}

/* Protege paginas administrativas segun el rol permitido. */
function protegerPaginaAdmin(rolesPermitidos) {
    const sesion = obtenerSesion();
    if (!sesion || rolesPermitidos.indexOf(sesion.rolId) === -1) {
        Swal.fire({
            title: "Acceso restringido",
            text: "Debes iniciar sesion con una cuenta autorizada para ver esta pagina.",
            icon: "warning",
            confirmButtonText: "Ir a Iniciar sesion"
        }).then(function () {
            window.location.href = "/login";
        });
        return false;
    }
    return true;
}

/* ---------- Navbar dinamica ---------- */

function actualizarNavbar() {
    const sesion = obtenerSesion();
    const navInvitado = document.getElementById("navInvitado");
    const navUsuario = document.getElementById("navUsuario");
    const navUsuarioNombre = document.getElementById("navUsuarioNombre");
    const navAdminItem = document.getElementById("navAdminItem");
    const btnCerrarSesion = document.getElementById("btnCerrarSesion");

    if (!navInvitado || !navUsuario) return;

    if (sesion) {
        navInvitado.style.display = "none";
        navUsuario.style.display = "block";
        if (navUsuarioNombre) navUsuarioNombre.textContent = sesion.nombre;
        if (navAdminItem && (sesion.rolId === 1 || sesion.rolId === 2)) {
            navAdminItem.style.display = "block";
        }
    } else {
        navInvitado.style.display = "flex";
        navUsuario.style.display = "none";
        if (navAdminItem) navAdminItem.style.display = "none";
    }

    if (btnCerrarSesion) {
        btnCerrarSesion.addEventListener("click", function (evento) {
            evento.preventDefault();
            cerrarSesion();
        });
    }
}

/* ---------- Badge del carrito ---------- */

function actualizarBadgeCarrito() {
    const carrito = obtenerColeccion(NEXO_KEYS.carrito);
    let totalItems = 0;
    for (let i = 0; i < carrito.length; i++) {
        totalItems += carrito[i].cantidad;
    }
    const badge = document.getElementById("carritoBadge");
    if (!badge) return;
    if (totalItems > 0) {
        badge.textContent = totalItems;
        badge.style.display = "block";
    } else {
        badge.style.display = "none";
    }
}

/* Oculta la seccion de Usuarios en el menu administrativo para el rol Vendedor. */
function ocultarUsuariosSiVendedor() {
    const sesion = obtenerSesion();
    if (!sesion || sesion.rolId !== 2) return;
    const enlaceSidebar = document.getElementById("sidebarUsuariosLink");
    const enlaceOffcanvas = document.getElementById("offcanvasUsuariosLink");
    if (enlaceSidebar) enlaceSidebar.style.display = "none";
    if (enlaceOffcanvas) enlaceOffcanvas.style.display = "none";
}

/* Marca como activo el enlace del navbar/sidebar que corresponde a la pagina actual. */
function marcarEnlaceActivo() {
    const rutaActual = window.location.pathname;
    document.querySelectorAll(".nav-link").forEach(function (enlace) {
        const href = enlace.getAttribute("href");
        if (href && href !== "#" && href === rutaActual) {
            enlace.classList.add("active");
        }
    });
}

function formatearPrecio(valor) {
    return "$" + Number(valor).toLocaleString("es-CL");
}

/* Mueve la linea indicadora del navbar bajo el enlace indicado o el activo. */
function moverIndicadorNav(enlace) {
    const contenedor = document.getElementById("navMenuPrincipal");
    const indicador = document.getElementById("navIndicator");
    if (!contenedor || !indicador) return;
    const destino = enlace || contenedor.querySelector(".nav-link.active");
    if (!destino) return;

    const rectLink = destino.getBoundingClientRect();
    const rectContenedor = contenedor.getBoundingClientRect();
    indicador.style.left = (rectLink.left - rectContenedor.left) + "px";
    indicador.style.width = rectLink.width + "px";
}

function inicializarIndicadorNav() {
    const contenedor = document.getElementById("navMenuPrincipal");
    const indicador = document.getElementById("navIndicator");
    if (!contenedor || !indicador) return;

    moverIndicadorNav(null);

    contenedor.querySelectorAll(".nav-link").forEach(function (enlace) {
        enlace.addEventListener("mouseenter", function () {
            moverIndicadorNav(enlace);
        });
        enlace.addEventListener("mouseleave", function () {
            moverIndicadorNav(null);
        });
    });

    window.addEventListener("resize", function () {
        moverIndicadorNav(null);
    });
}

document.addEventListener("DOMContentLoaded", function () {
    inicializarDatos();
    actualizarNavbar();
    actualizarBadgeCarrito();
    marcarEnlaceActivo();
    inicializarIndicadorNav();
});