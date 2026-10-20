import { useEffect } from 'react'
import { Routes, Route, useLocation } from 'react-router-dom'
import Navbar from './components/Navbar.jsx'
import Footer from './components/Footer.jsx'
import Home from './pages/Home.jsx'
import Productos from './pages/Productos.jsx'
import Detalle from './pages/Detalle.jsx'
import Ofertas from './pages/Ofertas.jsx'
import Carrito from './pages/Carrito.jsx'
import Checkout from './pages/Checkout.jsx'
import CompraExitosa from './pages/CompraExitosa.jsx'
import CompraError from './pages/CompraError.jsx'
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

export default function App() {
  const ruta = useLocation().pathname

  // al cambiar de pagina se vuelve arriba
  useEffect(() => {
    window.scrollTo(0, 0)
  }, [ruta])

  return (
    <>
      <Navbar />
      <main>
        <Routes>
          {/* tienda */}
          <Route path="/" element={<Home />} />
          <Route path="/productos" element={<Productos />} />
          <Route path="/producto/:codigo" element={<Detalle />} />
          <Route path="/ofertas" element={<Ofertas />} />
          <Route path="/carrito" element={<Carrito />} />
          <Route path="/checkout" element={<Checkout />} />
          <Route path="/compra/exitosa/:id" element={<CompraExitosa />} />
          <Route path="/compra/error/:id" element={<CompraError />} />
          <Route path="/login" element={<Login />} />
          <Route path="/registro" element={<Registro />} />
          <Route path="/blog" element={<Blog />} />
          <Route path="/blog/:slug" element={<BlogDetalle />} />
          <Route path="/nosotros" element={<Nosotros />} />
          <Route path="/contacto" element={<Contacto />} />

          {/* administrador */}
          <Route path="/admin" element={<Admin />} />
          <Route path="/admin/ordenes" element={<AdminOrdenes />} />
          <Route path="/admin/ordenes/:id" element={<AdminBoleta />} />
          <Route path="/admin/productos" element={<AdminProductos />} />
          <Route path="/admin/productos/nuevo" element={<AdminProductoForm />} />
          <Route path="/admin/productos/criticos" element={<AdminProductosCriticos />} />
          <Route path="/admin/productos/reportes" element={<AdminProductosReportes />} />
          <Route path="/admin/productos/ver/:codigo" element={<AdminProductoVer />} />
          <Route path="/admin/productos/:codigo" element={<AdminProductoForm />} />
          <Route path="/admin/categorias" element={<AdminCategorias />} />
          <Route path="/admin/categorias/nueva" element={<AdminCategoriaForm />} />
          <Route path="/admin/categorias/:id" element={<AdminCategoriaForm />} />
          <Route path="/admin/usuarios" element={<AdminUsuarios />} />
          <Route path="/admin/usuarios/nuevo" element={<AdminUsuarioForm />} />
          <Route path="/admin/usuarios/ver/:run" element={<AdminUsuarioVer />} />
          <Route path="/admin/usuarios/:run/historial" element={<AdminUsuarioHistorial />} />
          <Route path="/admin/usuarios/:run" element={<AdminUsuarioForm />} />
          <Route path="/admin/reportes" element={<AdminReportes />} />
          <Route path="/admin/perfil" element={<AdminPerfil />} />
          <Route path="/admin/mensajes" element={<AdminMensajes />} />
        </Routes>
      </main>
      <Footer />
    </>
  )
}
