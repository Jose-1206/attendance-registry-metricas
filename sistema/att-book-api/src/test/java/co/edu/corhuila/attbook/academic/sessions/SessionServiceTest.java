
package co.edu.corhuila.attbook.academic.sessions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import co.edu.corhuila.attbook.academic.courses.Course;
import co.edu.corhuila.attbook.academic.courses.CourseRepository;
import co.edu.corhuila.attbook.academic.enrollments.EnrollmentRepository;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private SessionService service;

    private SessionRequest validRequest;
    private SessionActor teacher;
    private SessionActor student;

    @BeforeEach
    void setUp() {
        LocalDateTime start = LocalDateTime.of(2026, 11, 10, 8, 0);

        validRequest = new SessionRequest(
                1L,
                "Mobile Programming",
                "Practical class",
                start,
                start.plusHours(2)
        );

        teacher = new SessionActor(10L, "TEACHER");
        student = new SessionActor(20L, "STUDENT");
    }

    @Test
    void teacherCanCreateSessionForOwnCourse() {
        Course course = new Course("Programming", "PROG101", 10L);

        when(courseRepository.findByIdAndTeacherId(1L, 10L))
                .thenReturn(Optional.of(course));

        when(sessionRepository.save(any(ClassSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SessionResponse response = service.create(validRequest, teacher);

        assertEquals("Mobile Programming", response.title());
        assertEquals(1L, response.courseId());

        verify(sessionRepository).save(any(ClassSession.class));
    }

    @Test
    void studentCannotCreateSession() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.create(validRequest, student)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verifyNoInteractions(sessionRepository);
    }

    @Test
    void teacherCannotCreateSessionForAnotherTeachersCourse() {
        when(courseRepository.findByIdAndTeacherId(1L, 10L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.create(validRequest, teacher)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void sessionEndTimeMustBeAfterStartTime() {
        LocalDateTime start = LocalDateTime.of(2026, 11, 10, 8, 0);

        SessionRequest invalidRequest = new SessionRequest(
                1L,
                "Invalid session",
                null,
                start,
                start.minusHours(1)
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.create(invalidRequest, teacher)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(sessionRepository);
    }

    @Test
    void blankSessionTitleIsRejected() {
        SessionRequest invalidRequest = new SessionRequest(
                1L,
                "   ",
                null,
                validRequest.startAt(),
                validRequest.endAt()
        );

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.create(invalidRequest, teacher)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void enrolledStudentCanListCourseSessions() {
        Course course = new Course("Programming", "PROG101", 10L);

        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(course));

        when(enrollmentRepository.existsByStudentIdAndCourseId(20L, 1L))
                .thenReturn(true);

        ClassSession session = new ClassSession(
                1L,
                "Mobile Programming",
                null,
                validRequest.startAt(),
                validRequest.endAt()
        );

        when(sessionRepository.findByCourseIdOrderByStartAtAsc(1L))
                .thenReturn(List.of(session));

        List<SessionResponse> result = service.findByCourse(1L, student);

        assertEquals(1, result.size());
        assertEquals("Mobile Programming", result.get(0).title());
    }

    @Test
    void unenrolledStudentCannotListCourseSessions() {
        Course course = new Course("Programming", "PROG101", 10L);

        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(course));

        when(enrollmentRepository.existsByStudentIdAndCourseId(20L, 1L))
                .thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.findByCourse(1L, student)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verifyNoInteractions(sessionRepository);
    }

    @Test
    void teacherCannotListAnotherTeachersSessions() {
        Course course = new Course("Programming", "PROG101", 99L);

        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(course));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.findByCourse(1L, teacher)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verifyNoInteractions(sessionRepository);
    }

    @Test
    void unauthenticatedUserCannotListSessions() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.findByCourse(1L, null)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verifyNoInteractions(sessionRepository);
    }

    @Test
    void teacherCanFindSessionFromOwnCourse() {
        ClassSession session = sessionWithId(5L);

        when(sessionRepository.findById(5L))
                .thenReturn(Optional.of(session));

        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(
                        new Course("Programming", "PROG101", 10L)
                ));

        SessionResponse response = service.findById(5L, teacher);

        assertEquals(5L, response.id());
        assertEquals(1L, response.courseId());
        assertEquals("Mobile Programming", response.title());
    }

    @Test
    void enrolledStudentCanFindSession() {
        when(sessionRepository.findById(5L))
                .thenReturn(Optional.of(sessionWithId(5L)));

        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(
                        new Course("Programming", "PROG101", 10L)
                ));

        when(enrollmentRepository.existsByStudentIdAndCourseId(20L, 1L))
                .thenReturn(true);

        SessionResponse response = service.findById(5L, student);

        assertEquals(5L, response.id());
    }

    @Test
    void unenrolledStudentCannotFindSession() {
        when(sessionRepository.findById(5L))
                .thenReturn(Optional.of(sessionWithId(5L)));

        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(
                        new Course("Programming", "PROG101", 10L)
                ));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.findById(5L, student)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void teacherCannotFindAnotherTeachersSession() {
        when(sessionRepository.findById(5L))
                .thenReturn(Optional.of(sessionWithId(5L)));

        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(
                        new Course("Programming", "PROG101", 99L)
                ));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.findById(5L, teacher)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    @Test
    void nonexistentSessionReturnsNotFound() {
        when(sessionRepository.findById(999L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.findById(999L, teacher)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    private ClassSession sessionWithId(Long id) {
        ClassSession session = new ClassSession(
                1L,
                "Mobile Programming",
                "Practical class",
                validRequest.startAt(),
                validRequest.endAt()
        );

        ReflectionTestUtils.setField(session, "id", id);

        return session;
    }
}
