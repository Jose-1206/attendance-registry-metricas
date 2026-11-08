package co.edu.corhuila.attbook.academic.courses;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository repository;

    private CourseService service;

    private final CourseActor teacher = new CourseActor(10L, "TEACHER");

    @BeforeEach
    void setUp() {
        service = new CourseService(repository);
    }

    @Test
    void rejectsMissingAuthentication() {
        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.list(null)
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verifyNoInteractions(repository);
    }

    @Test
    void rejectsStudentCreatingCourse() {
        CourseActor student = new CourseActor(20L, "STUDENT");
        CourseRequest request = new CourseRequest("Fullstack", "FS-01");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.create(request, student)
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verifyNoInteractions(repository);
    }

    @Test
    void listsOnlyCoursesOwnedByTeacher() {
        Course course = new Course("Fullstack", "FS-01", 10L);

        when(repository.findAllByTeacherIdOrderByIdAsc(10L))
                .thenReturn(List.of(course));

        List<CourseResponse> result = service.list(teacher);

        assertEquals(1, result.size());
        assertEquals("Fullstack", result.get(0).nombre());
        assertEquals(10L, result.get(0).profesorId());
        verify(repository).findAllByTeacherIdOrderByIdAsc(10L);
    }

    @Test
    void createsCourseWithAuthenticatedTeacherAsOwner() {
        CourseRequest request = new CourseRequest(
                "  Fullstack  ",
                "  FS-01  "
        );

        when(repository.save(any(Course.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        CourseResponse response = service.create(request, teacher);

        ArgumentCaptor<Course> captor =
                ArgumentCaptor.forClass(Course.class);

        verify(repository).save(captor.capture());

        Course savedCourse = captor.getValue();

        assertEquals("Fullstack", savedCourse.getName());
        assertEquals("FS-01", savedCourse.getCode());
        assertEquals(10L, savedCourse.getTeacherId());
        assertEquals(10L, response.profesorId());
    }

    @Test
    void returnsNotFoundWhenCourseIsNotVisibleToTeacher() {
        when(repository.findByIdAndTeacherId(99L, 10L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.findById(99L, teacher)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(repository).findByIdAndTeacherId(99L, 10L);
    }

    @Test
    void updatesOwnedCourseWithoutChangingOwner() {
        Course course = new Course("Old name", "OLD", 10L);

        when(repository.findByIdAndTeacherId(1L, 10L))
                .thenReturn(Optional.of(course));

        when(repository.save(course)).thenReturn(course);

        CourseResponse response = service.update(
                1L,
                new CourseRequest("New name", "NEW"),
                teacher
        );

        assertEquals("New name", response.nombre());
        assertEquals("NEW", response.codigo());
        assertEquals(10L, response.profesorId());
        verify(repository).save(course);
    }

    @Test
    void doesNotUpdateCourseOutsideTeacherOwnership() {
        when(repository.findByIdAndTeacherId(2L, 10L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.update(
                        2L,
                        new CourseRequest("Changed", "CH-01"),
                        teacher
                )
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(repository, never()).save(any(Course.class));
    }

    @Test
    void deletesOwnedCourseAndFlushesConstraints() {
        Course course = new Course("Fullstack", "FS-01", 10L);

        when(repository.findByIdAndTeacherId(1L, 10L))
                .thenReturn(Optional.of(course));

        service.delete(1L, teacher);

        verify(repository).delete(course);
        verify(repository).flush();
    }

    @Test
    void doesNotDeleteCourseOutsideTeacherOwnership() {
        when(repository.findByIdAndTeacherId(2L, 10L))
                .thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> service.delete(2L, teacher)
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(repository, never()).delete(any(Course.class));
        verify(repository, never()).flush();
    }
}
