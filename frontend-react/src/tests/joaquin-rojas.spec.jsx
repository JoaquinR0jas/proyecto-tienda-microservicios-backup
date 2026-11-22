import { render, screen, fireEvent, waitFor, cleanup } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import Productos from '../pages/Productos.jsx'
import Carrito from '../pages/Carrito.jsx'
import { getCarrito, setCarrito } from '../datos.js'
import { mockApi } from './mockApi.js'

describe('Carrito de compras', () => {
  beforeEach(() => {
    localStorage.clear()
    mockApi() // los productos vienen de un backend falso
  })

  afterEach(() => {
    cleanup()
  })

  it('aumenta la cantidad al agregar el mismo producto otra vez', async () => {
    const { container } = render(
      <MemoryRouter>
        <Productos />
      </MemoryRouter>
    )

    await screen.findByText('Polera Fresh Basica')
    const botones = container.querySelectorAll('.btn-accent')
    fireEvent.click(botones[0])
    fireEvent.click(botones[0])

    const carrito = getCarrito()
    expect(carrito.length).toBe(1)
    expect(carrito[0].cantidad).toBe(2)
  })

  it('actualiza el total cuando cambia la cantidad', async () => {
    setCarrito([{ codigo: 'FF-001', cantidad: 1 }])

    const { container } = render(
      <MemoryRouter>
        <Carrito />
      </MemoryRouter>
    )

    await screen.findByText('Polera Fresh Basica')
    const total = () => container.querySelector('.fs-4').textContent
    expect(total()).toBe('$14.990')

    fireEvent.click(screen.getByText('+'))

    await waitFor(() => {
      expect(total()).toBe('$29.980')
    })
    expect(screen.getByText('2')).toBeTruthy()
  })
})
