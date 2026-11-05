import { render, screen, fireEvent, cleanup, act } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import { useState } from 'react'
import Tarjeta from '../components/Tarjeta.jsx'
import Contacto from '../pages/Contacto.jsx'
import { KEYS, getColeccion, initDatos } from '../datos.js'

const producto = {
  codigo: 'FF-TEST',
  nombre: 'Polera de prueba',
  precio: 14990,
  stock: 5,
  categoriaId: 1,
  imagen: 'productos/polera-basica.webp',
  estado: 'Activo'
}

describe('Tarjeta de producto: props y evento onClick', () => {
  afterEach(() => {
    cleanup()
  })

  it('recibe la funcion onAgregar y la ejecuta con el codigo al hacer clic', () => {
    const onAgregar = jasmine.createSpy('onAgregar')
    const { container } = render(
      <MemoryRouter>
        <Tarjeta p={producto} onAgregar={onAgregar} />
      </MemoryRouter>
    )

    expect(screen.getByText('Ver detalle')).toBeTruthy()
    fireEvent.click(container.querySelector('button.btn-accent'))

    expect(onAgregar).toHaveBeenCalledTimes(1)
    expect(onAgregar).toHaveBeenCalledWith('FF-TEST')
  })
})

describe('Seccion de comentarios / formulario de contacto: estado con useState', () => {
  afterEach(() => {
    cleanup()
  })

  beforeEach(() => {
    localStorage.clear()
    initDatos()
  })

  it('actualiza el estado del comentario al escribir usando useState', () => {
    function ComentarioTest() {
      const [comentario, setComentario] = useState('')
      return (
        <div>
          <textarea
            id="comentarioTest"
            value={comentario}
            onChange={(e) => setComentario(e.target.value)}
          />
          <span data-testid="estado-comentario">{comentario}</span>
        </div>
      )
    }

    const { container } = render(<ComentarioTest />)
    const textarea = container.querySelector('#comentarioTest')

    act(() => {
      fireEvent.change(textarea, { target: { value: 'Tienen tallas grandes disponibles?' } })
    })

    expect(textarea.value).toBe('Tienen tallas grandes disponibles?')
    expect(screen.getByTestId('estado-comentario').textContent).toBe('Tienen tallas grandes disponibles?')
  })

  it('maneja el estado completo del formulario de comentarios con useState', () => {
    const { container } = render(
      <MemoryRouter>
        <Contacto />
      </MemoryRouter>
    )

    const nombre = container.querySelector('#nombreContacto')
    const correo = container.querySelector('#correoContacto')
    const comentario = container.querySelector('#comentarioContacto')

    act(() => {
      fireEvent.change(nombre, { target: { value: 'Andres Cereno' } })
      fireEvent.change(correo, { target: { value: 'andres@gmail.com' } })
      fireEvent.change(comentario, { target: { value: 'Necesito informacion sobre envios' } })
    })

    expect(nombre.value).toBe('Andres Cereno')
    expect(correo.value).toBe('andres@gmail.com')
    expect(comentario.value).toBe('Necesito informacion sobre envios')
  })

  it('no acepta un nombre con solo espacios y no guarda el mensaje', () => {
    const { container } = render(
      <MemoryRouter>
        <Contacto />
      </MemoryRouter>
    )

    const nombre = container.querySelector('#nombreContacto')
    const correo = container.querySelector('#correoContacto')
    fireEvent.change(nombre, { target: { value: '     ' } })
    fireEvent.change(correo, { target: { value: 'camila@gmail.com' } })
    fireEvent.change(container.querySelector('#comentarioContacto'), { target: { value: 'Hola, tienen tallas grandes?' } })

    expect(nombre.value).toBe('     ')

    fireEvent.click(container.querySelector('button[type="submit"]'))

    expect(nombre.classList.contains('is-invalid')).toBeTrue()
    expect(correo.classList.contains('is-valid')).toBeTrue()
    expect(getColeccion(KEYS.contactos).length).toBe(0)
  })
})
