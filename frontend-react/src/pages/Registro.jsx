import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { Container, Row, Col, Form, Button, Alert } from 'react-bootstrap'
import { KEYS, getColeccion, setColeccion } from '../datos.js'
import { correoOk, passOk, runOk, textoOk, normalizarRun } from '../validaciones.js'

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
    alert("Cuenta creada, ahora inicia sesion")
    nav("/login")
  }

  return (
    <Container className="py-5">
      <Row className="justify-content-center">
        <Col lg={8}>
          <div className="auth-card p-4 p-md-5">
            <h1 className="h3 mb-4 text-center">Crear cuenta en FreshFits</h1>
            <Form onSubmit={guardar}>
              <Row className="g-3">
                <Col md={6}>
                  <Form.Label>RUN (con digito verificador)</Form.Label>
                  <Form.Control name="run" maxLength="12" placeholder="Ej: 12345678-5" value={form.run} onChange={cambiar} />
                  <Form.Text className="text-muted">Puedes escribirlo con puntos y guion; se guarda sin formato.</Form.Text>
                </Col>
                <Col md={6}>
                  <Form.Label>Fecha de nacimiento</Form.Label>
                  <Form.Control name="fecha" type="date" value={form.fecha} onChange={cambiar} />
                </Col>
                <Col md={6}>
                  <Form.Label>Nombre</Form.Label>
                  <Form.Control name="nombre" maxLength="50" value={form.nombre} onChange={cambiar} />
                </Col>
                <Col md={6}>
                  <Form.Label>Apellidos</Form.Label>
                  <Form.Control name="apellidos" maxLength="100" value={form.apellidos} onChange={cambiar} />
                </Col>
                <Col md={6}>
                  <Form.Label>Correo</Form.Label>
                  <Form.Control name="correo" maxLength="100" placeholder="usuario@gmail.com" value={form.correo} onChange={cambiar} />
                </Col>
                <Col md={6}>
                  <Form.Label>Contrasena</Form.Label>
                  <Form.Control name="pass" type="password" maxLength="10" value={form.pass} onChange={cambiar} />
                </Col>
                <Col md={6}>
                  <Form.Label>Region</Form.Label>
                  <Form.Select name="region" value={form.region} onChange={cambiar}>
                    <option value="">Seleccione una region</option>
                    {regiones.map(r => <option key={r.id} value={r.id}>{r.nombre}</option>)}
                  </Form.Select>
                </Col>
                <Col md={6}>
                  <Form.Label>Comuna</Form.Label>
                  <Form.Select name="comuna" value={form.comuna} onChange={cambiar}>
                    <option value="">Seleccione una comuna</option>
                    {comunas.map(c => <option key={c.id} value={c.id}>{c.nombre}</option>)}
                  </Form.Select>
                </Col>
                <Col xs={12}>
                  <Form.Label>Direccion</Form.Label>
                  <Form.Control name="direccion" maxLength="300" value={form.direccion} onChange={cambiar} />
                </Col>
              </Row>
              {error && <Alert variant="danger" className="mt-3 py-2">{error}</Alert>}
              <Button type="submit" variant="accent" className="w-100 mt-4">Registrar</Button>
              <p className="text-center mt-3 mb-0 small text-muted">
                Ya tienes cuenta? <Link to="/login">Inicia sesion</Link>
              </p>
            </Form>
          </div>
        </Col>
      </Row>
    </Container>
  )
}
