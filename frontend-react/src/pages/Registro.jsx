import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { KEYS, getColeccion, setColeccion } from '../datos.js'
import { correoOk, passOk, runOk, textoOk, claseCampo } from '../validaciones.js'
import { aviso } from '../alertas.js'

export default function Registro() {
  const nav = useNavigate()
  const [form, setForm] = useState({
    run: "", nombre: "", apellidos: "", correo: "",
    pass: "", fecha: "", region: "", comuna: "", direccion: ""
  })
  const [regiones, setRegiones] = useState([])
  const [comunas, setComunas] = useState([])
  const [errores, setErrores] = useState({})
  const [validado, setValidado] = useState(false)

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
    const nuevo = { ...form, [e.target.name]: e.target.value }
    if (e.target.name === "region") nuevo.comuna = ""
    setForm(nuevo)
  }

  async function guardar(e) {
    e.preventDefault()
    const users = getColeccion(KEYS.usuarios)
    const err = {}
    if (!runOk(form.run)) err.run = "RUN invalido. Debe tener entre 7 y 9 caracteres."
    else if (users.some(u => u.run === form.run.toUpperCase().trim())) err.run = "Ese RUN ya esta registrado."
    if (!textoOk(form.nombre, 50)) err.nombre = true
    if (!textoOk(form.apellidos, 100)) err.apellidos = true
    if (!correoOk(form.correo)) err.correo = true
    if (!passOk(form.pass)) err.pass = true
    if (!form.region) err.region = true
    if (!form.comuna) err.comuna = true
    if (!textoOk(form.direccion, 300)) err.direccion = true
    setErrores(err)
    setValidado(true)

    if (Object.keys(err).length > 0) {
      aviso("Revisa el formulario", "Existen campos obligatorios o invalidos.", "error")
      return
    }

    users.push({
      run: form.run.toUpperCase().trim(),
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
    await aviso("Correcto", "Usuario registrado correctamente.")
    nav("/login")
  }

  const clase = (campo, base) => claseCampo(errores, validado, campo, base)

  return (
    <section className="container py-5">
      <div className="row justify-content-center">
        <div className="col-lg-8">
          <div className="auth-card p-4 p-md-5">
            <h1 className="h3 mb-4 text-center">Crear cuenta en FreshFits</h1>
            <form onSubmit={guardar} noValidate>
              <div className="row g-3">
                <div className="col-md-6">
                  <label htmlFor="runRegistro" className="form-label">RUN (sin puntos ni guion)</label>
                  <input id="runRegistro" name="run" className={clase("run")} maxLength="9" placeholder="Ej: 123456785" value={form.run} onChange={cambiar} />
                  <div className="invalid-feedback">{errores.run}</div>
                </div>
                <div className="col-md-6">
                  <label htmlFor="fechaRegistro" className="form-label">Fecha de nacimiento</label>
                  <input id="fechaRegistro" name="fecha" type="date" className="form-control" value={form.fecha} onChange={cambiar} />
                </div>
                <div className="col-md-6">
                  <label htmlFor="nombreRegistro" className="form-label">Nombre</label>
                  <input id="nombreRegistro" name="nombre" className={clase("nombre")} maxLength="50" value={form.nombre} onChange={cambiar} />
                  <div className="invalid-feedback">El nombre es obligatorio (maximo 50 caracteres).</div>
                </div>
                <div className="col-md-6">
                  <label htmlFor="apellidosRegistro" className="form-label">Apellidos</label>
                  <input id="apellidosRegistro" name="apellidos" className={clase("apellidos")} maxLength="100" value={form.apellidos} onChange={cambiar} />
                  <div className="invalid-feedback">Los apellidos son obligatorios (maximo 100 caracteres).</div>
                </div>
                <div className="col-md-6">
                  <label htmlFor="correoRegistro" className="form-label">Correo</label>
                  <input id="correoRegistro" name="correo" type="email" className={clase("correo")} maxLength="100" placeholder="usuario@gmail.com" value={form.correo} onChange={cambiar} />
                  <div className="invalid-feedback">Solo se aceptan correos @duoc.cl, @profesor.duoc.cl o @gmail.com.</div>
                </div>
                <div className="col-md-6">
                  <label htmlFor="passRegistro" className="form-label">Contrasena</label>
                  <input id="passRegistro" name="pass" type="password" className={clase("pass")} maxLength="10" value={form.pass} onChange={cambiar} />
                  <div className="invalid-feedback">La contrasena debe tener entre 4 y 10 caracteres.</div>
                </div>
                <div className="col-md-6">
                  <label htmlFor="regionRegistro" className="form-label">Region</label>
                  <select id="regionRegistro" name="region" className={clase("region", "form-select")} value={form.region} onChange={cambiar}>
                    <option value="">Seleccione una region</option>
                    {regiones.map(r => <option key={r.id} value={r.id}>{r.nombre}</option>)}
                  </select>
                  <div className="invalid-feedback">Selecciona una region.</div>
                </div>
                <div className="col-md-6">
                  <label htmlFor="comunaRegistro" className="form-label">Comuna</label>
                  <select id="comunaRegistro" name="comuna" className={clase("comuna", "form-select")} value={form.comuna} onChange={cambiar}>
                    <option value="">{form.region ? "Seleccione una comuna" : "Seleccione una region primero"}</option>
                    {comunas.map(c => <option key={c.id} value={c.id}>{c.nombre}</option>)}
                  </select>
                  <div className="invalid-feedback">Selecciona una comuna.</div>
                </div>
                <div className="col-12">
                  <label htmlFor="direccionRegistro" className="form-label">Direccion</label>
                  <input id="direccionRegistro" name="direccion" className={clase("direccion")} maxLength="300" value={form.direccion} onChange={cambiar} />
                  <div className="invalid-feedback">La direccion es obligatoria (maximo 300 caracteres).</div>
                </div>
              </div>
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
