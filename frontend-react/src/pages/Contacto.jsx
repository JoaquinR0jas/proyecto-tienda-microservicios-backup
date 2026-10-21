import { useState } from 'react'
import { Container, Row, Col, Form, Button, Alert } from 'react-bootstrap'
import { KEYS, getColeccion, setColeccion } from '../datos.js'
import { correoOk, textoOk } from '../validaciones.js'

export default function Contacto() {
  const [nombre, setNombre] = useState("")
  const [correo, setCorreo] = useState("")
  const [comentario, setComentario] = useState("")
  const [error, setError] = useState("")

  function enviar(e) {
    e.preventDefault()
    setError("")

    if (!textoOk(nombre, 100)) {
      setError("Pon tu nombre")
      return
    }
    if (!correoOk(correo)) {
      setError("Ese correo no sirve, usa @duoc.cl, @profesor.duoc.cl o @gmail.com")
      return
    }
    if (!textoOk(comentario, 500)) {
      setError("Escribe un comentario")
      return
    }

    const lista = getColeccion(KEYS.contactos)
    lista.push({
      nombre: nombre.trim(),
      correo: correo.trim(),
      comentario: comentario.trim(),
      fecha: new Date().toISOString().slice(0, 10)
    })
    setColeccion(KEYS.contactos, lista)

    alert("Mensaje enviado, gracias por escribirnos")
    setNombre("")
    setCorreo("")
    setComentario("")
  }

  return (
    <Container className="py-5">
      <Row className="justify-content-center">
        <Col lg={7}>
          <h1 className="section-title">Contacto</h1>
          <p className="text-muted mb-4">Tienes dudas o comentarios? Escribenos y te responderemos a la brevedad.</p>
          <div className="auth-card p-4 p-md-5">
            <Form onSubmit={enviar}>
              <Form.Group className="mb-3">
                <Form.Label>Nombre</Form.Label>
                <Form.Control maxLength="100" value={nombre} onChange={e => setNombre(e.target.value)} />
              </Form.Group>
              <Form.Group className="mb-3">
                <Form.Label>Correo</Form.Label>
                <Form.Control maxLength="100" placeholder="usuario@gmail.com" value={correo} onChange={e => setCorreo(e.target.value)} />
              </Form.Group>
              <Form.Group className="mb-4">
                <Form.Label>Comentario</Form.Label>
                <Form.Control as="textarea" rows={5} maxLength="500" value={comentario} onChange={e => setComentario(e.target.value)} />
              </Form.Group>
              {error && <Alert variant="danger" className="py-2">{error}</Alert>}
              <Button type="submit" variant="accent" className="w-100">Enviar</Button>
            </Form>
          </div>
        </Col>
      </Row>
    </Container>
  )
}
