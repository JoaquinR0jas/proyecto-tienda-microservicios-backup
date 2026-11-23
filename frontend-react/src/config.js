// Direccion del API Gateway. Se puede cambiar con la variable VITE_API_URL en un archivo .env
// (ej: VITE_API_URL=http://localhost:8080).
// En desarrollo se deja vacia a proposito: las peticiones van a /api y Vite las
// reenvia al gateway (ver proxy en vite.config.js). Asi funciona igual si se abre
// la pagina con localhost, con 127.0.0.1 o desde otro equipo de la red.
function leerUrl() {
  try {
    return import.meta.env.VITE_API_URL
  } catch {
    return undefined // en las pruebas con Karma no existe import.meta.env
  }
}

function esDesarrollo() {
  try {
    return !!import.meta.env.DEV
  } catch {
    return false
  }
}

export const API_URL = (leerUrl() || (esDesarrollo() ? "" : "http://localhost:8080")).replace(/\/$/, "")
