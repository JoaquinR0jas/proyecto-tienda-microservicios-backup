import { useState } from 'react'
import { Col, Container, Row } from 'react-bootstrap'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { iniciarSesion } from '../api.js'
import { correoOk, passOk, claseCampo } from '../validaciones.js'
import { aviso } from '../alertas.js'

export default function Login() {
  const [correo, setCorreo] = useState("")
  const [pass, setPass] = useState("")
  const [errores, setErrores] = useState({})
  const [validado, setValidado] = useState(false)
  const nav = useNavigate()
  const destino = useLocation().state?.volverA

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

    // auth-service valida las credenciales y entrega el token
    let sesion
    try {
      sesion = await iniciarSesion(correo.trim(), pass)
    } catch (e) {
      if (e.status === 401 && e.message.includes("deshabilitada")) {
        aviso("Cuenta inactiva", "Tu cuenta se encuentra deshabilitada. Contacta a un administrador.", "warning")
      } else if (e.status === 401) {
        aviso("Credenciales incorrectas", "El correo o la contrasena no coinciden.", "error")
      } else {
        aviso("No se pudo iniciar sesion", e.message, "error")
      }
      return
    }

    await aviso("Bienvenido", "Inicio de sesion correcto.")
    // si venia del carrito, vuelve al checkout para terminar la compra
    if (destino) nav(destino)
    else if (sesion.rolId === 1 || sesion.rolId === 2) nav("/admin")
    else nav("/")
  }

  return (
    <Container className="py-5 min-vh-70 d-flex align-items-center">
      <Row className="justify-content-center w-100">
        <Col md={6} lg={5}>
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
        </Col>
      </Row>
    </Container>
  )
}
