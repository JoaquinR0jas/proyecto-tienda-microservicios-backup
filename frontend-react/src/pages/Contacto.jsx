import { useState } from 'react'
import { KEYS, getColeccion, setColeccion, siguienteId } from '../datos.js'
import { correoOk, textoOk, claseCampo } from '../validaciones.js'
import { aviso } from '../alertas.js'

const VACIO = { nombre: "", correo: "", comentario: "" }

export default function Contacto() {
  const [form, setForm] = useState(VACIO)
  const [errores, setErrores] = useState({})
  const [validado, setValidado] = useState(false)

  function cambiar(e) {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  function enviar(e) {
    e.preventDefault()
    const err = {}
    if (!textoOk(form.nombre, 100)) err.nombre = true
    if (!correoOk(form.correo)) err.correo = true
    if (!textoOk(form.comentario, 500)) err.comentario = true
    setErrores(err)
    setValidado(true)

    if (Object.keys(err).length > 0) {
      aviso("Revisa el formulario", "Existen campos obligatorios o invalidos.", "error")
      return
    }

    const lista = getColeccion(KEYS.contactos)
    lista.push({
      id: siguienteId(lista),
      nombre: form.nombre.trim(),
      correo: form.correo.trim(),
      comentario: form.comentario.trim(),
      fecha: new Date().toISOString().slice(0, 10)
    })
    setColeccion(KEYS.contactos, lista)

    aviso("Mensaje enviado", "Gracias por escribirnos, te responderemos pronto.")
    setForm(VACIO)
    setErrores({})
    setValidado(false)
  }

  const clase = (campo) => claseCampo(errores, validado, campo)

  return (
    <section className="container py-5">
      <div className="row justify-content-center">
        <div className="col-lg-7">
          <h1 className="section-title">Contacto</h1>
          <p className="text-muted mb-4">Tienes dudas o comentarios? Escribenos y te responderemos a la brevedad.</p>
          <div className="auth-card p-4 p-md-5">
            <form onSubmit={enviar} noValidate>
              <div className="mb-3">
                <label htmlFor="nombreContacto" className="form-label">Nombre</label>
                <input id="nombreContacto" name="nombre" className={clase("nombre")} maxLength="100" value={form.nombre} onChange={cambiar} />
                <div className="invalid-feedback">El nombre es obligatorio (maximo 100 caracteres).</div>
              </div>
              <div className="mb-3">
                <label htmlFor="correoContacto" className="form-label">Correo</label>
                <input id="correoContacto" name="correo" type="email" className={clase("correo")} maxLength="100" placeholder="usuario@gmail.com" value={form.correo} onChange={cambiar} />
                <div className="invalid-feedback">Solo se aceptan correos @duoc.cl, @profesor.duoc.cl o @gmail.com.</div>
              </div>
              <div className="mb-4">
                <label htmlFor="comentarioContacto" className="form-label">Comentario</label>
                <textarea id="comentarioContacto" name="comentario" className={clase("comentario")} rows="5" maxLength="500" value={form.comentario} onChange={cambiar}></textarea>
                <div className="invalid-feedback">El comentario es obligatorio (maximo 500 caracteres).</div>
              </div>
              <button type="submit" className="btn btn-accent w-100">Enviar</button>
            </form>
          </div>
        </div>
      </div>
    </section>
  )
}
