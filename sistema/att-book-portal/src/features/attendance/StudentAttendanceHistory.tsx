
import { useEffect, useState } from 'react'
import { studentAttendanceApi } from './studentAttendanceApi'
import type { StudentAttendance } from './studentAttendanceApi'

interface Props {
  accessToken: string
  refreshKey?: number
}

export default function StudentAttendanceHistory({
  accessToken,
  refreshKey = 0,
}: Props) {
  const [records, setRecords] = useState<StudentAttendance[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  useEffect(() => {
    let active = true

    studentAttendanceApi.history(accessToken)
      .then((result) => {
        if (active) setRecords(result)
      })
      .catch((cause: unknown) => {
        if (active) {
          setError(
            cause instanceof Error
              ? cause.message
              : 'No se pudo consultar el historial.',
          )
        }
      })
      .finally(() => {
        if (active) setLoading(false)
      })

    return () => {
      active = false
    }
 }, [accessToken, refreshKey])

  return (
    <section aria-labelledby="attendance-history-title">
      <h2 id="attendance-history-title">Mi historial de asistencias</h2>

      {loading ? (
        <p>Cargando asistencias...</p>
      ) : error ? (
        <p role="alert">{error}</p>
      ) : records.length === 0 ? (
        <p>Aún no tienes asistencias registradas.</p>
      ) : (
        <ul>
          {records.map((record) => (
            <li key={record.id}>
              <strong>Sesión #{record.claseId}</strong>
              <p>
                Fecha: {new Date(record.registeredAt).toLocaleString('es-CO')}
              </p>
            </li>
          ))}
        </ul>
      )}
    </section>
  )
}
