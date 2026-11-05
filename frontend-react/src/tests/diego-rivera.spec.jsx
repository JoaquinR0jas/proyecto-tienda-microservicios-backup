import { render, screen, fireEvent, waitFor, cleanup } from '@testing-library/react'
import { MemoryRouter } from 'react-router-dom'
import Productos from '../pages/Productos.jsx'
import { initDatos } from '../datos.js'

describe('Renderizado del catalogo', () => {
  beforeEach(() => {
    localStorage.clear()
    initDatos()
  })
  // Limpiar el DOM después de cada prueba
  afterEach(() => {
    cleanup()
  })

  // Prueba para verificar que el titulo y las tarjetas de productos se renderizan correctamente
  it('renderiza el titulo y las tarjetas de productos', async () => {
    const { container } = render(
      <MemoryRouter>
        <Productos />
      </MemoryRouter>
    )

    expect(screen.getByText('Catalogo FreshFits')).toBeTruthy()
    await screen.findByText('Polera Fresh Basica')
    expect(container.querySelectorAll('.card').length).toBe(8)
  })

  // Prueba para verificar que se muestra un mensaje cuando no hay productos que coincidan con el filtro
  it('muestra un mensaje cuando no hay productos que coincidan', async () => {
    const { container } = render(
      <MemoryRouter>
        <Productos />
      </MemoryRouter>
    )

    await screen.findByText('Polera Fresh Basica')
    const input = container.querySelector('input[type="text"]')
    fireEvent.change(input, { target: { value: 'zzzz' } })

    // Esperar a que el mensaje de "No se encontraron prendas con esos criterios." aparezca en el DOM
    await waitFor(() => {
      expect(screen.getByText('No se encontraron prendas con esos criterios.')).toBeTruthy()
    })
    expect(container.querySelectorAll('.card').length).toBe(0)
  })
})
