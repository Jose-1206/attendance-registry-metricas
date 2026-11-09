package co.edu.corhuila.attbook.academic.enrollments;

import co.edu.corhuila.attbook.academic.courses.CoursePrincipal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class EnrollmentControllerTest {

    private EnrollmentService service;
    private MockMvc mvc;

    private final EnrollmentActor studentActor =
            new EnrollmentActor(1L, "STUDENT");

    private final CoursePrincipal studentPrincipal = new CoursePrincipal() {

        @Override
        public Long getUserId() {
            return 1L;
        }

        @Override
        public String getRole() {
            return "STUDENT";
        }

        @Override
        public String getName() {
            return "student@example.test";
        }
    };

    @BeforeEach
    void setUp() {
        service = mock(EnrollmentService.class);

        mvc = MockMvcBuilders
                .standaloneSetup(new EnrollmentController(service))
                .build();
    }

    @Test
    void rejectsEnrollmentListWithoutAuthentication() throws Exception {
        mvc.perform(get("/enrollments/me"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(service);
    }

    @Test
    void rejectsUnverifiedPrincipal() throws Exception {
        mvc.perform(get("/enrollments/me")
                        .principal(() -> "unverified-user"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(service);
    }

    @Test
    void returnsStudentEnrollments() throws Exception {
        when(service.listMine(studentActor))
                .thenReturn(List.of(new Enrollment(1L, 10L)));

        mvc.perform(get("/enrollments/me")
                        .principal(studentPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentId").value(1))
                .andExpect(jsonPath("$[0].courseId").value(10));

        verify(service).listMine(studentActor);
    }

    @Test
    void createsEnrollment() throws Exception {
        when(service.enroll(10L, studentActor))
                .thenReturn(new Enrollment(1L, 10L));

        mvc.perform(post("/enrollments/courses/10")
                        .principal(studentPrincipal))
                .andExpect(status().isCreated())
                .andExpect(header().doesNotExist("Location"))
                .andExpect(jsonPath("$.studentId").value(1))
                .andExpect(jsonPath("$.courseId").value(10));

        verify(service).enroll(10L, studentActor);
    }

    @Test
    void rejectsEnrollmentWithoutAuthentication() throws Exception {
        mvc.perform(post("/enrollments/courses/10"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(service);
    }

    @Test
    void returnsConflictForDuplicateEnrollment() throws Exception {
        when(service.enroll(10L, studentActor))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Student is already enrolled"
                ));

        mvc.perform(post("/enrollments/courses/10")
                        .principal(studentPrincipal))
                .andExpect(status().isConflict());
    }

    @Test
    void returnsNotFoundForMissingCourse() throws Exception {
        when(service.enroll(99L, studentActor))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Course not found"
                ));

        mvc.perform(post("/enrollments/courses/99")
                        .principal(studentPrincipal))
                .andExpect(status().isNotFound());
    }
}