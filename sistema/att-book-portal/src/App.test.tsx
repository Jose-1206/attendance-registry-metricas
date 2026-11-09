
// @vitest-environment jsdom

import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { cleanup, render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import App from './App'
import { authApi, AuthApiError } from './features/auth/authApi'
import type { AuthSession } from './features/auth/authApi'

vi.mock('./features/auth/authApi', async (importOriginal) => {
  const original = await importOriginal<
    typeof import('./features/auth/authApi')
  >()

  return {
    ...original,
    authApi: {
      login: vi.fn(),
      restore: vi.fn(),
    },
  }
})

vi.mock('./features/courses/CoursesPage', () => ({
  default: ({ accessToken }: { accessToken: string }) => (
    <section>
      <h1>Cursos protegidos</h1>
      <span data-testid="course-token">{accessToken}</span>
    </section>
  ),
}))

vi.mock('./features/enrollments/StudentEnrollmentsPage', () => ({
  default: ({ accessToken }: { accessToken: string }) => (
    <section>
      <h1>My enrollments</h1>
      <span data-testid="enrollment-token">{accessToken}</span>
    </section>
  ),
}))

const sessionKey = 'att-book.access-token'

function teacherSession(): AuthSession {
  return {
    token: 'teacher-test-token',
    user: {
      id: 3,
      nombre: 'Demo Teacher Owner',
      email: 'teacher.owner@example.test',
      rol: 'TEACHER',
    },
    expiresAt: Date.now() + 3600000,
  }
}

describe('Portal authentication', () => {
  beforeEach(() => {
    sessionStorage.clear()
    vi.resetAllMocks()
  })

  afterEach(() => {
    cleanup()
    sessionStorage.clear()
  })

  it('shows login when no token is stored', async () => {
    render(<App />)

    expect(
      await screen.findByRole('heading', { name: 'Iniciar sesión' }),
    ).toBeTruthy()

    expect(authApi.restore).not.toHaveBeenCalled()
    expect(
      screen.queryByRole('heading', { name: 'Cursos protegidos' }),
    ).toBeNull()
  })

  it('logs in, passes the token to courses and logs out', async () => {
    const user = userEvent.setup()
    vi.mocked(authApi.login).mockResolvedValue(teacherSession())

    render(<App />)

    await user.type(
      await screen.findByLabelText('Correo electrónico'),
      'teacher.owner@example.test',
    )
    await user.type(
      screen.getByLabelText('Contraseña'),
      'AttendanceDemo123!',
    )
    await user.click(screen.getByRole('button', { name: 'Ingresar' }))

    expect(
      await screen.findByRole('heading', { name: 'Cursos protegidos' }),
    ).toBeTruthy()

    expect(authApi.login).toHaveBeenCalledWith(
      'teacher.owner@example.test',
      'AttendanceDemo123!',
    )
    expect(screen.getByTestId('course-token').textContent)
      .toBe('teacher-test-token')
    expect(sessionStorage.getItem(sessionKey))
      .toBe('teacher-test-token')

    await user.click(
      screen.getByRole('button', { name: 'Cerrar sesión' }),
    )

    expect(
      await screen.findByRole('heading', { name: 'Iniciar sesión' }),
    ).toBeTruthy()
    expect(sessionStorage.getItem(sessionKey)).toBeNull()
  })

  it('shows an error when credentials are rejected', async () => {
    const user = userEvent.setup()

    vi.mocked(authApi.login).mockRejectedValue(
      new AuthApiError(401, 'Credenciales incorrectas.'),
    )

    render(<App />)

    await user.type(
      await screen.findByLabelText('Correo electrónico'),
      'teacher.owner@example.test',
    )
    await user.type(screen.getByLabelText('Contraseña'), 'WrongPass123!')
    await user.click(screen.getByRole('button', { name: 'Ingresar' }))

    expect((await screen.findByRole('alert')).textContent)
      .toBe('Credenciales incorrectas.')
    expect(sessionStorage.getItem(sessionKey)).toBeNull()
  })

  it('validates a stored token before showing courses', async () => {
    sessionStorage.setItem(sessionKey, 'teacher-test-token')
    vi.mocked(authApi.restore).mockResolvedValue(teacherSession())

    render(<App />)

    expect(
      await screen.findByRole('heading', { name: 'Cursos protegidos' }),
    ).toBeTruthy()

    expect(authApi.restore).toHaveBeenCalledWith(
      'teacher-test-token',
      expect.any(AbortSignal),
    )
  })

  it('removes a stored token rejected by the server', async () => {
    sessionStorage.setItem(sessionKey, 'invalid-token')

    vi.mocked(authApi.restore).mockRejectedValue(
      new AuthApiError(401, 'Invalid token'),
    )

    render(<App />)

    expect(
      await screen.findByRole('heading', { name: 'Iniciar sesión' }),
    ).toBeTruthy()

    expect(sessionStorage.getItem(sessionKey)).toBeNull()
  })

  it('preserves the token when the server cannot be reached', async () => {
    sessionStorage.setItem(sessionKey, 'teacher-test-token')

    vi.mocked(authApi.restore).mockRejectedValue(
      new TypeError('Failed to fetch'),
    )

    render(<App />)

    expect((await screen.findByRole('alert')).textContent)
      .toContain('No se pudo verificar la sesión')
    expect(sessionStorage.getItem(sessionKey))
      .toBe('teacher-test-token')
    expect(
      screen.queryByRole('heading', { name: 'Cursos protegidos' }),
    ).toBeNull()
  })

  it('does not show teacher course management to a student', async () => {
    sessionStorage.setItem(sessionKey, 'student-test-token')

    vi.mocked(authApi.restore).mockResolvedValue({
      token: 'student-test-token',
      user: {
        id: 5,
        nombre: 'Demo Enrolled Student',
        email: 'student.enrolled@example.test',
        rol: 'STUDENT',
      },
      expiresAt: Date.now() + 3600000,
    })

    render(<App />)

    expect(
      await screen.findByRole('heading', {
        name: 'My enrollments',
      }),
    ).toBeTruthy()

    expect(screen.getByTestId('enrollment-token').textContent)
      .toBe('student-test-token')

    expect(
      screen.queryByRole('heading', { name: 'Cursos protegidos' }),
    ).toBeNull()
  })

  it('clears an expired restored session', async () => {
    sessionStorage.setItem(sessionKey, 'expired-token')

    vi.mocked(authApi.restore).mockResolvedValue({
      ...teacherSession(),
      expiresAt: Date.now() - 1000,
    })

    render(<App />)

    await waitFor(() => {
      expect(sessionStorage.getItem(sessionKey)).toBeNull()
    })

    expect(
      await screen.findByRole('heading', { name: 'Iniciar sesión' }),
    ).toBeTruthy()
  })
})
