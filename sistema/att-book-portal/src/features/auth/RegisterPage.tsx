
import { useState, type FormEvent } from 'react'
import {
  authApi,
  AuthApiError,
} from './authApi'
import './auth.css'

interface RegisterPageProps {
  onGoToLogin?: () => void
}

export function RegisterPage({
  onGoToLogin,
}: RegisterPageProps) {
  const [nombre, setNombre] = useState('')
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')
  const [success, setSuccess] = useState('')

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (loading) return

    setError('')
    setSuccess('')
    setLoading(true)

    try {
      await authApi.register({
        nombre,
        email,
        password,
      })

      setSuccess(
        'Cuenta creada correctamente. Ya puedes iniciar sesión.',
      )
      setPassword('')
    } catch (err) {
      if (err instanceof AuthApiError) {
        setError(err.message)
      } else {
        setError('Ocurrió un error inesperado.')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className="register-page">
      <section className="auth-panel">
        <h1>Registro de estudiantes</h1>
        <p>Crea tu cuenta en Attendance Registry.</p>

        <form onSubmit={handleSubmit}>
          <label htmlFor="register-nombre">
            Nombre completo
          </label>
          <input
            id="register-nombre"
            type="text"
            value={nombre}
            onChange={(event) => setNombre(event.target.value)}
            autoComplete="name"
            required
            disabled={loading || Boolean(success)}
          />

          <label htmlFor="register-email">
            Correo electrónico
          </label>
          <input
            id="register-email"
            type="email"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            autoComplete="email"
            required
            disabled={loading || Boolean(success)}
          />

          <label htmlFor="register-password">
            Contraseña
          </label>
          <input
            id="register-password"
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            autoComplete="new-password"
            minLength={8}
            required
            disabled={loading || Boolean(success)}
          />

          <p>La contraseña debe tener al menos 8 caracteres.</p>

          {error && (
            <p role="alert" className="register-error">
              {error}
            </p>
          )}

          {success && (
            <p role="status" className="register-success">
              {success}
            </p>
          )}

          {!success && (
            <button type="submit" disabled={loading}>
              {loading ? 'Registrando...' : 'Crear cuenta'}
            </button>
          )}
        </form>

        {onGoToLogin && (
          <button
            type="button"
            onClick={onGoToLogin}
            disabled={loading}
          >
            Ir a iniciar sesión
          </button>
        )}
      </section>
    </main>
  )
}
