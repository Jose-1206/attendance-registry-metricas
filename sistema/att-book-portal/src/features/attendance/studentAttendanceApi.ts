
export interface StudentAttendance {
  id: number
  studentId: number
  claseId: number
  registeredAt: string
}

export class StudentAttendanceError extends Error {
  readonly status: number

  constructor(status: number, message: string) {
    super(message)
    this.name = 'StudentAttendanceError'
    this.status = status
  }
}

function errorMessage(status: number): string {
  switch (status) {
    case 400:
      return 'El código QR es inválido o está vencido.'
    case 401:
      return 'Tu sesión ha vencido. Inicia sesión nuevamente.'
    case 403:
      return 'No tienes permiso o no estás inscrito en la clase.'
    case 404:
      return 'No se encontró la clase o el recurso solicitado.'
    case 409:
      return 'Ya registraste tu asistencia a esta clase.'
    case 405:
    case 501:
      return 'El servidor todavía no permite esta operación.'
    default:
      return 'No fue posible completar la solicitud.'
  }
}

async function request<T>(
  path: string,
  token: string,
  options: RequestInit = {},
): Promise<T> {
  if (!token.trim()) {
    throw new StudentAttendanceError(401, errorMessage(401))
  }

  const headers = new Headers(options.headers)
  headers.set('Accept', 'application/json')
  headers.set('Authorization', `Bearer ${token}`)

  if (options.body) {
    headers.set('Content-Type', 'application/json')
  }

  let response: Response

  try {
    response = await fetch(`/api/v1/asistencias${path}`, {
      ...options,
      headers,
      cache: 'no-store',
    })
  } catch {
    throw new StudentAttendanceError(
      0,
      'No se pudo conectar con el servidor.',
    )
  }

  if (!response.ok) {
    throw new StudentAttendanceError(
      response.status,
      errorMessage(response.status),
    )
  }

  const data: unknown = await response.json()
  return data as T
}

export const studentAttendanceApi = {
  register(qr: string, token: string) {
    const value = qr.trim()

    if (!value) {
      throw new StudentAttendanceError(
        400,
        'Debes ingresar o escanear un código QR.',
      )
    }

    return request<StudentAttendance>('', token, {
      method: 'POST',
      body: JSON.stringify({ qr: value }),
    })
  },

  history(token: string, signal?: AbortSignal) {
    return request<StudentAttendance[]>('/historial', token, {
      signal,
    })
  },
}
