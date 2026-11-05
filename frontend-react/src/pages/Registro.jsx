import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { KEYS, getColeccion, setColeccion } from '../datos.js'
import { correoOk, passOk, runOk, textoOk, normalizarRun } from '../validaciones.js'
import { aviso } from '../alertas.js'

export default function Registro() {
  const nav = useNavigate()
  const [form, setForm] = useState({
    run: "", nombre: "", apellidos: "", correo: "",
    pass: "", fecha: "", region: "", comuna: "", direccion: ""
  })
  const [regiones, setRegiones] = useState([])
  const [comunas, setComunas] = useState([])
  const [error, setError] = useState("")

  useEffect(() => {
    setRegiones(getColeccion(KEYS.regiones))
  }, [])

  useEffect(() => {
    if (!form.region) {
      setComunas([])
      return
    }
    setComunas(getColeccion(KEYS.comunas).filter(c => c.regionId === Number(form.region)))
  }, [form.region])

  function cambiar(e) {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  function guardar(e) {
    e.preventDefault()
    setError("")

    const run = normalizarRun(form.run)

    if (!runOk(run)) {
      setError("Revisa el RUN: debe incluir el digito verificador (ej: 12345678-5)")
      return
    }
    if (getColeccion(KEYS.usuarios).some(u => normalizarRun(u.run) === run)) {
      setError("Ese run ya esta registrado")
      return
    }
    if (!textoOk(form.nombre, 50) || !textoOk(form.apellidos, 100)) {
      setError("Revisa el nombre y apellidos")
      return
    }
    if (!correoOk(form.correo)) {
      setError("Solo se aceptan correos @duoc.cl, @profesor.duoc.cl o @gmail.com")
      return
    }
    if (!passOk(form.pass)) {
      setError("La contrasena debe tener entre 4 y 10 caracteres")
      return
    }
    if (!form.region || !form.comuna || !textoOk(form.direccion, 300)) {
      setError("Completa region, comuna y direccion")
      return
    }

    const users = getColeccion(KEYS.usuarios)
    users.push({
      run,
      nombre: form.nombre.trim(),
      apellidos: form.apellidos.trim(),
      correo: form.correo.trim(),
      password: form.pass,
      fechaNacimiento: form.fecha || "",
      rolId: 3,
      regionId: Number(form.region),
      comunaId: Number(form.comuna),
      direccion: form.direccion.trim(),
      estado: "Activo"
    })
    setColeccion(KEYS.usuarios, users)
    aviso("Cuenta creada", "Ahora inicia sesion.")
    nav("/login")
  }

  return (
    <section className="container py-5">
      <div className="row justify-content-center">
        <div className="col-lg-8">
          <div className="auth-card p-4 p-md-5">
            <h1 className="h3 mb-4 text-center">Crear cuenta en FreshFits</h1>
            <form onSubmit={guardar}>
              <div className="row g-3">
                <div className="col-md-6">
                  <label className="form-label">RUN (con digito verificador)</label>
                  <input className="form-control" name="run" maxLength="12" placeholder="Ej: 12345678-5" value={form.run} onChange={cambiar} />
                  <div className="form-text">Puedes escribirlo con puntos y guion; se guarda sin formato.</div>
                </div>
                <div className="col-md-6">
                  <label className="form-label">Fecha de nacimiento</label>
                  <input className="form-control" name="fecha" type="date" value={form.fecha} onChange={cambiar} />
                </div>
                <div className="col-md-6">
                  <label className="form-label">Nombre</label>
                  <input className="form-control" name="nombre" maxLength="50" value={form.nombre} onChange={cambiar} />
                </div>
                <div className="col-md-6">
                  <label className="form-label">Apellidos</label>
                  <input className="form-control" name="apellidos" maxLength="100" value={form.apellidos} onChange={cambiar} />
                </div>
                <div className="col-md-6">
                  <label className="form-label">Correo</label>
                  <input className="form-control" name="correo" maxLength="100" placeholder="usuario@gmail.com" value={form.correo} onChange={cambiar} />
                </div>
                <div className="col-md-6">
                  <label className="form-label">Contrasena</label>
                  <input className="form-control" name="pass" type="password" maxLength="10" value={form.pass} onChange={cambiar} />
                </div>
                <div className="col-md-6">
                  <label className="form-label">Region</label>
                  <select className="form-select" name="region" value={form.region} onChange={cambiar}>
                    <option value="">Seleccione una region</option>
                    {regiones.map(r => <option key={r.id} value={r.id}>{r.nombre}</option>)}
                  </select>
                </div>
                <div className="col-md-6">
                  <label className="form-label">Comuna</label>
                  <select className="form-select" name="comuna" value={form.comuna} onChange={cambiar}>
                    <option value="">Seleccione una comuna</option>
                    {comunas.map(c => <option key={c.id} value={c.id}>{c.nombre}</option>)}
                  </select>
                </div>
                <div className="col-12">
                  <label className="form-label">Direccion</label>
                  <input className="form-control" name="direccion" maxLength="300" value={form.direccion} onChange={cambiar} />
                </div>
              </div>
              {error && <div className="alert alert-danger mt-3 py-2">{error}</div>}
              <button type="submit" className="btn btn-accent w-100 mt-4">Registrar</button>
              <p className="text-center mt-3 mb-0 small text-muted">
                Ya tienes cuenta? <Link to="/login">Inicia sesion</Link>
              </p>
            </form>
          </div>
        </div>
      </div>
    </section>
  )
}
