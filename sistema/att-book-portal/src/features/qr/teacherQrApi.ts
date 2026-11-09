import { teacherRequest } from '../sessions/sessionsApi'

export interface QrResponse {
  qr: string
  expiresAt: string
}

// Contract from docs/07-api/contracts/openapi/qr-api.yaml.
// Confirm the implementation with the backend developer before integration tests.
export const teacherQrApi = {
  generate(sessionId: number, token: string) {
    return teacherRequest<QrResponse>(`/clases/${sessionId}/qr`, token, {
      method: 'POST',
    })
  },
}
