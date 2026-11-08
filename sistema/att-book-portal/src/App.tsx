import { useEffect, useState } from 'react'
import CoursesPage from './features/courses/CoursesPage'
import LoginPage from './features/auth/LoginPage'
import { authApi, AuthApiError } from './features/auth/authApi'
import type { AuthSession } from './features/auth/authApi'
import './App.css'

const SESSION_KEY = 'att-book.access-token'

function storedToken(): string | null {
  try {
    return sessionStorage.getItem(SESSION_KEY)
  } catch {
    return null
  }
}

function storeToken(token: string | null) {
  try {
    if (token) {
      sessionStorage.setItem(SESSION_KEY, token)
    } else {
      sessionStorage.removeItem(SESSION_KEY)
    }
  } catch {
    // Authentication still works in memory if storage is unavailable.
  }
}

function App() {
  const [session, setSession] = useState<AuthSession | null>(null)
  const [checking, setChecking] = useState(
  () => Boolean(storedToken()),
  )
  const [restoreError, setRestoreError] = useState('')
  const [notice, setNotice] = useState('')
  const [retry, setRetry] = useState(0)

  useEffect(() => {
    const controller = new AbortController()
    const token = storedToken()

    if (!token) {
     return () => controller.abort()
    }

    authApi.restore(token, controller.signal)
      .then((restored) => {
        if (!controller.signal.aborted) {
          setSession(restored)
        }
      })
      .catch((cause: unknown) => {
        if (controller.signal.aborted) return

        if (
          cause instanceof AuthApiError
          && [401, 403, 404].includes(cause.status)
        ) {
          storeToken(null)
          setSession(null)
          setNotice('La sesión ya no es válida. Ingresa nuevamente.')
        } else {
          setRestoreError(
            'No se pudo verificar la sesión. Comprueba la conexión.',
          )
        }
      })
      .finally(() => {
        if (!controller.signal.aborted) {
          setChecking(false)
        }
      })

    return () => controller.abort()
  }, [retry])

  useEffect(() => {
    if (!session) return

    function expireSession() {
      storeToken(null)
      setSession(null)
      setNotice('La sesión venció. Ingresa nuevamente.')
    }

    const remaining = session.expiresAt - Date.now()
    const timer = window.setTimeout(
      expireSession,
      Math.max(0, Math.min(remaining, 2147483647)),
    )

    function checkExpiration() {
      if (Date.now() >= session!.expiresAt) {
        expireSession()
      }
    }

    window.addEventListener('focus', checkExpiration)

    return () => {
      window.clearTimeout(timer)
      window.removeEventListener('focus', checkExpiration)
    }
  }, [session])

  function acceptSession(authenticated: AuthSession) {
    storeToken(authenticated.token)
    setSession(authenticated)
    setNotice('')
    setRestoreError('')
  }

  function logout() {
    storeToken(null)
    setSession(null)
    setRestoreError('')
    setNotice('Cerraste sesión.')
  }

  return (
    <div className="app">
      <header className="app-header">
        <div className="container">
          <a className="brand" href="/">
            Attendance Registry
          </a>
          <p className="brand-description">
            Gestión de cursos y asistencia
          </p>
        </div>
      </header>

      <main className="container main-content">
        {notice && <p role="status">{notice}</p>}

        {checking ? (
          <p role="status">Verificando sesión…</p>
        ) : restoreError ? (
          <section className="auth-panel">
            <p role="alert">{restoreError}</p>
            <button
              type="button"
              onClick={() => {
                setChecking(true)
                setRetry((value) => value + 1)
              }}
            >
              Reintentar
            </button>
            <button type="button" onClick={() => {
              setRestoreError('')
              setChecking(true)
              setRetry((value) => value + 1)
            }}>
              Volver al inicio de sesión
            </button>
          </section>
        ) : session ? (
          <>
            <div className="session-bar">
              <p>
                {session.user.nombre} ·{' '}
                {session.user.rol === 'TEACHER'
                  ? 'Docente'
                  : 'Estudiante'}
              </p>
              <button type="button" onClick={logout}>
                Cerrar sesión
              </button>
            </div>

            {session.user.rol === 'TEACHER' ? (
              <CoursesPage accessToken={session.token} />
            ) : (
              <section className="auth-panel">
                <h1>Mi cuenta de estudiante</h1>
                <p>Iniciaste sesión correctamente.</p>
                <p>
                  Las funciones de asistencia se incorporarán
                  al integrar el módulo correspondiente.
                </p>
              </section>
            )}
          </>
        ) : (
          <LoginPage onAuthenticated={acceptSession} />
        )}
      </main>

      <footer className="app-footer">
        <div className="container">
          Attendance Registry · CORHUILA
        </div>
      </footer>
    </div>
  )
}

export default App