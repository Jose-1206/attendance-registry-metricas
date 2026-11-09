package co.edu.corhuila.attbook.academic.sessions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import co.edu.corhuila.attbook.academic.courses.Course;
import co.edu.corhuila.attbook.academic.courses.CourseRepository;
import co.edu.corhuila.attbook.academic.enrollments.EnrollmentRepository;

@ExtendWith(MockitoExtension.class)
class SessionMutationServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @InjectMocks
    private SessionService service;

    private SessionActor teacher;
    private SessionActor student;
    private SessionRequest request;
    private ClassSession session;

    @BeforeEach
    void setUp() {
        LocalDateTime start = LocalDateTime.of(2026, 11, 10, 8, 0);

        teacher = new SessionActor(10L, "TEACHER");
        student = new SessionActor(20L, "STUDENT");

        request = new SessionRequest(
                1L,
                "Updated session",
                "Updated description",
                start,
                start.plusHours(2)
        );

        session = new ClassSession(
                1L,
                "Original session",
                "Original description",
                start,
                start.plusHours(1)
        );

        ReflectionTestUtils.setField(session, "id", 5L);
    }

    @Test
    void ownerCanUpdateSession() {
        mockOwnership(10L);

        when(sessionRepository.save(any(ClassSession.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SessionResponse result = service.update(5L, request, teacher);

        assertEquals(5L, result.id());
        assertEquals(1L, result.courseId());
        assertEquals("Updated session", result.title());
        assertEquals(request.description(), session.getDescription());
        assertEquals(request.startAt(), session.getStartAt());
        assertEquals(request.endAt(), session.getEndAt());

        verify(sessionRepository).save(session);
    }

    @Test
    void studentCannotUpdateSession() {
        assertStatus(
                HttpStatus.FORBIDDEN,
                () -> service.update(5L, request, student)
        );

        verifyNoInteractions(sessionRepository, courseRepository);
    }

    @Test
    void anotherTeacherCannotUpdateSession() {
        mockOwnership(99L);

        assertStatus(
                HttpStatus.FORBIDDEN,
                () -> service.update(5L, request, teacher)
        );

        verify(sessionRepository, never()).save(any());
        assertEquals("Original session", session.getTitle());
    }

    @Test
    void sessionCannotBeMovedToAnotherCourse() {
        mockOwnership(10L);

        SessionRequest otherCourse = new SessionRequest(
                2L,
                request.title(),
                request.description(),
                request.startAt(),
                request.endAt()
        );

        assertStatus(
                HttpStatus.BAD_REQUEST,
                () -> service.update(5L, otherCourse, teacher)
        );

        verify(sessionRepository, never()).save(any());
        assertEquals(1L, session.getCourseId());
    }

    @Test
    void updateRejectsInvalidTimeRange() {
        mockOwnership(10L);

        SessionRequest invalid = new SessionRequest(
                1L,
                request.title(),
                request.description(),
                request.startAt(),
                request.startAt()
        );

        assertStatus(
                HttpStatus.BAD_REQUEST,
                () -> service.update(5L, invalid, teacher)
        );

        verify(sessionRepository, never()).save(any());
    }

    @Test
    void updatingUnknownSessionReturnsNotFound() {
        when(sessionRepository.findById(5L))
                .thenReturn(Optional.empty());

        assertStatus(
                HttpStatus.NOT_FOUND,
                () -> service.update(5L, request, teacher)
        );

        verify(sessionRepository, never()).save(any());
    }

    @Test
    void ownerCanDeleteSession() {
        mockOwnership(10L);

        service.delete(5L, teacher);

        verify(sessionRepository).delete(session);
        verify(sessionRepository).flush();
    }

    @Test
    void studentCannotDeleteSession() {
        assertStatus(
                HttpStatus.FORBIDDEN,
                () -> service.delete(5L, student)
        );

        verifyNoInteractions(sessionRepository, courseRepository);
    }

    @Test
    void anotherTeacherCannotDeleteSession() {
        mockOwnership(99L);

        assertStatus(
                HttpStatus.FORBIDDEN,
                () -> service.delete(5L, teacher)
        );

        verify(sessionRepository, never()).delete(any(ClassSession.class));
        verify(sessionRepository, never()).flush();
    }

    @Test
    void deletingUnknownSessionReturnsNotFound() {
        when(sessionRepository.findById(5L))
                .thenReturn(Optional.empty());

        assertStatus(
                HttpStatus.NOT_FOUND,
                () -> service.delete(5L, teacher)
        );

        verify(sessionRepository, never()).delete(any(ClassSession.class));
    }

    @Test
    void integrityConflictReturnsConflict() {
        mockOwnership(10L);

        doThrow(new DataIntegrityViolationException("Related records"))
                .when(sessionRepository).flush();

        assertStatus(
                HttpStatus.CONFLICT,
                () -> service.delete(5L, teacher)
        );
    }

    @Test
    void unauthenticatedUserCannotModifySessions() {
        assertStatus(
                HttpStatus.UNAUTHORIZED,
                () -> service.update(5L, request, null)
        );

        assertStatus(
                HttpStatus.UNAUTHORIZED,
                () -> service.delete(5L, null)
        );

        verifyNoInteractions(sessionRepository, courseRepository);
    }

    @Test
    void invalidSessionIdIsRejected() {
        assertStatus(
                HttpStatus.BAD_REQUEST,
                () -> service.update(0L, request, teacher)
        );

        assertStatus(
                HttpStatus.BAD_REQUEST,
                () -> service.delete(-1L, teacher)
        );

        verifyNoInteractions(sessionRepository, courseRepository);
    }

    private void mockOwnership(Long ownerId) {
        when(sessionRepository.findById(5L))
                .thenReturn(Optional.of(session));

        when(courseRepository.findById(1L))
                .thenReturn(Optional.of(
                        new Course("Programming", "PROG101", ownerId)
                ));
    }

    private void assertStatus(
            HttpStatus expected,
            Runnable action
    ) {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                action::run
        );

        assertEquals(expected, exception.getStatusCode());
    }
}