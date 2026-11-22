import { useAdmin } from './AdminMenu.jsx'

// Protege las paginas del panel /admin.
// useAdmin revisa la sesion y, si no corresponde, muestra un aviso y redirige.
// Con soloAdmin={true} la pagina queda reservada al administrador.
export default function RutaAdmin({ soloAdmin = false, children }) {
  const sesion = useAdmin(soloAdmin)
  return sesion ? children : null
}
