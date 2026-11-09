package co.edu.corhuila.attbook.academic.enrollments;

import co.edu.corhuila.attbook.academic.courses.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EnrollmentServiceTest {

    private EnrollmentRepository enrollmentRepository;
    private CourseRepository courseRepository;
    private EnrollmentService service;

    private final EnrollmentActor student =
            new EnrollmentActor(1L, "STUDENT");

    @BeforeEach
    void setUp() {
        enrollmentRepository = mock(EnrollmentRepository.class);
        courseRepository = mock(CourseRepository.class);

        service = new EnrollmentService(
                enrollmentRepository,
                courseRepository
        );
    }

    @Test
    void shouldEnrollStudentInExistingCourse() {
        when(courseRepository.existsById(10L)).thenReturn(true);
        when(enrollmentRepository.saveAndFlush(any(Enrollment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Enrollment result = service.enroll(10L, student);

        assertEquals(1L, result.getStudentId());
        assertEquals(10L, result.getCourseId());
        assertNotNull(result.getEnrollmentDate());

        verify(enrollmentRepository).saveAndFlush(any(Enrollment.class));
    }

    @Test
    void shouldRejectDuplicateEnrollment() {
        when(courseRepository.existsById(10L)).thenReturn(true);
        when(enrollmentRepository.existsByStudentIdAndCourseId(1L, 10L))
                .thenReturn(true);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.enroll(10L, student)
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(enrollmentRepository, never()).saveAndFlush(any());
    }

    @Test
    void shouldRejectNonexistentCourse() {
        when(courseRepository.existsById(99L)).thenReturn(false);

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.enroll(99L, student)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void shouldRejectTeacherEnrollment() {
        EnrollmentActor teacher = new EnrollmentActor(2L, "TEACHER");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.enroll(10L, teacher)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verifyNoInteractions(courseRepository, enrollmentRepository);
    }

    @Test
    void shouldRejectUnauthenticatedEnrollment() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.enroll(10L, null)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
    }

    @Test
    void shouldRejectInvalidCourseId() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.enroll(0L, student)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
    }

    @Test
    void shouldListOnlyAuthenticatedStudentEnrollments() {
        Enrollment enrollment = new Enrollment(1L, 10L);

        when(enrollmentRepository.findByStudentId(1L))
                .thenReturn(List.of(enrollment));

        List<Enrollment> result = service.listMine(student);

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getStudentId());

        verify(enrollmentRepository).findByStudentId(1L);
        verify(enrollmentRepository, never()).findByStudentId(2L);
    }

    @Test
    void shouldRejectTeacherEnrollmentHistoryAccess() {
        EnrollmentActor teacher = new EnrollmentActor(2L, "TEACHER");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.listMine(teacher)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }
}
