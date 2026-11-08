import { useState } from 'react'
import type { FormEvent } from 'react'
import { authApi } from './authApi'
import type { AuthSession } from './authApi'
import './auth.css'

interface LoginPageProps {
  onAuthenticated: (session: AuthSession) => void
}

export default function LoginPage({
  onAuthenticated,
}: LoginPageProps) {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setBusy(true)
    setError('')

    try {
      const session = await authApi.login(email, password)
      setPassword('')
      onAuthenticated(session)
    } catch (cause) {
      setError(
        cause instanceof Error
          ? cause.message
          : 'No fue posible iniciar sesión.',
      )
    } finally {
      setBusy(false)
    }
  }

  return (
    <section className="auth-panel" aria-labelledby="login-title">
      <h1 id="login-title">Iniciar sesión</h1>
      <p>Ingresa con tu cuenta de Attendance Registry.</p>

      {error && <p role="alert">{error}</p>}

      <form onSubmit={submit} aria-busy={busy}>
        <fieldset disabled={busy}>
          <label htmlFor="login-email">Correo electrónico</label>
          <input
            id="login-email"
            type="email"
            autoComplete="username"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            maxLength={254}
            required
          />

          <label htmlFor="login-password">Contraseña</label>
          <input
            id="login-password"
            type="password"
            autoComplete="current-password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            maxLength={72}
            required
          />

          <button type="submit">
            {busy ? 'Ingresando…' : 'Ingresar'}
          </button>
        </fieldset>
      </form>
    </section>
  )
}