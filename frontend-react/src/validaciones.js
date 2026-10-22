// validaciones de los formularios
const DOMINIOS = ["@duoc.cl", "@profesor.duoc.cl", "@gmail.com"]

export function correoOk(correo) {
  if (!correo || correo.length === 0 || correo.length > 100) return false
  return DOMINIOS.some(d => correo.toLowerCase().endsWith(d))
}

export function passOk(pass) {
  return typeof pass === "string" && pass.length >= 4 && pass.length <= 10
}

export function normalizarRun(run) {
  return String(run || "").replace(/[.\s-]/g, "").toUpperCase()
}

export function runOk(run) {
  const limpio = normalizarRun(run)
  if (limpio.length < 7 || limpio.length > 9) return false

  const cuerpo = limpio.slice(0, -1)
  const dv = limpio.slice(-1)

  if (!/^[0-9]+$/.test(cuerpo)) return false
  if (!/^[0-9K]$/.test(dv)) return false

  let suma = 0
  let multi = 2
  for (let i = cuerpo.length - 1; i >= 0; i--) {
    suma += parseInt(cuerpo.charAt(i)) * multi
    multi = multi === 7 ? 2 : multi + 1
  }

  const resto = 11 - (suma % 11)
  let esperado
  if (resto === 11) esperado = "0"
  else if (resto === 10) esperado = "K"
  else esperado = String(resto)

  return dv === esperado
}

export function textoOk(valor, maximo) {
  if (!valor || valor.trim().length === 0) return false
  if (maximo && valor.trim().length > maximo) return false
  return true
}
