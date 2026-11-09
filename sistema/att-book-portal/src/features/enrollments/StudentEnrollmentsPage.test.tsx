
 // @vitest-environment jsdom

import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest'
import { cleanup, render, screen, waitFor } from '@testing-library/react'
import userEvent from '@testing-library/user-event'
import StudentEnrollmentsPage from './StudentEnrollmentsPage'
import { enrollmentsApi, EnrollmentsApiError } from './enrollmentsApi'


vi.mock('./enrollmentsApi', async (importOriginal) => {
  const original = await importOriginal<
    typeof import('./enrollmentsApi')
  >()

  return {
    ...original,
    enrollmentsApi: {
      listMine: vi.fn(),
      enroll: vi.fn(),
    },
  }
})

vi.mock('../attendance/StudentAttendanceHistory', () => ({
  default: () => null,
}))


const accessToken = 'student-test-token'

const existingEnrollment = {
  id: 10,
  studentId: 5,
  courseId: 3,
  enrollmentDate: '2026-11-07T10:00:00',
}

describe('Student enrollments', () => {
  beforeEach(() => {
    vi.resetAllMocks()
    vi.mocked(enrollmentsApi.listMine).mockResolvedValue([])
  })

  afterEach(() => {
    cleanup()
  })

  it('loads the student enrollments using the access token', async () => {
    vi.mocked(enrollmentsApi.listMine).mockResolvedValue([
      existingEnrollment,
    ])

    render(<StudentEnrollmentsPage accessToken={accessToken} />)

    expect(
      await screen.findByRole('heading', { name: 'Curso #3' }),
    ).toBeTruthy()

    expect(enrollmentsApi.listMine).toHaveBeenCalledWith(
      accessToken,
      expect.any(AbortSignal),
    )
  })

  it('shows an empty state when there are no enrollments', async () => {
    render(<StudentEnrollmentsPage accessToken={accessToken} />)

    expect(
      await screen.findByText(
        'Todavía no estás inscrito en ningún curso.',
      ),
    ).toBeTruthy()
  })

  it('enrolls the student and displays the new course', async () => {
    const user = userEvent.setup()

    vi.mocked(enrollmentsApi.enroll).mockResolvedValue({
      id: 11,
      studentId: 5,
      courseId: 7,
      enrollmentDate: '2026-11-07T11:00:00',
    })

    render(<StudentEnrollmentsPage accessToken={accessToken} />)

    await screen.findByText(
      'Todavía no estás inscrito en ningún curso.',
    )

    await user.type(screen.getByLabelText('ID del curso'), '7')
    await user.click(screen.getByRole('button', { name: 'Inscribirse' }))

    expect(
      await screen.findByRole('heading', { name: 'Curso #7' }),
    ).toBeTruthy()

    expect(enrollmentsApi.enroll).toHaveBeenCalledWith(
      7,
      accessToken,
    )

    expect(
      screen.getByRole('status').textContent,
    ).toContain('Inscripción realizada correctamente.')
  })

  it('prevents duplicate enrollments', async () => {
    const user = userEvent.setup()

    vi.mocked(enrollmentsApi.listMine).mockResolvedValue([
      existingEnrollment,
    ])

    render(<StudentEnrollmentsPage accessToken={accessToken} />)

    await screen.findByRole('heading', { name: 'Curso #3' })

    await user.type(screen.getByLabelText('ID del curso'), '3')
    await user.click(screen.getByRole('button', { name: 'Inscribirse' }))

    expect(
      await screen.findByRole('alert'),
    ).toHaveProperty(
      'textContent',
      'Ya estás inscrito en este curso.',
    )

    expect(enrollmentsApi.enroll).not.toHaveBeenCalled()
  })

  it('shows an error when loading enrollments fails', async () => {
    vi.mocked(enrollmentsApi.listMine).mockRejectedValue(
      new EnrollmentsApiError(500, 'Unable to load enrollments.'),
    )

    render(<StudentEnrollmentsPage accessToken={accessToken} />)

    expect(
      (await screen.findByRole('alert')).textContent,
    ).toBe('Unable to load enrollments.')

    expect(
      screen.getByText('No se pudieron mostrar las inscripciones.'),
    ).toBeTruthy()
  })

  it('shows an error when enrollment is rejected', async () => {
    const user = userEvent.setup()

    vi.mocked(enrollmentsApi.enroll).mockRejectedValue(
      new EnrollmentsApiError(409, 'Already enrolled.'),
    )

    render(<StudentEnrollmentsPage accessToken={accessToken} />)

    await screen.findByText(
      'Todavía no estás inscrito en ningún curso.',
    )

    await user.type(screen.getByLabelText('ID del curso'), '9')
    await user.click(screen.getByRole('button', { name: 'Inscribirse' }))

    expect(
      (await screen.findByRole('alert')).textContent,
    ).toBe('Already enrolled.')
  })

  it('refreshes the enrollments when requested', async () => {
    const user = userEvent.setup()

    vi.mocked(enrollmentsApi.listMine)
      .mockResolvedValueOnce([])
      .mockResolvedValueOnce([existingEnrollment])

    render(<StudentEnrollmentsPage accessToken={accessToken} />)

    await screen.findByText(
      'Todavía no estás inscrito en ningún curso.',
    )

    await user.click(screen.getByRole('button', { name: 'Actualizar' }))

    expect(
      await screen.findByRole('heading', { name: 'Curso #3' }),
    ).toBeTruthy()

    await waitFor(() => {
      expect(enrollmentsApi.listMine).toHaveBeenCalledTimes(2)
    })
  })
})
