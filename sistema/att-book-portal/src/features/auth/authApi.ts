export interface AuthUser {
  id: number
  nombre: string
  email: string
  rol: 'TEACHER' | 'STUDENT'
}

export interface AuthSession {
  token: string
  user: AuthUser
  expiresAt: number
}

export class AuthApiError extends Error {
  readonly status: number

  constructor(status: number, message: string) {
    super(message)
    this.name = 'AuthApiError'
    this.status = status
  }
}

function parseUser(value: unknown): AuthUser {
  if (typeof value !== 'object' || value === null) {
    throw new Error('La respuesta de usuario no es válida.')
  }

  const user = value as Record<string, unknown>

  if (
    typeof user.id !== 'number'
    || !Number.isSafeInteger(user.id)
    || user.id <= 0
    || typeof user.nombre !== 'string'
    || typeof user.email !== 'string'
    || (user.rol !== 'TEACHER' && user.rol !== 'STUDENT')
  ) {
    throw new Error('La respuesta de usuario no es válida.')
  }

  return {
    id: user.id,
    nombre: user.nombre,
    email: user.email,
    rol: user.rol,
  }
}

function sessionFrom(token: string, user: unknown): AuthSession {
  try {
    const payload = token.split('.')[1]
    const normalized = payload.replace(/-/g, '+').replace(/_/g, '/')
    const padded = normalized.padEnd(
      Math.ceil(normalized.length / 4) * 4,
      '=',
    )
    const claims = JSON.parse(atob(padded)) as Record<string, unknown>
    const expiration = claims.exp

    if (
      typeof expiration !== 'number'
      || !Number.isFinite(expiration)
      || expiration * 1000 <= Date.now()
    ) {
      throw new Error('Expired token')
    }

    return {
      token,
      user: parseUser(user),
      expiresAt: expiration * 1000,
    }
  } catch {
    throw new AuthApiError(401, 'La sesión venció. Inicia sesión nuevamente.')
  }
}

async function request(
  path: string,
  options: RequestInit,
): Promise<unknown> {
  const response = await fetch(`/api/v1${path}`, {
    ...options,
    cache: 'no-store',
  })

  if (!response.ok) {
    const message = response.status === 401
      ? 'Correo o contraseña incorrectos, o sesión vencida.'
      : response.status === 400
        ? 'Revisa los datos ingresados.'
        : 'No fue posible completar la solicitud.'

    throw new AuthApiError(response.status, message)
  }

  return response.json()
}

export const authApi = {
  async login(email: string, password: string): Promise<AuthSession> {
    const result = await request('/auth/login', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        Accept: 'application/json',
      },
      body: JSON.stringify({ email: email.trim(), password }),
    })

    if (typeof result !== 'object' || result === null) {
      throw new Error('La respuesta de autenticación no es válida.')
    }

    const data = result as Record<string, unknown>

    if (typeof data.token !== 'string' || !data.token) {
      throw new Error('El servidor no devolvió una sesión válida.')
    }

    return sessionFrom(data.token, data.user)
  },

  async restore(
    token: string,
    signal: AbortSignal,
  ): Promise<AuthSession> {
    const user = await request('/usuarios/me', {
      signal,
      headers: {
        Accept: 'application/json',
        Authorization: `Bearer ${token}`,
      },
    })

    return sessionFrom(token, user)
  },
}