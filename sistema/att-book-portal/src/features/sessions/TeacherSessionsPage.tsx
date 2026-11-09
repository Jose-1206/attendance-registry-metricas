import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { coursesApi } from '../courses/coursesApi'
import type { Course } from '../courses/coursesApi'
import { sessionsApi } from './sessionsApi'
import type { ClassSession, SessionInput } from './sessionsApi'
import TeacherQrPanel from '../qr/TeacherQrPanel'
import TeacherAttendancePanel from '../attendance/TeacherAttendancePanel'
import './TeacherSessionsPage.css'

interface Props { accessToken: string }

type FormValues = {
  title: string
  description: string
  startAt: string
  endAt: string
}

const emptyForm: FormValues = {
  title: '', description: '', startAt: '', endAt: '',
}

function readableDate(value: string) {
  return value.replace('T', ' ').slice(0, 16)
}

function inputDate(value: string) {
  return value.slice(0, 16)
}

function apiDate(value: string) {
  return value.length === 16 ? `${value}:00` : value
}

function errorMessage(cause: unknown) {
  return cause instanceof Error ? cause.message : 'Ocurrió un error inesperado.'
}

export default function TeacherSessionsPage({ accessToken }: Props) {
  const [courses, setCourses] = useState<Course[]>([])
  const [courseId, setCourseId] = useState<number | null>(null)
  const [sessions, setSessions] = useState<ClassSession[]>([])
  const [selectedId, setSelectedId] = useState<number | null>(null)
  const [editingId, setEditingId] = useState<number | null>(null)
  const [form, setForm] = useState<FormValues>(emptyForm)
  const [loadingCourses, setLoadingCourses] = useState(true)
  const [loadingSessions, setLoadingSessions] = useState(false)
  const [saving, setSaving] = useState(false)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const [reload, setReload] = useState(0)

  useEffect(() => {
    const controller = new AbortController()
    coursesApi.list(accessToken, controller.signal)
      .then((result) => {
        if (controller.signal.aborted) return
        setCourses(result)
setLoadingSessions(result.length > 0)
setCourseId((current) => result.some((c) => c.id === current)
  ? current : (result[0]?.id ?? null))
      })
      .catch((cause: unknown) => {
        if (!controller.signal.aborted) setError(errorMessage(cause))
      })
      .finally(() => {
        if (!controller.signal.aborted) setLoadingCourses(false)
      })
    return () => controller.abort()
  }, [accessToken])

  useEffect(() => {
  if (courseId === null) return
  const controller = new AbortController()
  sessionsApi.listByCourse(courseId, accessToken, controller.signal)
      .then((result) => {
        if (!controller.signal.aborted) {
          setSessions(result)
          setSelectedId((current) => result.some((s) => s.id === current) ? current : null)
        }
      })
      .catch((cause: unknown) => {
        if (!controller.signal.aborted) {
          setSessions([])
          setSelectedId(null)
          setError(errorMessage(cause))
        }
      })
      .finally(() => {
        if (!controller.signal.aborted) setLoadingSessions(false)
      })
    return () => controller.abort()
  }, [accessToken, courseId, reload])

  const selectedSession = sessions.find((session) => session.id === selectedId) ?? null
  const disabled = saving || loadingCourses || loadingSessions

  function resetForm() {
    setEditingId(null)
    setForm(emptyForm)
  }

  function changeCourse(next: number) {
    setLoadingSessions(true)
    setCourseId(next)
    setSessions([])
    setSelectedId(null)
    resetForm()
    setNotice('')
    setError('')
  }

  function edit(session: ClassSession) {
    setEditingId(session.id)
    setForm({
      title: session.title,
      description: session.description ?? '',
      startAt: inputDate(session.startAt),
      endAt: inputDate(session.endAt),
    })
    setError('')
    setNotice('')
  }

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    if (courseId === null) return
    if (!form.title.trim() || !form.startAt || !form.endAt) {
      setError('Completa el título y las fechas de la sesión.')
      return
    }
    if (form.endAt <= form.startAt) {
      setError('La hora de finalización debe ser posterior a la de inicio.')
      return
    }

    const input: SessionInput = {
      courseId,
      title: form.title.trim(),
      description: form.description.trim(),
      startAt: apiDate(form.startAt),
      endAt: apiDate(form.endAt),
    }

    setSaving(true)
    setError('')
    setNotice('')
    try {
      const result = editingId === null
        ? await sessionsApi.create(input, accessToken)
        : await sessionsApi.update(editingId, input, accessToken)
      setSessions((old) => [...old.filter((s) => s.id !== result.id), result]
        .sort((a, b) => a.startAt.localeCompare(b.startAt)))
      setSelectedId(result.id)
      setNotice(editingId === null ? 'Sesión creada correctamente.' : 'Sesión actualizada correctamente.')
      resetForm()
    } catch (cause) {
      setError(errorMessage(cause))
    } finally {
      setSaving(false)
    }
  }

  async function remove(session: ClassSession) {
    if (!window.confirm(`¿Eliminar la sesión "${session.title}"?`)) return
    setSaving(true)
    setError('')
    setNotice('')
    try {
      await sessionsApi.remove(session.id, accessToken)
      setSessions((old) => old.filter((s) => s.id !== session.id))
      if (selectedId === session.id) setSelectedId(null)
      if (editingId === session.id) resetForm()
      setNotice('Sesión eliminada correctamente.')
    } catch (cause) {
      setError(errorMessage(cause))
    } finally {
      setSaving(false)
    }
  }

  return (
    <section className="teacher-sessions" aria-labelledby="teacher-sessions-title">
      <div className="teacher-heading">
        <div>
          <p className="eyebrow">Portal del docente</p>
          <h1 id="teacher-sessions-title">Sesiones y asistencia</h1>
          <p>Administra las clases, genera códigos QR y consulta la asistencia.</p>
        </div>
        <button type="button" disabled={disabled || courseId === null}
          onClick={() => {
            setLoadingSessions(true)
            setError('')
            setReload((x) => x + 1)
          }}>
          Actualizar sesiones
        </button>
      </div>

      {error && <p role="alert" className="teacher-error">{error}</p>}
      {notice && <p role="status" className="teacher-notice">{notice}</p>}
      {loadingCourses ? <p role="status">Cargando tus cursos…</p> : (
        <>
          {courses.length === 0 ? (
            <p>No tienes cursos todavía. Crea uno en la sección Mis cursos.</p>
          ) : (
            <>
              <label className="teacher-course-picker" htmlFor="teacher-course">
                Curso
                <select id="teacher-course" value={courseId ?? ''}
                  disabled={saving}
                  onChange={(event) => changeCourse(Number(event.target.value))}>
                  {courses.map((course) => (
                    <option key={course.id} value={course.id}>
                      {course.nombre} ({course.codigo})
                    </option>
                  ))}
                </select>
              </label>
              <div className="teacher-grid">
                <form onSubmit={(event) => void submit(event)} className="teacher-module">
                  <h2>{editingId === null ? 'Crear sesión' : 'Editar sesión'}</h2>
                  <fieldset disabled={disabled}>
                    <label htmlFor="session-title">Título</label>
                    <input id="session-title" required maxLength={150}
                      value={form.title}
                      onChange={(event) => setForm((old) => ({ ...old, title: event.target.value }))}/>

                    <label htmlFor="session-description">Descripción</label>
                    <textarea id="session-description" maxLength={500}
                      value={form.description}
                      onChange={(event) => setForm((old) => ({ ...old, description: event.target.value }))}/>

                    <label htmlFor="session-start">Fecha y hora de inicio</label>
                    <input id="session-start" type="datetime-local" required
                      value={form.startAt}
                      onChange={(event) => setForm((old) => ({ ...old, startAt: event.target.value }))}/>

                    <label htmlFor="session-end">Fecha y hora de finalización</label>
                    <input id="session-end" type="datetime-local" required
                      value={form.endAt}
                      onChange={(event) => setForm((old) => ({ ...old, endAt: event.target.value }))}/>
                    <div className="teacher-actions">
                      <button type="submit">{saving ? 'Guardando…' : editingId === null ? 'Crear sesión' : 'Guardar cambios'}</button>
                      {editingId !== null && <button type="button" onClick={resetForm}>Cancelar edición</button>}
                    </div>
                  </fieldset>
                </form>

                <div className="teacher-module">
                  <h2>Sesiones del curso</h2>
                  {loadingSessions && <p role="status">Cargando sesiones…</p>}
                  {!loadingSessions && sessions.length === 0 && <p>No se encontraron sesiones.</p>}
                  <ul className="teacher-session-list">
                    {sessions.map((session) => (
                      <li className="teacher-session-item" key={session.id}>
                        <h3>{session.title}</h3>
                        <p>{readableDate(session.startAt)} – {readableDate(session.endAt)}</p>
                        {session.description && <p>{session.description}</p>}
                        <div className="teacher-actions">
                          <button type="button" disabled={disabled} onClick={() => {
                            setSelectedId(session.id)
                            setError('')
                          }}>QR y asistencia</button>
                          <button type="button" disabled={disabled} onClick={() => edit(session)}>Editar</button>
                          <button type="button" disabled={disabled} onClick={() => void remove(session)}>Eliminar</button>
                        </div>
                      </li>
                    ))}
                  </ul>
                </div>
              </div>
              {selectedSession && (
                <div className="teacher-selected-session">
                  <h2>Sesión seleccionada: {selectedSession.title}</h2>
                  <div className="teacher-grid">
                    <TeacherQrPanel key={selectedSession.id} sessionId={selectedSession.id} accessToken={accessToken} />
                    <TeacherAttendancePanel key={selectedSession.id} sessionId={selectedSession.id} accessToken={accessToken} />
                  </div>
                </div>
              )}
            </>
          )}
        </>
      )}
    </section>
  )
}
