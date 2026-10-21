import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Container, Row, Col, Form, Button, Alert } from 'react-bootstrap'
import { KEYS, getColeccion } from '../datos.js'
import { correoOk, passOk } from '../validaciones.js'

export default function Login() {
  const [correo, setCorreo] = useState("")
  const [pass, setPass] = useState("")
  const [error, setError] = useState("")
  const nav = useNavigate()

  function entrar(e) {
    e.preventDefault()
    setError("")

    if (!correoOk(correo) || !passOk(pass)) {
      setError("Revisa el correo o la contrasena")
      return
    }

    const user = getColeccion(KEYS.usuarios).find(
      u => u.correo.toLowerCase() === correo.toLowerCase() && u.password === pass
    )

    if (!user) {
      setError("El correo o la contrasena no coinciden")
      return
    }

    if (user.estado === "Inactivo") {
      setError("Tu cuenta esta deshabilitada, habla con un admin")
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

    if (user.rolId === 1 || user.rolId === 2) nav("/admin")
    else nav("/")
  }

  return (
    <Container className="py-5 min-vh-70 d-flex align-items-center">
      <Row className="justify-content-center w-100">
        <Col md={6} lg={5}>
          <div className="auth-card p-4 p-md-5 text-center">
            <img src="/img/logo.svg" alt="FreshFits" height="70" className="mb-3" />
            <h1 className="h3 mb-4">Iniciar sesion</h1>
            <Form onSubmit={entrar} className="text-start">
              <Form.Group className="mb-3">
                <Form.Label>Correo</Form.Label>
                <Form.Control
                  type="email"
                  maxLength="100"
                  placeholder="usuario@gmail.com"
                  value={correo}
                  onChange={e => setCorreo(e.target.value)}
                />
              </Form.Group>
              <Form.Group className="mb-4">
                <Form.Label>Contrasena</Form.Label>
                <Form.Control
                  type="password"
                  maxLength="10"
                  value={pass}
                  onChange={e => setPass(e.target.value)}
                />
              </Form.Group>
              {error && <Alert variant="danger" className="py-2">{error}</Alert>}
              <Button type="submit" variant="accent" className="w-100">Iniciar sesion</Button>
            </Form>
            <p className="mt-4 mb-0 small text-muted">
              No tienes cuenta? <Link to="/registro">Registrate aqui</Link>
            </p>
            <p className="mt-2 mb-0 small text-muted">admin@gmail.com / admin123</p>
          </div>
        </Col>
      </Row>
    </Container>
  )
}
