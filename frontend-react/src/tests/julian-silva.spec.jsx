import { render, screen, cleanup } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import Tarjeta from '../components/Tarjeta.jsx'

const producto = {
  codigo: 'FF-TEST',
  nombre: 'Polera de prueba',
  precio: 14990,
  stock: 5,
  categoriaId: 1,
  imagen: 'productos/polera-basica.webp',
  estado: 'Activo'
}

describe('Tarjeta de producto', () => {
  afterEach(() => {
    cleanup()
  })

  it('muestra el nombre del producto', () => {
    render(
      <MemoryRouter>
        <Tarjeta p={producto} onAgregar={() => {}} />
      </MemoryRouter>
    )

    expect(screen.getByText('Polera de prueba')).toBeTruthy()
  })

  it('muestra el precio formateado en pesos', () => {
    render(
      <MemoryRouter>
        <Tarjeta p={producto} onAgregar={() => {}} />
      </MemoryRouter>
    )

    expect(screen.getByText('$14.990')).toBeTruthy()
  })
})
