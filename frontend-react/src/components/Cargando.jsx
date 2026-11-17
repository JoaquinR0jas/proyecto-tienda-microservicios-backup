import { useCallback, useEffect, useState } from 'react'

// Carga datos del backend al abrir una pagina.
// Devuelve { datos, cargando, error, recargar }
export function useCarga(cargar, deps = []) {
  const [estado, setEstado] = useState({ datos: null, cargando: true, error: null })

  const recargar = useCallback(async () => {
    setEstado(e => ({ ...e, cargando: true, error: null }))
    try {
      const datos = await cargar()
      setEstado({ datos, cargando: false, error: null })
    } catch (e) {
      setEstado({ datos: null, cargando: false, error: e.message || "Ocurrio un error al cargar los datos." })
    }
  }, deps)

  useEffect(() => {
    recargar()
  }, [recargar])

  return { ...estado, recargar }
}

// Muestra un spinner mientras carga o el error con un boton para reintentar.
// Si no esta cargando ni hay error, no muestra nada.
export default function Cargando({ cargando, error, onReintentar }) {
  if (cargando) {
    return (
      <div className="text-center py-5 text-muted">
        <div className="spinner-border spinner-border-sm me-2" role="status"></div>
        Cargando...
      </div>
    )
  }
  if (error) {
    return (
      <div className="alert alert-danger d-flex flex-wrap justify-content-between align-items-center gap-2">
        <span><i className="bi bi-exclamation-triangle me-1"></i>{error}</span>
        {onReintentar && <button className="btn btn-sm btn-outline-danger" onClick={onReintentar}>Reintentar</button>}
      </div>
    )
  }
  return null
}
