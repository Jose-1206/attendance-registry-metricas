import { useState } from 'react'
import CoursesPage from '../courses/CoursesPage'
import TeacherSessionsPage from '../sessions/TeacherSessionsPage'
import './TeacherDashboard.css'

interface Props { accessToken: string }
type Tab = 'courses' | 'sessions'

export default function TeacherDashboard({ accessToken }: Props) {
  const [tab, setTab] = useState<Tab>('courses')
  return (
    <div>
      <nav className="teacher-nav" aria-label="Navegación docente">
        <button type="button" aria-current={tab === 'courses' ? 'page' : undefined}
          onClick={() => setTab('courses')}>Mis cursos</button>
        <button type="button" aria-current={tab === 'sessions' ? 'page' : undefined}
          onClick={() => setTab('sessions')}>Sesiones y asistencia</button>
      </nav>
      {tab === 'courses'
        ? <CoursesPage accessToken={accessToken} />
        : <TeacherSessionsPage accessToken={accessToken} />}
    </div>
  )
}
