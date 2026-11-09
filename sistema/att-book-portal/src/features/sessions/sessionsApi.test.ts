import { afterEach, describe, expect, it, vi } from 'vitest'
import { sessionsApi, TeacherApiError } from './sessionsApi'

const token = 'fake-test-jwt'
const session = {
  id: 7,
  courseId: 2,
  title: 'Software Engineering',
  description: 'Planning',
  startAt: '2026-11-12T08:00:00',
  endAt: '2026-11-12T10:00:00',
}

const input = {
  courseId: session.courseId,
  title: session.title,
  description: session.description,
  startAt: session.startAt,
  endAt: session.endAt,
}

afterEach(() => vi.unstubAllGlobals())

describe('sessionsApi', () => {
  it('lists course sessions using the actual backend route', async () => {
    const fetchMock = vi.fn().mockResolvedValue(new Response(JSON.stringify([session]), {
      status: 200,
      headers: { 'Content-Type': 'application/json' },
    }))
    vi.stubGlobal('fetch', fetchMock)
    const result = await sessionsApi.listByCourse(2, token)
    expect(result).toEqual([session])
    expect(fetchMock).toHaveBeenCalledWith('/api/v1/sessions/courses/2',
      expect.objectContaining({
        headers: expect.any(Headers),
        cache: 'no-store',
      }),
    )
    const headers = fetchMock.mock.calls[0][1].headers as Headers
    expect(headers.get('Authorization')).toBe(`Bearer ${token}`)
  })

  it('creates a session with the required JSON body', async () => {
    const fetchMock = vi.fn().mockResolvedValue(new Response(JSON.stringify(session), { status: 201 }))
    vi.stubGlobal('fetch', fetchMock)
    expect(await sessionsApi.create(input, token)).toEqual(session)
    expect(fetchMock).toHaveBeenCalledWith('/api/v1/sessions',
      expect.objectContaining({method: 'POST', body: JSON.stringify(input)}),
    )
  })

  it('handles an empty delete response', async () => {
    const fetchMock = vi.fn().mockResolvedValue(new Response(null, { status: 204 }))
    vi.stubGlobal('fetch', fetchMock)
    expect(await sessionsApi.remove(7, token)).toBeUndefined()
    expect(fetchMock).toHaveBeenCalledWith('/api/v1/sessions/7',
      expect.objectContaining({method: 'DELETE'}),
    )
  })

  it('rejects requests without a token', async () => {
    await expect(sessionsApi.listByCourse(2, '')).rejects.toBeInstanceOf(TeacherApiError)
  })
})
