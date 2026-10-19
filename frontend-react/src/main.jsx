import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import App from './App.jsx'
import './estilos.css'
import { initDatos } from './datos.js'

// se cargan los datos antes de dibujar para que todas las paginas los encuentren en la primera visita
initDatos()

ReactDOM.createRoot(document.getElementById('root')).render(
  <BrowserRouter>
    <App />
  </BrowserRouter>
)
