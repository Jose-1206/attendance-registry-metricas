import { useEffect, useState } from 'react'
import { teacherAttendanceApi } from './teacherAttendanceApi'
import type { AttendanceRecord } from './teacherAttendanceApi'

interface Props {
  sessionId: number
  accessToken: string
}

export default function TeacherAttendancePanel({ sessionId, accessToken }: Props) {
  const [rows, setRows] = useState<AttendanceRecord[]>([])
  const [loading, setLoading] = useState(true)
  const [reload, setReload] = useState(0)
  const [error, setError] = useState('')

  useEffect(() => {
    const controller = new AbortController()
    teacherAttendanceApi.listBySession(sessionId, accessToken, controller.signal)
      .then((result) => {
        if (!controller.signal.aborted) {
          setRows(result)
          setError('')
        }
      })
      .catch((cause: unknown) => {
        if (!controller.signal.aborted) {
          setError(cause instanceof Error ? cause.message : 'No se pudo consultar la asistencia.')
        }
      })
      .finally(() => {
        if (!controller.signal.aborted) setLoading(false)
      })
    return () => controller.abort()
  }, [accessToken, reload, sessionId])

  function refresh() {
    setLoading(true)
    setError('')
    setReload((value) => value + 1)
  }

  return (
    <section className="teacher-module" aria-labelledby="attendance-title">
      <h3 id="attendance-title">Asistencias registradas</h3>
      <button type="button" disabled={loading} onClick={refresh}>Actualizar asistencias</button>
      {loading && <p role="status">Consultando asistencias…</p>}
      {error && <p role="alert" className="teacher-error">{error}</p>}
      {!loading && !error && rows.length === 0 && (
        <p>Esta sesión todavía no tiene asistencias registradas.</p>
      )}
      {!loading && !error && rows.length > 0 && (
        <div className="teacher-table-wrap">
          <table className="teacher-table">
            <thead>
              <tr><th>Registro</th><th>ID de estudiante</th><th>Fecha y hora</th></tr>
            </thead>
            <tbody>
              {rows.map((row) => (
                <tr key={row.id}>
                  <td>{row.id}</td>
                  <td>{row.studentId}</td>
                  <td>{row.registeredAt.replace('T', ' ')}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </section>
  )
}
