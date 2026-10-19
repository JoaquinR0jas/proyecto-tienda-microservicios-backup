import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { KEYS, getColeccion } from '../datos.js'
import { correoOk, passOk, claseCampo } from '../validaciones.js'
import { aviso } from '../alertas.js'

export default function Login() {
  const [correo, setCorreo] = useState("")
  const [pass, setPass] = useState("")
  const [errores, setErrores] = useState({})
  const [validado, setValidado] = useState(false)
  const nav = useNavigate()

  async function entrar(e) {
    e.preventDefault()
    const err = {}
    if (!correoOk(correo)) err.correo = true
    if (!passOk(pass)) err.pass = true
    setErrores(err)
    setValidado(true)

    if (Object.keys(err).length > 0) {
      aviso("Revisa el formulario", "Correo o contrasena con formato invalido.", "error")
      return
    }

    const user = getColeccion(KEYS.usuarios).find(
      u => u.correo.toLowerCase() === correo.toLowerCase() && u.password === pass
    )

    if (!user) {
      aviso("Credenciales incorrectas", "El correo o la contrasena no coinciden.", "error")
      return
    }

    if (user.estado === "Inactivo") {
      aviso("Cuenta inactiva", "Tu cuenta se encuentra deshabilitada. Contacta a un administrador.", "warning")
      return
    }

    localStorage.setItem(KEYS.sesion, JSON.stringify({
      run: user.run,
      nombre: user.nombre,
      apellidos: user.apellidos,
      correo: user.correo,
      rolId: user.rolId
    }))
    window.dispatchEvent(new Event("sesion-cambio"))

    await aviso("Bienvenido", "Inicio de sesion correcto.")
    if (user.rolId === 1 || user.rolId === 2) nav("/admin")
    else nav("/")
  }

  return (
    <section className="container py-5 min-vh-70 d-flex align-items-center">
      <div className="row justify-content-center w-100">
        <div className="col-md-6 col-lg-5">
          <div className="auth-card p-4 p-md-5 text-center">
            <img src="/img/logo.svg" alt="FreshFits" height="70" className="mb-3" />
            <h1 className="h3 mb-4">Iniciar sesion</h1>
            <form onSubmit={entrar} className="text-start" noValidate>
              <div className="mb-3">
                <label htmlFor="correoLogin" className="form-label">Correo</label>
                <input
                  id="correoLogin"
                  type="email"
                  className={claseCampo(errores, validado, "correo")}
                  maxLength="100"
                  placeholder="usuario@gmail.com"
                  value={correo}
                  onChange={e => setCorreo(e.target.value)}
                />
                <div className="invalid-feedback">Ingresa un correo valido (@duoc.cl, @profesor.duoc.cl o @gmail.com).</div>
              </div>
              <div className="mb-4">
                <label htmlFor="passLogin" className="form-label">Contrasena</label>
                <input
                  id="passLogin"
                  type="password"
                  className={claseCampo(errores, validado, "pass")}
                  maxLength="10"
                  value={pass}
                  onChange={e => setPass(e.target.value)}
                />
                <div className="invalid-feedback">La contrasena debe tener entre 4 y 10 caracteres.</div>
              </div>
              <button type="submit" className="btn btn-accent w-100">Iniciar sesion</button>
            </form>
            <p className="mt-4 mb-0 small text-muted">
              No tienes cuenta? <Link to="/registro">Registrate aqui</Link>
            </p>
          </div>
        </div>
      </div>
    </section>
  )
}
