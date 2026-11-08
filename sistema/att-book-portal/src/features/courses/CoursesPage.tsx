import { useEffect, useState } from 'react'
import type { FormEvent } from 'react'
import { coursesApi } from './coursesApi'
import type { Course } from './coursesApi'
import './CoursesPage.css'

interface CoursesPageProps {
  accessToken: string | null
}

function messageFrom(error: unknown): string {
  return error instanceof Error
    ? error.message
    : 'Ocurrió un error inesperado.'
}

export default function CoursesPage({ accessToken }: CoursesPageProps) {
  if (!accessToken?.trim()) {
    return (
      <section className="courses-panel" aria-labelledby="courses-title">
        <h1 id="courses-title">Mis cursos</h1>
        <p>Inicia sesión como docente para gestionar tus cursos.</p>
      </section>
    )
  }

  return (
    <AuthenticatedCourses
      key={accessToken}
      accessToken={accessToken}
    />
  )
}

function AuthenticatedCourses({
  accessToken,
}: {
  accessToken: string
}) {
  const [courses, setCourses] = useState<Course[]>([])
  const [loading, setLoading] = useState(true)
  const [busy, setBusy] = useState(false)
  const [error, setError] = useState('')
  const [notice, setNotice] = useState('')
  const [reload, setReload] = useState(0)

  const [editingId, setEditingId] = useState<number | null>(null)
  const [nombre, setNombre] = useState('')
  const [codigo, setCodigo] = useState('')
  const [detail, setDetail] = useState<Course | null>(null)

  useEffect(() => {
    const controller = new AbortController()

    coursesApi.list(accessToken, controller.signal)
      .then((result) => {
        if (!controller.signal.aborted) {
          setCourses(result)
        }
      })
      .catch((cause: unknown) => {
        if (!controller.signal.aborted) {
          setError(messageFrom(cause))
        }
      })
      .finally(() => {
        if (!controller.signal.aborted) {
          setLoading(false)
        }
      })

    return () => controller.abort()
  }, [accessToken, reload])

  const disabled = loading || busy

  function clearForm() {
    setEditingId(null)
    setNombre('')
    setCodigo('')
  }

  function edit(course: Course) {
    setEditingId(course.id)
    setNombre(course.nombre)
    setCodigo(course.codigo)
    setError('')
    setNotice('')
  }

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    const input = {
      nombre: nombre.trim(),
      codigo: codigo.trim(),
    }

    if (!input.nombre || !input.codigo) {
      setError('El nombre y el código son obligatorios.')
      return
    }

    setBusy(true)
    setError('')
    setNotice('')

    try {
      const saved = editingId === null
        ? await coursesApi.create(input, accessToken)
        : await coursesApi.update(editingId, input, accessToken)

      setCourses((current) => (
        editingId === null
          ? [...current, saved]
          : current.map((course) => (
              course.id === saved.id ? saved : course
            ))
      ))

      setDetail((current) => (
        current?.id === saved.id ? saved : current
      ))

      setNotice(
        editingId === null
          ? 'Curso creado correctamente.'
          : 'Curso actualizado correctamente.',
      )

      clearForm()
    } catch (cause) {
      setError(messageFrom(cause))
    } finally {
      setBusy(false)
    }
  }

  async function view(course: Course) {
    setBusy(true)
    setError('')
    setDetail(null)

    try {
      setDetail(await coursesApi.find(course.id, accessToken))
    } catch (cause) {
      setError(messageFrom(cause))
    } finally {
      setBusy(false)
    }
  }

  async function remove(course: Course) {
    const confirmed = window.confirm(
      `¿Eliminar el curso "${course.nombre}"? Esta acción no se puede deshacer.`,
    )

    if (!confirmed) return

    setBusy(true)
    setError('')
    setNotice('')

    try {
      await coursesApi.remove(course.id, accessToken)

      setCourses((current) => (
        current.filter((item) => item.id !== course.id)
      ))

      if (editingId === course.id) clearForm()
      if (detail?.id === course.id) setDetail(null)

      setNotice('Curso eliminado correctamente.')
    } catch (cause) {
      setError(messageFrom(cause))
    } finally {
      setBusy(false)
    }
  }

  return (
    <section className="courses-panel" aria-labelledby="courses-title">
      <div className="courses-heading">
        <div>
          <p className="eyebrow">Gestión académica</p>
          <h1 id="courses-title">Mis cursos</h1>
          <p>Consulta y administra tus cursos.</p>
        </div>

        <button
          type="button"
          className="secondary-button"
          disabled={disabled}
          onClick={() => {
            setLoading(true)
            setError('')
            setNotice('')
            setDetail(null)
            setReload((value) => value + 1)
          }}
        >
          Actualizar lista
        </button>
      </div>

      {error && <p className="courses-error" role="alert">{error}</p>}
      {notice && <p className="courses-notice" role="status">{notice}</p>}
      {busy && <p role="status">Procesando operación…</p>}

      <div className="courses-layout">
        <form className="course-form" onSubmit={submit}>
          <h2>{editingId === null ? 'Crear curso' : 'Editar curso'}</h2>

          <fieldset disabled={disabled}>
            <label htmlFor="course-name">Nombre del curso</label>
            <input
              id="course-name"
              value={nombre}
              onChange={(event) => setNombre(event.target.value)}
              maxLength={150}
              required
            />

            <label htmlFor="course-code">Código del curso</label>
            <input
              id="course-code"
              value={codigo}
              onChange={(event) => setCodigo(event.target.value)}
              maxLength={30}
              required
            />

            <div className="course-actions">
              <button type="submit">
                {editingId === null ? 'Crear curso' : 'Guardar cambios'}
              </button>

              {editingId !== null && (
                <button
                  type="button"
                  className="secondary-button"
                  onClick={clearForm}
                >
                  Cancelar edición
                </button>
              )}
            </div>
          </fieldset>
        </form>

        <div aria-busy={disabled}>
          <h2>Cursos registrados</h2>

          {loading ? (
            <p role="status">Cargando cursos…</p>
          ) : courses.length === 0 ? (
            <p>
              {error
                ? 'No se pudo obtener la lista de cursos.'
                : 'Todavía no tienes cursos registrados.'}
            </p>
          ) : (
            <ul className="course-list">
              {courses.map((course) => (
                <li key={course.id} className="course-card">
                  <h3>{course.nombre}</h3>
                  <p>Código: {course.codigo}</p>

                  <div className="course-actions">
                    <button
                      type="button"
                      className="secondary-button"
                      disabled={disabled}
                      aria-label={`Ver detalles de ${course.nombre}`}
                      onClick={() => void view(course)}
                    >
                      Ver detalles
                    </button>

                    <button
                      type="button"
                      className="secondary-button"
                      disabled={disabled}
                      aria-label={`Editar ${course.nombre}`}
                      onClick={() => edit(course)}
                    >
                      Editar
                    </button>

                    <button
                      type="button"
                      className="danger-button"
                      disabled={disabled}
                      aria-label={`Eliminar ${course.nombre}`}
                      onClick={() => void remove(course)}
                    >
                      Eliminar
                    </button>
                  </div>
                </li>
              ))}
            </ul>
          )}

          {detail && (
            <section className="course-detail" aria-labelledby="detail-title">
              <h2 id="detail-title">Detalle del curso</h2>
              <dl>
                <dt>Identificador</dt>
                <dd>{detail.id}</dd>
                <dt>Nombre</dt>
                <dd>{detail.nombre}</dd>
                <dt>Código</dt>
                <dd>{detail.codigo}</dd>
                <dt>Identificador del docente</dt>
                <dd>{detail.profesorId}</dd>
              </dl>
              <button
                type="button"
                className="secondary-button"
                onClick={() => setDetail(null)}
              >
                Cerrar detalle
              </button>
            </section>
          )}
        </div>
      </div>
    </section>
  )
}