import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { ROLES, getReferencias } from '../datos.js'
import { obtenerUsuarioPorRun, crearUsuario, actualizarUsuario } from '../api.js'
import Cargando, { useCarga } from '../components/Cargando.jsx'
import { correoOk, passOk, runOk, textoOk, claseCampo, normalizarRun } from '../validaciones.js'
import { AdminLayout, useAdmin } from '../components/AdminMenu.jsx'
import { aviso } from '../alertas.js'

const VACIO = {
  run: "", nombre: "", apellidos: "", correo: "", pass: "",
  fecha: "", rol: "", region: "", comuna: "", direccion: "", estado: "Activo"
}

export default function AdminUsuarioForm() {
  const { run } = useParams()
  const nav = useNavigate()
  const sesion = useAdmin(true)
  const editando = !!run
  const [roles] = useState(ROLES)
  const [regiones] = useState(() => getReferencias().regiones)
  const [comunas, setComunas] = useState([])
  const [errores, setErrores] = useState({})
  const [validado, setValidado] = useState(false)
  const [form, setForm] = useState(VACIO)
  // al editar se trae el usuario desde ms-usuarios
  const carga = useCarga(async () => (run ? obtenerUsuarioPorRun(run) : null), [run])

  useEffect(() => {
    const u = carga.datos
    if (!u) return
    setForm({
      run: u.run, nombre: u.nombre, apellidos: u.apellidos, correo: u.correo,
      pass: u.password, fecha: u.fechaNacimiento || "", rol: u.rolId,
      region: u.regionId, comuna: u.comunaId, direccion: u.direccion, estado: u.estado
    })
  }, [carga.datos])

  useEffect(() => {
    setComunas(form.region ? getReferencias().comunas.filter(c => c.regionId === Number(form.region)) : [])
  }, [form.region])

  if (!sesion) return null

  if (editando && (carga.cargando || carga.error || !carga.datos)) {
    return (
      <AdminLayout>
        <Cargando cargando={carga.cargando} error={carga.error} onReintentar={carga.recargar} />
        {!carga.cargando && !carga.error && <p className="text-muted">Usuario no encontrado. <Link to="/admin/usuarios">Volver</Link></p>}
      </AdminLayout>
    )
  }

  function cambiar(e) {
    const nuevo = { ...form, [e.target.name]: e.target.value }
    if (e.target.name === "region") nuevo.comuna = ""
    setForm(nuevo)
  }

  async function guardar(e) {
    e.preventDefault()
    const err = {}
    // que el RUN o el correo no esten repetidos lo revisa ms-usuarios al guardar
    if (!runOk(form.run)) err.run = "RUN invalido."
    if (!textoOk(form.nombre, 50)) err.nombre = true
    if (!textoOk(form.apellidos, 100)) err.apellidos = true
    if (!correoOk(form.correo)) err.correo = true
    if (!passOk(form.pass)) err.pass = true
    if (!form.rol) err.rol = true
    if (!form.region) err.region = true
    if (!form.comuna) err.comuna = true
    if (!textoOk(form.direccion, 300)) err.direccion = true
    setErrores(err)
    setValidado(true)

    if (Object.keys(err).length > 0) {
      aviso("Revisa el formulario", "Existen campos obligatorios o invalidos.", "error")
      return
    }

    const user = {
      run: normalizarRun(form.run),
      nombre: form.nombre.trim(),
      apellidos: form.apellidos.trim(),
      correo: form.correo.trim(),
      password: form.pass,
      fechaNacimiento: form.fecha || "",
      rolId: Number(form.rol),
      regionId: Number(form.region),
      comunaId: Number(form.comuna),
      direccion: form.direccion.trim(),
      estado: form.estado
    }

    try {
      if (editando) await actualizarUsuario(carga.datos.id, user)
      else await crearUsuario(user)
    } catch (e2) {
      if (e2.status === 409) {
        setErrores({ ...err, run: "Ese RUN o correo ya esta registrado." })
        aviso("Revisa el formulario", e2.message, "error")
      } else {
        aviso("No se pudo guardar", e2.message, "error")
      }
      return
    }
    await aviso("Correcto", "Usuario guardado correctamente.")
    nav("/admin/usuarios")
  }

  const clase = (campo, base) => claseCampo(errores, validado, campo, base)

  return (
    <AdminLayout>
      <h1 className="section-title">{editando ? "Editar usuario" : "Nuevo usuario"}</h1>
      <form onSubmit={guardar} className="bg-ff-panel rounded p-4" noValidate>
        <div className="row g-3">
          <div className="col-md-4">
            <label htmlFor="runUsuario" className="form-label">RUN (sin puntos ni guion)</label>
            <input id="runUsuario" name="run" className={clase("run")} maxLength="9" placeholder="Ej: 123456785" value={form.run} onChange={cambiar} readOnly={editando} />
            <div className="invalid-feedback">{errores.run}</div>
          </div>
          <div className="col-md-4">
            <label htmlFor="nombreUsuario" className="form-label">Nombre</label>
            <input id="nombreUsuario" name="nombre" className={clase("nombre")} maxLength="50" value={form.nombre} onChange={cambiar} />
            <div className="invalid-feedback">Obligatorio (maximo 50 caracteres).</div>
          </div>
          <div className="col-md-4">
            <label htmlFor="apellidosUsuario" className="form-label">Apellidos</label>
            <input id="apellidosUsuario" name="apellidos" className={clase("apellidos")} maxLength="100" value={form.apellidos} onChange={cambiar} />
            <div className="invalid-feedback">Obligatorio (maximo 100 caracteres).</div>
          </div>
          <div className="col-md-6">
            <label htmlFor="correoUsuario" className="form-label">Correo</label>
            <input id="correoUsuario" name="correo" type="email" className={clase("correo")} maxLength="100" value={form.correo} onChange={cambiar} />
            <div className="invalid-feedback">Solo @duoc.cl, @profesor.duoc.cl o @gmail.com.</div>
          </div>
          <div className="col-md-6">
            <label htmlFor="passUsuario" className="form-label">Contrasena</label>
            <input id="passUsuario" name="pass" className={clase("pass")} maxLength="10" value={form.pass} onChange={cambiar} />
            <div className="invalid-feedback">Entre 4 y 10 caracteres.</div>
          </div>
          <div className="col-md-4">
            <label htmlFor="fechaUsuario" className="form-label">Fecha de nacimiento</label>
            <input id="fechaUsuario" name="fecha" type="date" className="form-control" value={form.fecha} onChange={cambiar} />
          </div>
          <div className="col-md-4">
            <label htmlFor="rolUsuario" className="form-label">Tipo de usuario</label>
            <select id="rolUsuario" name="rol" className={clase("rol", "form-select")} value={form.rol} onChange={cambiar}>
              <option value="">Seleccione un tipo de usuario</option>
              {roles.map(r => <option key={r.id} value={r.id}>{r.nombre}</option>)}
            </select>
            <div className="invalid-feedback">Selecciona un tipo de usuario.</div>
          </div>
          <div className="col-md-4">
            <label htmlFor="estadoUsuario" className="form-label">Estado</label>
            <select id="estadoUsuario" name="estado" className="form-select" value={form.estado} onChange={cambiar}>
              <option>Activo</option>
              <option>Inactivo</option>
            </select>
          </div>
          <div className="col-md-6">
            <label htmlFor="regionUsuario" className="form-label">Region</label>
            <select id="regionUsuario" name="region" className={clase("region", "form-select")} value={form.region} onChange={cambiar}>
              <option value="">Seleccione una region</option>
              {regiones.map(r => <option key={r.id} value={r.id}>{r.nombre}</option>)}
            </select>
            <div className="invalid-feedback">Selecciona una region.</div>
          </div>
          <div className="col-md-6">
            <label htmlFor="comunaUsuario" className="form-label">Comuna</label>
            <select id="comunaUsuario" name="comuna" className={clase("comuna", "form-select")} value={form.comuna} onChange={cambiar}>
              <option value="">{form.region ? "Seleccione una comuna" : "Seleccione una region primero"}</option>
              {comunas.map(c => <option key={c.id} value={c.id}>{c.nombre}</option>)}
            </select>
            <div className="invalid-feedback">Selecciona una comuna.</div>
          </div>
          <div className="col-12">
            <label htmlFor="direccionUsuario" className="form-label">Direccion</label>
            <input id="direccionUsuario" name="direccion" className={clase("direccion")} maxLength="300" value={form.direccion} onChange={cambiar} />
            <div className="invalid-feedback">Obligatoria (maximo 300 caracteres).</div>
          </div>
        </div>
        <div className="d-flex gap-2 mt-4">
          <button type="submit" className="btn btn-accent">Guardar</button>
          <Link to="/admin/usuarios" className="btn btn-outline-accent">Cancelar</Link>
        </div>
      </form>
    </AdminLayout>
  )
}
