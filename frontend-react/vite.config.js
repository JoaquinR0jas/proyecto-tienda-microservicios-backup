import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    // En desarrollo el navegador pide a /api y Vite lo manda al gateway.
    // Asi no importa si el navegador entra por localhost, 127.0.0.1 o la IP de la red.
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
