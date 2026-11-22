import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import 'bootstrap/dist/css/bootstrap.min.css'
import App from './App.jsx'
import './estilos.css'
import { getSesion, cerrarSesion } from './datos.js'

// limpieza de la version anterior, cuando todo se guardaba en localStorage
;["ff_usuarios", "ff_productos", "ff_categorias", "ff_regiones", "ff_comunas", "ff_roles",
  "ff_contactos", "ff_blog", "ff_ordenes", "ff_datos_version"].forEach(k => localStorage.removeItem(k))

// una sesion sin token es de la version anterior, hay que volver a iniciar sesion
const sesion = getSesion()
if (sesion && !sesion.token) cerrarSesion()

ReactDOM.createRoot(document.getElementById('root')).render(
  <BrowserRouter>
    <App />
  </BrowserRouter>
)
