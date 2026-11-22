import { useEffect } from 'react'
import { Routes, Route, useLocation } from 'react-router-dom'
import Navbar from './components/Navbar.jsx'
import Footer from './components/Footer.jsx'
import Cargando, { useCarga } from './components/Cargando.jsx'
import { cargarReferencias } from './api.js'
import Home from './pages/Home.jsx'
import Productos from './pages/Productos.jsx'
import Detalle from './pages/Detalle.jsx'
import Ofertas from './pages/Ofertas.jsx'
import Carrito from './pages/Carrito.jsx'
import Checkout from './pages/Checkout.jsx'
import CompraExitosa from './pages/CompraExitosa.jsx'
import CompraError from './pages/CompraError.jsx'
import MisCompras from './pages/MisCompras.jsx'
import MiCompraDetalle from './pages/MiCompraDetalle.jsx'
import Login from './pages/Login.jsx'
import Registro from './pages/Registro.jsx'
import Blog from './pages/Blog.jsx'
import BlogDetalle from './pages/BlogDetalle.jsx'
import Nosotros from './pages/Nosotros.jsx'
import Contacto from './pages/Contacto.jsx'
import Admin from './pages/Admin.jsx'
import AdminOrdenes from './pages/AdminOrdenes.jsx'
import AdminBoleta from './pages/AdminBoleta.jsx'
import AdminProductos from './pages/AdminProductos.jsx'
import AdminProductoForm from './pages/AdminProductoForm.jsx'
import AdminProductoVer from './pages/AdminProductoVer.jsx'
import AdminProductosCriticos from './pages/AdminProductosCriticos.jsx'
import AdminProductosReportes from './pages/AdminProductosReportes.jsx'
import AdminCategorias from './pages/AdminCategorias.jsx'
import AdminCategoriaForm from './pages/AdminCategoriaForm.jsx'
import AdminUsuarios from './pages/AdminUsuarios.jsx'
import AdminUsuarioForm from './pages/AdminUsuarioForm.jsx'
import AdminUsuarioVer from './pages/AdminUsuarioVer.jsx'
import AdminUsuarioHistorial from './pages/AdminUsuarioHistorial.jsx'
import AdminReportes from './pages/AdminReportes.jsx'
import AdminPerfil from './pages/AdminPerfil.jsx'
import AdminMensajes from './pages/AdminMensajes.jsx'
import RutaAdmin from './components/RutaAdmin.jsx'

export default function App() {
  const ruta = useLocation().pathname
  // categorias, regiones y comunas se cargan una vez y las usan muchas paginas
  const referencias = useCarga(cargarReferencias)

  useEffect(() => {
    window.scrollTo(0, 0)
  }, [ruta])

  if (referencias.cargando || referencias.error) {
    return (
      <>
        <Navbar />
        <main className="container py-5">
          <Cargando cargando={referencias.cargando} error={referencias.error} onReintentar={referencias.recargar} />
        </main>
        <Footer />
      </>
    )
  }

  return (
    <>
      <Navbar />
      <main>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/productos" element={<Productos />} />
          <Route path="/producto/:codigo" element={<Detalle />} />
          <Route path="/ofertas" element={<Ofertas />} />
          <Route path="/carrito" element={<Carrito />} />
          <Route path="/checkout" element={<Checkout />} />
          <Route path="/compra/exitosa/:id" element={<CompraExitosa />} />
          <Route path="/compra/error/:id" element={<CompraError />} />
          <Route path="/mis-compras" element={<MisCompras />} />
          <Route path="/mis-compras/:id" element={<MiCompraDetalle />} />
          <Route path="/login" element={<Login />} />
          <Route path="/registro" element={<Registro />} />
          <Route path="/blog" element={<Blog />} />
          <Route path="/blog/:slug" element={<BlogDetalle />} />
          <Route path="/nosotros" element={<Nosotros />} />
          <Route path="/contacto" element={<Contacto />} />

          {/* Panel de administracion. Cada ruta pasa por RutaAdmin, que revisa
              la sesion y el rol antes de mostrar la pagina. */}
          <Route path="/admin" element={<RutaAdmin><Admin /></RutaAdmin>} />
          <Route path="/admin/ordenes" element={<RutaAdmin><AdminOrdenes /></RutaAdmin>} />
          <Route path="/admin/ordenes/:id" element={<RutaAdmin><AdminBoleta /></RutaAdmin>} />
          <Route path="/admin/productos" element={<RutaAdmin><AdminProductos /></RutaAdmin>} />
          <Route path="/admin/productos/nuevo" element={<RutaAdmin soloAdmin><AdminProductoForm /></RutaAdmin>} />
          <Route path="/admin/productos/criticos" element={<RutaAdmin soloAdmin><AdminProductosCriticos /></RutaAdmin>} />
          <Route path="/admin/productos/reportes" element={<RutaAdmin soloAdmin><AdminProductosReportes /></RutaAdmin>} />
          <Route path="/admin/productos/ver/:codigo" element={<RutaAdmin><AdminProductoVer /></RutaAdmin>} />
          <Route path="/admin/productos/:codigo" element={<RutaAdmin soloAdmin><AdminProductoForm /></RutaAdmin>} />
          <Route path="/admin/categorias" element={<RutaAdmin soloAdmin><AdminCategorias /></RutaAdmin>} />
          <Route path="/admin/categorias/nueva" element={<RutaAdmin soloAdmin><AdminCategoriaForm /></RutaAdmin>} />
          <Route path="/admin/categorias/:id" element={<RutaAdmin soloAdmin><AdminCategoriaForm /></RutaAdmin>} />
          <Route path="/admin/usuarios" element={<RutaAdmin soloAdmin><AdminUsuarios /></RutaAdmin>} />
          <Route path="/admin/usuarios/nuevo" element={<RutaAdmin soloAdmin><AdminUsuarioForm /></RutaAdmin>} />
          <Route path="/admin/usuarios/ver/:run" element={<RutaAdmin soloAdmin><AdminUsuarioVer /></RutaAdmin>} />
          <Route path="/admin/usuarios/:run/historial" element={<RutaAdmin soloAdmin><AdminUsuarioHistorial /></RutaAdmin>} />
          <Route path="/admin/usuarios/:run" element={<RutaAdmin soloAdmin><AdminUsuarioForm /></RutaAdmin>} />
          <Route path="/admin/reportes" element={<RutaAdmin soloAdmin><AdminReportes /></RutaAdmin>} />
          <Route path="/admin/perfil" element={<RutaAdmin soloAdmin><AdminPerfil /></RutaAdmin>} />
          <Route path="/admin/mensajes" element={<RutaAdmin soloAdmin><AdminMensajes /></RutaAdmin>} />
        </Routes>
      </main>
      <Footer />
    </>
  )
}
