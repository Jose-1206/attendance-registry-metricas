export interface ClassSession {
  id: number
  courseId: number
  title: string
  description: string | null
  startAt: string
  endAt: string
}

export interface SessionInput {
  courseId: number
  title: string
  description: string
  startAt: string
  endAt: string
}


export class TeacherApiError extends Error {
  public readonly status: number

  constructor(status: number, message: string) {
    super(message)
    this.name = 'TeacherApiError'
    this.status = status
  }
}


function messageForStatus(status: number): string {
  switch (status) {
    case 400: return 'Los datos enviados no son válidos. Revisa el formulario.'
    case 401: return 'Tu sesión expiró. Inicia sesión nuevamente.'
    case 403: return 'No tienes permisos para esta operación.'
    case 404: return 'No se encontró el recurso solicitado.'
    case 409: return 'Hay registros relacionados que impiden esta operación.'
    default: return 'No se pudo completar la operación. Inténtalo de nuevo.'
  }
}

export async function teacherRequest<T>(
  path: string,
  accessToken: string,
  options: RequestInit = {},
): Promise<T> {
  if (!accessToken.trim()) throw new TeacherApiError(401, messageForStatus(401))

  const headers = new Headers(options.headers)
  headers.set('Accept', 'application/json')
  headers.set('Authorization', `Bearer ${accessToken}`)
  if (options.body !== undefined) headers.set('Content-Type', 'application/json')

  let response: Response
  try {
    response = await fetch(`/api/v1${path}`, {
      ...options,
      headers,
      cache: 'no-store',
    })
  } catch (error) {
    if (error instanceof Error && error.name === 'AbortError') throw error
    throw new TeacherApiError(0, 'No fue posible conectar con el servidor.')
  }

  if (!response.ok) throw new TeacherApiError(response.status, messageForStatus(response.status))
  if (response.status === 204) return undefined as T
  return response.json() as Promise<T>
}

export const sessionsApi = {
  listByCourse(courseId: number, token: string, signal?: AbortSignal) {
    return teacherRequest<ClassSession[]>(`/sessions/courses/${courseId}`, token, { signal })
  },
  find(id: number, token: string) {
    return teacherRequest<ClassSession>(`/sessions/${id}`, token)
  },
  create(input: SessionInput, token: string) {
    return teacherRequest<ClassSession>('/sessions', token, {
      method: 'POST', body: JSON.stringify(input),
    })
  },
  update(id: number, input: SessionInput, token: string) {
    return teacherRequest<ClassSession>(`/sessions/${id}`, token, {
      method: 'PUT', body: JSON.stringify(input),
    })
  },
  remove(id: number, token: string) {
    return teacherRequest<void>(`/sessions/${id}`, token, { method: 'DELETE' })
  },
}
