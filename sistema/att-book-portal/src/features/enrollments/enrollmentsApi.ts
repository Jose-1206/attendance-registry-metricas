
export interface Enrollment {
  id: number
  studentId: number
  courseId: number
  enrollmentDate: string
}

export class EnrollmentsApiError extends Error {
  readonly status: number

  constructor(status: number, message: string) {
    super(message)
    this.name = 'EnrollmentsApiError'
    this.status = status
  }
}

function errorMessage(status: number): string {
  switch (status) {
    case 400:
      return 'The enrollment request is invalid.'
    case 401:
      return 'Your session has expired. Please sign in again.'
    case 403:
      return 'You do not have permission to enroll.'
    case 404:
      return 'The requested course was not found.'
    case 409:
      return 'You are already enrolled in this course.'
    default:
      return 'Unable to complete the request. Please try again.'
  }
}

async function request<T>(
  path: string,
  token: string,
  options: RequestInit = {},
): Promise<T> {
  if (!token.trim()) {
    throw new EnrollmentsApiError(401, errorMessage(401))
  }

  const headers = new Headers(options.headers)
  headers.set('Accept', 'application/json')
  headers.set('Authorization', `Bearer ${token}`)

  let response: Response

  try {
    response = await fetch(`/api/v1/enrollments${path}`, {
      ...options,
      headers,
      cache: 'no-store',
    })
  } catch (error) {
    if (error instanceof Error && error.name === 'AbortError') {
      throw error
    }

    throw new EnrollmentsApiError(
      0,
      'Unable to connect to the server.',
    )
  }

  if (!response.ok) {
    throw new EnrollmentsApiError(
      response.status,
      errorMessage(response.status),
    )
  }

  return response.json() as Promise<T>
}

export const enrollmentsApi = {
  listMine(token: string, signal?: AbortSignal) {
    return request<Enrollment[]>('/me', token, { signal })
  },

  enroll(courseId: number, token: string) {
    if (!Number.isSafeInteger(courseId) || courseId <= 0) {
      throw new EnrollmentsApiError(
        400,
        'A valid course ID is required.',
      )
    }

    return request<Enrollment>(
      `/courses/${courseId}`,
      token,
      { method: 'POST' },
    )
  },
}
