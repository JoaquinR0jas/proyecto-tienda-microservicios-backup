import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar.jsx'
import Footer from './components/Footer.jsx'
import Home from './pages/Home.jsx'
import Productos from './pages/Productos.jsx'
import Detalle from './pages/Detalle.jsx'
import Carrito from './pages/Carrito.jsx'
import Login from './pages/Login.jsx'
import Registro from './pages/Registro.jsx'
import Blog from './pages/Blog.jsx'
import BlogDetalle from './pages/BlogDetalle.jsx'
import Nosotros from './pages/Nosotros.jsx'
import Contacto from './pages/Contacto.jsx'
import Admin from './pages/Admin.jsx'
import AdminProductos from './pages/AdminProductos.jsx'
import AdminProductoForm from './pages/AdminProductoForm.jsx'
import AdminUsuarios from './pages/AdminUsuarios.jsx'
import AdminUsuarioForm from './pages/AdminUsuarioForm.jsx'

export default function App() {
  return (
    <>
      <Navbar />
      <main>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/productos" element={<Productos />} />
          <Route path="/producto/:codigo" element={<Detalle />} />
          <Route path="/carrito" element={<Carrito />} />
          <Route path="/login" element={<Login />} />
          <Route path="/registro" element={<Registro />} />
          <Route path="/blog" element={<Blog />} />
          <Route path="/blog/:slug" element={<BlogDetalle />} />
          <Route path="/nosotros" element={<Nosotros />} />
          <Route path="/contacto" element={<Contacto />} />
          <Route path="/admin" element={<Admin />} />
          <Route path="/admin/productos" element={<AdminProductos />} />
          <Route path="/admin/productos/nuevo" element={<AdminProductoForm />} />
          <Route path="/admin/productos/:codigo" element={<AdminProductoForm />} />
          <Route path="/admin/usuarios" element={<AdminUsuarios />} />
          <Route path="/admin/usuarios/nuevo" element={<AdminUsuarioForm />} />
          <Route path="/admin/usuarios/:run" element={<AdminUsuarioForm />} />
        </Routes>
      </main>
      <Footer />
    </>
  )
}
