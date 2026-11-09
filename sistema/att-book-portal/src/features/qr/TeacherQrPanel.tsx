import { useEffect, useState } from 'react'
import { QRCodeSVG } from 'qrcode.react'
import { teacherQrApi } from './teacherQrApi'
import type { QrResponse } from './teacherQrApi'

interface Props {
  sessionId: number
  accessToken: string
}

function remainingSeconds(value: string, now: number): number {
  // An ISO date without an offset is interpreted as local time by the browser.
  // Prefer an offset-aware expiresAt from the backend to avoid ambiguity.
  const end = Date.parse(value)
  return Number.isFinite(end) ? Math.max(0, Math.ceil((end - now) / 1000)) : 0
}

export default function TeacherQrPanel({ sessionId, accessToken }: Props) {
  const [qr, setQr] = useState<QrResponse | null>(null)
  const [now, setNow] = useState(() => Date.now())
  const [loading, setLoading] = useState(false)
  const [error, setError] = useState('')

  useEffect(() => {
    const timer = window.setInterval(() => setNow(Date.now()), 1000)
    return () => window.clearInterval(timer)
  }, [])

  const seconds = qr ? remainingSeconds(qr.expiresAt, now) : 0
  const active = Boolean(qr?.qr && seconds > 0)

  async function generate() {
    setLoading(true)
    setError('')
    setQr(null)
    try {
      const result = await teacherQrApi.generate(sessionId, accessToken)
      if (!result.qr || !result.expiresAt) throw new Error('Respuesta de QR incompleta.')
      setQr(result)
      setNow(Date.now())
    } catch (cause) {
      setError(cause instanceof Error ? cause.message : 'No se pudo generar el QR.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <section className="teacher-module" aria-labelledby="qr-panel-title">
      <h3 id="qr-panel-title">Código QR de la sesión</h3>
      <p>Genera un código QR para que los estudiantes inscritos registren su asistencia.</p>
      <button type="button" onClick={() => void generate()} disabled={loading}>
        {loading ? 'Generando…' : qr ? 'Generar otro QR' : 'Generar QR'}
      </button>
      {error && <p role="alert" className="teacher-error">{error}</p>}
      {active && qr && (
        <div className="teacher-qr" aria-label="Código QR de asistencia">
          <QRCodeSVG value={qr.qr} size={220} level="M" marginSize={4} />
          <p>Vence en {Math.floor(seconds / 60)}:{String(seconds % 60).padStart(2, '0')}</p>
          <p>Vencimiento: {qr.expiresAt.replace('T', ' ')}</p>
        </div>
      )}
      {qr && !active && (
        <p role="status">El código expiró o su fecha de vencimiento no es válida. Genera uno nuevo.</p>
      )}
    </section>
  )
}
