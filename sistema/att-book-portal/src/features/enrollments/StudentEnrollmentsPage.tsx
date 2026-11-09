
import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { enrollmentsApi } from './enrollmentsApi'
import type { Enrollment } from './enrollmentsApi'
import StudentQrScanner from '../qr/StudentQrScanner'
import StudentAttendanceHistory from '../attendance/StudentAttendanceHistory'
import { studentAttendanceApi } from '../attendance/studentAttendanceApi'
import './StudentEnrollmentsPage.css'

interface StudentEnrollmentsPageProps {
  accessToken: string
}

function errorMessage(error: unknown): string {
  return error instanceof Error
    ? error.message
    : 'Ocurrió un error inesperado.'
}

export default function StudentEnrollmentsPage({
  accessToken,
}: StudentEnrollmentsPageProps) {
  const [enrollments, setEnrollments] = useState<Enrollment[]>([])
  const [courseId, setCourseId] = useState('')
  const [manualQr, setManualQr] = useState('')
  const [loading, setLoading] = useState(true)
  const [busy, setBusy] = useState(false)
  const [attendanceBusy, setAttendanceBusy] = useState(false)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const [attendanceError, setAttendanceError] = useState('')
  const [attendanceNotice, setAttendanceNotice] = useState('')
  const [reload, setReload] = useState(0)
  const [historyRefresh, setHistoryRefresh] = useState(0)

  useEffect(() => {
    const controller = new AbortController()

    enrollmentsApi.listMine(accessToken, controller.signal)
      .then((result) => {
        if (!controller.signal.aborted) {
          setEnrollments(result)
          setError('')
        }
      })
      .catch((cause: unknown) => {
        if (!controller.signal.aborted) {
          setError(errorMessage(cause))
        }
      })
      .finally(() => {
        if (!controller.signal.aborted) setLoading(false)
      })

    return () => controller.abort()
  }, [accessToken, reload])

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    const id = Number(courseId)

    if (!Number.isSafeInteger(id) || id <= 0) {
      setError('Ingresa un ID de curso válido.')
      return
    }

    if (enrollments.some((item) => item.courseId === id)) {
      setError('Ya estás inscrito en este curso.')
      return
    }

    setBusy(true)
    setError('')
    setNotice('')

    try {
      const created = await enrollmentsApi.enroll(id, accessToken)
      setEnrollments((current) => [...current, created])
      setCourseId('')
      setNotice('Inscripción realizada correctamente.')
    } catch (cause) {
      setError(errorMessage(cause))
    } finally {
      setBusy(false)
    }
  }

  async function registerAttendance(qr: string) {
    if (attendanceBusy) return

    setAttendanceBusy(true)
    setAttendanceError('')
    setAttendanceNotice('Procesando código QR...')

    try {
      await studentAttendanceApi.register(qr, accessToken)
      setAttendanceNotice('Asistencia registrada correctamente.')
      setManualQr('')
      setHistoryRefresh((value) => value + 1)
    } catch (cause) {
      setAttendanceNotice('')
      setAttendanceError(errorMessage(cause))
    } finally {
      setAttendanceBusy(false)
    }
  }

  function refresh() {
    setLoading(true)
    setError('')
    setNotice('')
    setReload((current) => current + 1)
  }

  return (
    <section
      className="enrollments-panel"
      aria-labelledby="enrollments-title"
    >
      <div className="enrollments-heading">
        <div>
          <p className="enrollments-eyebrow">Portal del estudiante</p>
          <h1 id="enrollments-title">Mis inscripciones</h1>
          <p>Gestiona tus cursos y asistencias.</p>
        </div>

        <button
          type="button"
          className="enrollments-secondary"
          disabled={loading || busy}
          onClick={refresh}
        >
          Actualizar
        </button>
      </div>

      {error && (
        <p className="enrollments-error" role="alert">
          {error}
        </p>
      )}

      {notice && (
        <p className="enrollments-notice" role="status">
          {notice}
        </p>
      )}

      <div className="enrollments-layout">
        <form className="enrollments-form" onSubmit={submit}>
          <h2>Inscribirse en un curso</h2>

          <label htmlFor="enrollment-course-id">
            ID del curso
          </label>

          <input
            id="enrollment-course-id"
            type="number"
            min="1"
            step="1"
            required
            value={courseId}
            onChange={(event) => setCourseId(event.target.value)}
            disabled={loading || busy}
            placeholder="Ingresa el ID del curso"
          />

          <button type="submit" disabled={loading || busy}>
            {busy ? 'Inscribiendo...' : 'Inscribirse'}
          </button>
        </form>

        <div className="enrollments-results" aria-busy={loading}>
          <h2>Cursos inscritos</h2>

          {loading ? (
            <p role="status">Cargando inscripciones...</p>
          ) : error && enrollments.length === 0 ? (
            <p>No se pudieron mostrar las inscripciones.</p>
          ) : enrollments.length === 0 ? (
            <p>Todavía no estás inscrito en ningún curso.</p>
          ) : (
            <ul className="enrollments-list">
              {enrollments.map((enrollment) => (
                <li
                  key={enrollment.id}
                  className="enrollments-card"
                >
                  <h3>Curso #{enrollment.courseId}</h3>
                  <p>ID de inscripción: {enrollment.id}</p>
                  <p>
                    Fecha:{' '}
                    {enrollment.enrollmentDate
                      ? new Date(enrollment.enrollmentDate)
                          .toLocaleDateString('es-CO')
                      : 'No disponible'}
                  </p>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>

      <div className="enrollments-results">
        <h2>Registrar asistencia con QR</h2>

        <StudentQrScanner
          onScan={(qr) => {
            void registerAttendance(qr)
          }}
        />

        <form
          onSubmit={(event) => {
            event.preventDefault()
            void registerAttendance(manualQr)
          }}
        >
          <h3>Ingresar código manualmente</h3>

          <label htmlFor="manual-qr">
            Código de asistencia
          </label>

          <input
            id="manual-qr"
            type="text"
            value={manualQr}
            onChange={(event) => setManualQr(event.target.value)}
            disabled={attendanceBusy}
            required
            placeholder="Ingresa el código QR"
          />

          <button type="submit" disabled={attendanceBusy}>
            {attendanceBusy
              ? 'Registrando...'
              : 'Registrar asistencia'}
          </button>
        </form>

        {attendanceError && (
          <p className="enrollments-error" role="alert">
            {attendanceError}
          </p>
        )}

        {attendanceNotice && (
          <p className="enrollments-notice" role="status">
            {attendanceNotice}
          </p>
        )}
      </div>

      <div className="enrollments-results">
        <StudentAttendanceHistory
          accessToken={accessToken}
          refreshKey={historyRefresh}
        />
      </div>
    </section>
  )
}
