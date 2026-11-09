
import { useEffect, useState } from 'react'
import TeacherDashboard from './features/teacher/TeacherDashboard'
import LoginPage from './features/auth/LoginPage'
import { RegisterPage } from './features/auth/RegisterPage'
import StudentEnrollmentsPage from './features/enrollments/StudentEnrollmentsPage'
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

function storeToken(token: string | null): void {
  try {
    if (token) {
      sessionStorage.setItem(SESSION_KEY, token)
    } else {
      sessionStorage.removeItem(SESSION_KEY)
    }
  } catch {
    // La autenticación continúa funcionando en memoria.
  }
}

function App() {
  const [authView, setAuthView] = useState<'login' | 'register'>('login')
  const [session, setSession] = useState<AuthSession | null>(null)
  const [checking, setChecking] = useState<boolean>(
    () => Boolean(storedToken())
  )
  const [restoreError, setRestoreError] = useState('')
  const [notice, setNotice] = useState('')
  const [retry, setRetry] = useState(0)

  useEffect(() => {
    const controller = new AbortController()
    const token = storedToken()

    if (!token) {
      setChecking(false)
      return () => controller.abort()
    }

    authApi
      .restore(token, controller.signal)
      .then((restored) => {
        if (!controller.signal.aborted) {
          setSession(restored)
        }
      })
      .catch((cause: unknown) => {
        if (controller.signal.aborted) return

        if (
          cause instanceof AuthApiError &&
          [401, 403, 404].includes(cause.status)
        ) {
          storeToken(null)
          setSession(null)
          setNotice('La sesión ya no es válida. Ingresa nuevamente.')
        } else {
          setRestoreError(
            'No se pudo verificar la sesión. Comprueba la conexión.'
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
      setAuthView('login')
      setNotice('La sesión venció. Ingresa nuevamente.')
    }

    const remaining = session.expiresAt - Date.now()

    const timer = window.setTimeout(
      expireSession,
      Math.max(0, Math.min(remaining, 2147483647))
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
    setAuthView('login')
    setNotice('')
    setRestoreError('')
  }

  function logout() {
    storeToken(null)
    setSession(null)
    setAuthView('login')
    setRestoreError('')
    setNotice('Cerraste sesión.')
  }

  function goToLogin() {
    setAuthView('login')
    setNotice('')
  }

  function goToRegister() {
    setAuthView('register')
    setNotice('')
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

            <button
              type="button"
              onClick={() => {
                storeToken(null)
                setSession(null)
                setRestoreError('')
                setChecking(false)
                setAuthView('login')
              }}
            >
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
              <TeacherDashboard accessToken={session.token} />
            ) : (
              <StudentEnrollmentsPage accessToken={session.token} />
            )}
          </>
        ) : authView === 'register' ? (
          <RegisterPage onGoToLogin={goToLogin} />
        ) : (
          <>
            <LoginPage onAuthenticated={acceptSession} />

            <div
              style={{
                textAlign: 'center',
                marginTop: '1rem'
              }}
            >
              <p>¿Todavía no tienes una cuenta?</p>

              <button
                type="button"
                onClick={goToRegister}
              >
                Registrarse como estudiante
              </button>
            </div>
          </>
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
