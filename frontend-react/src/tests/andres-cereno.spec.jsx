import { render, screen, fireEvent, cleanup } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
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

describe('Formulario de contacto: estado', () => {
  afterEach(() => {
    cleanup()
  })

  beforeEach(() => {
    localStorage.clear()
    initDatos()
  })

  // caso borde: un nombre con solo espacios no esta vacio, pero tampoco es un nombre valido
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

    // el estado guarda lo escrito, aunque sean solo espacios
    expect(nombre.value).toBe('     ')

    fireEvent.click(container.querySelector('button[type="submit"]'))

    expect(nombre.classList.contains('is-invalid')).toBeTrue()
    expect(correo.classList.contains('is-valid')).toBeTrue()
    expect(getColeccion(KEYS.contactos).length).toBe(0)
  })
})
