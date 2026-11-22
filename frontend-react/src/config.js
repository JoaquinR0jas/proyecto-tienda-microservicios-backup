// Direccion del API Gateway. Se puede cambiar con la variable VITE_API_URL en un archivo .env
// (ej: VITE_API_URL=http://localhost:8080). Si no existe se usa el gateway local.
function leerUrl() {
  try {
    return import.meta.env.VITE_API_URL
  } catch {
    return undefined // en las pruebas con Karma no existe import.meta.env
  }
}

export const API_URL = (leerUrl() || "http://localhost:8080").replace(/\/$/, "")
