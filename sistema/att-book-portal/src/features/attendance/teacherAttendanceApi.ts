import { teacherRequest } from '../sessions/sessionsApi'

export interface AttendanceRecord {
  id: number
  studentId: number
  claseId: number
  registeredAt: string
}

// HU-11: endpoint currently proposed in the OpenAPI contract.
export const teacherAttendanceApi = {
  listBySession(sessionId: number, token: string, signal?: AbortSignal) {
    return teacherRequest<AttendanceRecord[]>(
      `/clases/${sessionId}/asistencias`, token, { signal },
    )
  },
}
