import { useEffect, useState } from 'react'
import { Link, useNavigate, useParams } from 'react-router-dom'
import { Row, Col, Form, Button } from 'react-bootstrap'
import { KEYS, getColeccion, setColeccion, getSesion } from '../datos.js'
import { correoOk, passOk, runOk, textoOk } from '../validaciones.js'
import { AdminLayout } from '../components/AdminMenu.jsx'

export default function AdminUsuarioForm() {
  const { run } = useParams()
  const nav = useNavigate()
  const editando = !!run
  const [roles, setRoles] = useState([])
  const [regiones, setRegiones] = useState([])
  const [comunas, setComunas] = useState([])
  const [form, setForm] = useState({
    run: "", nombre: "", apellidos: "", correo: "", pass: "",
    fecha: "", rol: "", region: "", comuna: "", direccion: "", estado: "Activo"
  })

  useEffect(() => {
    const s = getSesion()
    if (!s || s.rolId !== 1) {
      nav("/admin")
      return
    }
    setRoles(getColeccion(KEYS.roles))
    setRegiones(getColeccion(KEYS.regiones))
    if (run) {
      const u = getColeccion(KEYS.usuarios).find(x => x.run === run)
      if (u) {
        setForm({
          run: u.run, nombre: u.nombre, apellidos: u.apellidos, correo: u.correo,
          pass: u.password, fecha: u.fechaNacimiento || "", rol: u.rolId,
          region: u.regionId, comuna: u.comunaId, direccion: u.direccion, estado: u.estado
        })
      }
    }
  }, [run])

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
    if (!runOk(form.run) || !textoOk(form.nombre, 50) || !textoOk(form.apellidos, 100)) {
      alert("Revisa run, nombre y apellidos")
      return
    }
    if (!correoOk(form.correo) || !passOk(form.pass)) {
      alert("Revisa correo y contrasena")
      return
    }
    if (!form.rol || !form.region || !form.comuna || !textoOk(form.direccion, 300)) {
      alert("Faltan rol, region, comuna o direccion")
      return
    }

    const todos = getColeccion(KEYS.usuarios)
    if (!editando && todos.some(u => u.run === form.run.toUpperCase().trim())) {
      alert("Ese run ya existe")
      return
    }

    const user = {
      run: form.run.toUpperCase().trim(),
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

    if (editando) {
      setColeccion(KEYS.usuarios, todos.map(u => u.run === run ? user : u))
    } else {
      todos.push(user)
      setColeccion(KEYS.usuarios, todos)
    }
    alert("Usuario guardado")
    nav("/admin/usuarios")
  }

  return (
    <AdminLayout>
      <h1 className="section-title">{editando ? "Editar usuario" : "Nuevo usuario"}</h1>
      <Form onSubmit={guardar} className="bg-ff-panel rounded p-4">
        <Row className="g-3">
          <Col md={6}>
            <Form.Label>RUN</Form.Label>
            <Form.Control name="run" value={form.run} onChange={cambiar} readOnly={editando} />
          </Col>
          <Col md={6}>
            <Form.Label>Fecha nacimiento</Form.Label>
            <Form.Control name="fecha" type="date" value={form.fecha} onChange={cambiar} />
          </Col>
          <Col md={6}>
            <Form.Label>Nombre</Form.Label>
            <Form.Control name="nombre" value={form.nombre} onChange={cambiar} />
          </Col>
          <Col md={6}>
            <Form.Label>Apellidos</Form.Label>
            <Form.Control name="apellidos" value={form.apellidos} onChange={cambiar} />
          </Col>
          <Col md={6}>
            <Form.Label>Correo</Form.Label>
            <Form.Control name="correo" value={form.correo} onChange={cambiar} />
          </Col>
          <Col md={6}>
            <Form.Label>Contrasena</Form.Label>
            <Form.Control name="pass" value={form.pass} onChange={cambiar} />
          </Col>
          <Col md={4}>
            <Form.Label>Rol</Form.Label>
            <Form.Select name="rol" value={form.rol} onChange={cambiar}>
              <option value="">Seleccione...</option>
              {roles.map(r => <option key={r.id} value={r.id}>{r.nombre}</option>)}
            </Form.Select>
          </Col>
          <Col md={4}>
            <Form.Label>Region</Form.Label>
            <Form.Select name="region" value={form.region} onChange={cambiar}>
              <option value="">Seleccione...</option>
              {regiones.map(r => <option key={r.id} value={r.id}>{r.nombre}</option>)}
            </Form.Select>
          </Col>
          <Col md={4}>
            <Form.Label>Comuna</Form.Label>
            <Form.Select name="comuna" value={form.comuna} onChange={cambiar}>
              <option value="">Seleccione...</option>
              {comunas.map(c => <option key={c.id} value={c.id}>{c.nombre}</option>)}
            </Form.Select>
          </Col>
          <Col md={8}>
            <Form.Label>Direccion</Form.Label>
            <Form.Control name="direccion" value={form.direccion} onChange={cambiar} />
          </Col>
          <Col md={4}>
            <Form.Label>Estado</Form.Label>
            <Form.Select name="estado" value={form.estado} onChange={cambiar}>
              <option>Activo</option>
              <option>Inactivo</option>
            </Form.Select>
          </Col>
        </Row>
        <div className="d-flex gap-2 mt-4">
          <Button type="submit" variant="accent">Guardar</Button>
          <Button as={Link} to="/admin/usuarios" variant="outline-accent">Volver</Button>
        </div>
      </Form>
    </AdminLayout>
  )
}
