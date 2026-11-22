// fila de un reporte con una barra horizontal proporcional al maximo
export default function Barra({ etiqueta, valor, maximo, texto }) {
  const ancho = maximo > 0 ? Math.round((valor / maximo) * 100) : 0
  return (
    <div className="mb-2">
      <div className="d-flex justify-content-between small">
        <span>{etiqueta}</span>
        <span className="fw-bold">{texto ?? valor}</span>
      </div>
      <div className="progress" style={{ height: 8 }}>
        <div className="progress-bar bg-dark" style={{ width: ancho + "%" }}></div>
      </div>
    </div>
  )
}
