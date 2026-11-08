// @vitest-environment jsdom

import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { cleanup, render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import CoursesPage from './CoursesPage'
import { coursesApi } from './coursesApi'

vi.mock('./coursesApi', () => ({
  coursesApi: {
    list: vi.fn(),
    find: vi.fn(),
    create: vi.fn(),
    update: vi.fn(),
    remove: vi.fn(),
  },
}))

const course = {
  id: 1,
  nombre: 'Fullstack',
  codigo: 'FS-01',
  profesorId: 10,
}

const testToken = 'test-token'

async function renderAuthenticatedPage() {
  const user = userEvent.setup()

  render(<CoursesPage accessToken={testToken} />)

  await screen.findByRole('heading', { name: 'Fullstack' })

  return user
}

describe('CoursesPage', () => {
  beforeEach(() => {
    vi.resetAllMocks()
    vi.mocked(coursesApi.list).mockResolvedValue([{ ...course }])
  })

  afterEach(() => {
    cleanup()
    vi.restoreAllMocks()
  })

  it('requests login without calling the API when no session exists', () => {
    render(<CoursesPage accessToken={null} />)

    expect(
      screen.getByText(
        'Inicia sesión como docente para gestionar tus cursos.',
      ),
    ).toBeTruthy()

    expect(coursesApi.list).not.toHaveBeenCalled()
  })

  it('displays courses returned by the API', async () => {
    await renderAuthenticatedPage()

    expect(screen.getByText('Código: FS-01')).toBeTruthy()

    expect(coursesApi.list).toHaveBeenCalledWith(
      testToken,
      expect.any(AbortSignal),
    )
  })

  it('creates a course and displays the saved result', async () => {
    const user = await renderAuthenticatedPage()

    vi.mocked(coursesApi.create).mockResolvedValue({
      id: 2,
      nombre: 'Bases de datos',
      codigo: 'BD-01',
      profesorId: 10,
    })

    await user.type(
      screen.getByLabelText('Nombre del curso'),
      'Bases de datos',
    )

    await user.type(
      screen.getByLabelText('Código del curso'),
      'BD-01',
    )

    await user.click(screen.getByRole('button', { name: 'Crear curso' }))

    await screen.findByText('Curso creado correctamente.')

    expect(coursesApi.create).toHaveBeenCalledWith(
      { nombre: 'Bases de datos', codigo: 'BD-01' },
      testToken,
    )

    expect(
      screen.getByRole('heading', { name: 'Bases de datos' }),
    ).toBeTruthy()
  })

  it('updates the selected course', async () => {
    const user = await renderAuthenticatedPage()

    vi.mocked(coursesApi.update).mockResolvedValue({
      ...course,
      nombre: 'Fullstack avanzado',
      codigo: 'FS-02',
    })

    await user.click(
      screen.getByRole('button', { name: 'Editar Fullstack' }),
    )

    const nameInput = screen.getByLabelText('Nombre del curso')
    const codeInput = screen.getByLabelText('Código del curso')

    await user.clear(nameInput)
    await user.type(nameInput, 'Fullstack avanzado')
    await user.clear(codeInput)
    await user.type(codeInput, 'FS-02')

    await user.click(
      screen.getByRole('button', { name: 'Guardar cambios' }),
    )

    await screen.findByText('Curso actualizado correctamente.')

    expect(coursesApi.update).toHaveBeenCalledWith(
      1,
      { nombre: 'Fullstack avanzado', codigo: 'FS-02' },
      testToken,
    )

    expect(
      screen.getByRole('heading', { name: 'Fullstack avanzado' }),
    ).toBeTruthy()
  })

  it('retrieves course details from the API', async () => {
    const user = await renderAuthenticatedPage()

    vi.mocked(coursesApi.find).mockResolvedValue({ ...course })

    await user.click(
      screen.getByRole('button', { name: 'Ver detalles de Fullstack' }),
    )

    await screen.findByRole('heading', { name: 'Detalle del curso' })

    expect(coursesApi.find).toHaveBeenCalledWith(1, testToken)
    expect(screen.getByText('Identificador del docente')).toBeTruthy()
  })

  it('does not delete when confirmation is cancelled', async () => {
    const user = await renderAuthenticatedPage()

    vi.spyOn(window, 'confirm').mockReturnValue(false)

    await user.click(
      screen.getByRole('button', { name: 'Eliminar Fullstack' }),
    )

    expect(coursesApi.remove).not.toHaveBeenCalled()
    expect(
      screen.getByRole('heading', { name: 'Fullstack' }),
    ).toBeTruthy()
  })

  it('deletes a course after confirmation', async () => {
    const user = await renderAuthenticatedPage()

    vi.spyOn(window, 'confirm').mockReturnValue(true)
    vi.mocked(coursesApi.remove).mockResolvedValue(undefined)

    await user.click(
      screen.getByRole('button', { name: 'Eliminar Fullstack' }),
    )

    await screen.findByText('Curso eliminado correctamente.')

    expect(coursesApi.remove).toHaveBeenCalledWith(1, testToken)

    expect(
      screen.queryByRole('heading', { name: 'Fullstack' }),
    ).toBeNull()

    expect(
      screen.getByText('Todavía no tienes cursos registrados.'),
    ).toBeTruthy()
  })

  it('preserves the course and displays a deletion error', async () => {
    const user = await renderAuthenticatedPage()

    vi.spyOn(window, 'confirm').mockReturnValue(true)

    vi.mocked(coursesApi.remove).mockRejectedValue(
      new Error('El curso tiene registros relacionados.'),
    )

    await user.click(
      screen.getByRole('button', { name: 'Eliminar Fullstack' }),
    )

    await waitFor(() => {
      expect(screen.getByRole('alert').textContent).toContain(
        'El curso tiene registros relacionados.',
      )
    })

    expect(
      screen.getByRole('heading', { name: 'Fullstack' }),
    ).toBeTruthy()
  })
})