export interface Course {
  id: number
  nombre: string
  codigo: string
  profesorId: number
}

export interface CourseInput {
  nombre: string
  codigo: string
}

export class CoursesApiError extends Error {
  readonly status: number

  constructor(status: number, message: string) {
    super(message)
    this.name = 'CoursesApiError'
    this.status = status
  }
}

function errorMessage(status: number): string {
  switch (status) {
    case 400:
      return 'Revisa el nombre y el código del curso.'
    case 401:
      return 'Tu sesión no es válida. Inicia sesión nuevamente.'
    case 403:
      return 'No tienes permiso para realizar esta operación.'
    case 404:
      return 'El curso no existe o no está disponible para tu usuario.'
    case 409:
      return 'La operación entra en conflicto con datos existentes. El curso puede tener registros relacionados.'
    default:
      return 'No fue posible completar la operación. Intenta nuevamente.'
  }
}

async function request<T>(
  path: string,
  token: string,
  options: RequestInit = {},
): Promise<T> {
  if (!token.trim()) {
    throw new CoursesApiError(401, errorMessage(401))
  }

  const headers = new Headers(options.headers)
  headers.set('Accept', 'application/json')
  headers.set('Authorization', `Bearer ${token}`)

  if (options.body !== undefined) {
    headers.set('Content-Type', 'application/json')
  }

  let response: Response

  try {
    response = await fetch(`/api/v1/cursos${path}`, {
      ...options,
      headers,
      cache: 'no-store',
    })
  } catch (error) {
    if (error instanceof Error && error.name === 'AbortError') {
      throw error
    }

    throw new CoursesApiError(
      0,
      'No se pudo conectar con el servidor. Comprueba la conexión.',
    )
  }

  if (!response.ok) {
    throw new CoursesApiError(
      response.status,
      errorMessage(response.status),
    )
  }

  if (response.status === 204) {
    return undefined as T
  }

  return response.json() as Promise<T>
}

export const coursesApi = {
  list(token: string, signal?: AbortSignal) {
    return request<Course[]>('', token, { signal })
  },

  find(id: number, token: string) {
    return request<Course>(`/${id}`, token)
  },

  create(input: CourseInput, token: string) {
    return request<Course>('', token, {
      method: 'POST',
      body: JSON.stringify(input),
    })
  },

  update(id: number, input: CourseInput, token: string) {
    return request<Course>(`/${id}`, token, {
      method: 'PUT',
      body: JSON.stringify(input),
    })
  },

  remove(id: number, token: string) {
    return request<void>(`/${id}`, token, {
      method: 'DELETE',
    })
  },
}