
package co.edu.corhuila.attbook.academic.sessions;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import co.edu.corhuila.attbook.academic.courses.CoursePrincipal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SessionControllerTest {

    private SessionService service;
    private MockMvc mvc;

    private final SessionActor teacherActor =
            new SessionActor(10L, "TEACHER");

    private final SessionActor studentActor =
            new SessionActor(20L, "STUDENT");

    private final CoursePrincipal teacherPrincipal =
            new TestPrincipal(10L, "TEACHER");

    private final CoursePrincipal studentPrincipal =
            new TestPrincipal(20L, "STUDENT");

    @BeforeEach
    void setUp() {
        service = mock(SessionService.class);

        mvc = MockMvcBuilders
                .standaloneSetup(new SessionController(service))
                .build();
    }

    @Test
    void rejectsRequestWithoutAuthentication() throws Exception {
        mvc.perform(get("/sessions/courses/1"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(service);
    }

    @Test
    void rejectsUnverifiedPrincipal() throws Exception {
        mvc.perform(get("/sessions/courses/1")
                        .principal(() -> "unverified-user"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(service);
    }

    @Test
    void createsSessionAndReturnsLocation() throws Exception {
        LocalDateTime start =
                LocalDateTime.of(2026, 11, 10, 8, 0);

        SessionRequest request = new SessionRequest(
                1L,
                "Mobile Programming",
                "Practical class",
                start,
                start.plusHours(2)
        );

        when(service.create(request, teacherActor)).thenReturn(
                new SessionResponse(
                        5L,
                        1L,
                        "Mobile Programming",
                        "Practical class",
                        start,
                        start.plusHours(2)
                )
        );

        mvc.perform(post("/sessions")
                        .principal(teacherPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "courseId": 1,
                                  "title": "Mobile Programming",
                                  "description": "Practical class",
                                  "startAt": "2026-11-10T08:00:00",
                                  "endAt": "2026-11-10T10:00:00"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(header().string(
                        "Location",
                        "http://localhost/sessions/5"
                ))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.title")
                        .value("Mobile Programming"))
                .andExpect(jsonPath("$.courseId").value(1));

        verify(service).create(request, teacherActor);
    }

    @Test
    void rejectsBlankSessionTitle() throws Exception {
        mvc.perform(post("/sessions")
                        .principal(teacherPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "courseId": 1,
                                  "title": "   ",
                                  "startAt": "2026-11-10T08:00:00",
                                  "endAt": "2026-11-10T10:00:00"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void rejectsMissingStartTime() throws Exception {
        mvc.perform(post("/sessions")
                        .principal(teacherPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "courseId": 1,
                                  "title": "Mobile Programming",
                                  "endAt": "2026-11-10T10:00:00"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void listsSessionsForEnrolledStudent() throws Exception {
        LocalDateTime start =
                LocalDateTime.of(2026, 11, 10, 8, 0);

        when(service.findByCourse(1L, studentActor))
                .thenReturn(List.of(
                        new SessionResponse(
                                5L,
                                1L,
                                "Mobile Programming",
                                null,
                                start,
                                start.plusHours(2)
                        )
                ));

        mvc.perform(get("/sessions/courses/1")
                        .principal(studentPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5))
                .andExpect(jsonPath("$[0].title")
                        .value("Mobile Programming"));

        verify(service).findByCourse(1L, studentActor);
    }

    @Test
    void returnsForbiddenForUnenrolledStudent() throws Exception {
        when(service.findByCourse(1L, studentActor))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Student is not enrolled in this course"
                ));

        mvc.perform(get("/sessions/courses/1")
                        .principal(studentPrincipal))
                .andExpect(status().isForbidden());
    }

    @Test
    void returnsNotFoundForUnavailableCourse() throws Exception {
        when(service.findByCourse(99L, teacherActor))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Course not found"
                ));

        mvc.perform(get("/sessions/courses/99")
                        .principal(teacherPrincipal))
                .andExpect(status().isNotFound());
    }

    @Test
    void teacherCanGetSessionById() throws Exception {
        LocalDateTime start =
                LocalDateTime.of(2026, 11, 10, 8, 0);

        when(service.findById(5L, teacherActor))
                .thenReturn(new SessionResponse(
                        5L,
                        1L,
                        "Mobile Programming",
                        "Practical class",
                        start,
                        start.plusHours(2)
                ));

        mvc.perform(get("/sessions/5")
                        .principal(teacherPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.courseId").value(1))
                .andExpect(jsonPath("$.title")
                        .value("Mobile Programming"));

        verify(service).findById(5L, teacherActor);
    }

    @Test
    void studentCanGetAccessibleSessionById() throws Exception {
        LocalDateTime start =
                LocalDateTime.of(2026, 11, 10, 8, 0);

        when(service.findById(5L, studentActor))
                .thenReturn(new SessionResponse(
                        5L,
                        1L,
                        "Mobile Programming",
                        null,
                        start,
                        start.plusHours(2)
                ));

        mvc.perform(get("/sessions/5")
                        .principal(studentPrincipal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));

        verify(service).findById(5L, studentActor);
    }

    @Test
    void rejectsSessionLookupWithoutAuthentication() throws Exception {
        mvc.perform(get("/sessions/5"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(service);
    }

    @Test
    void returnsNotFoundForUnknownSession() throws Exception {
        when(service.findById(999L, teacherActor))
                .thenThrow(new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Session not found"
                ));

        mvc.perform(get("/sessions/999")
                        .principal(teacherPrincipal))
                .andExpect(status().isNotFound());

        verify(service).findById(999L, teacherActor);
    }

    private static class TestPrincipal implements CoursePrincipal {

        private final Long userId;
        private final String role;

        TestPrincipal(Long userId, String role) {
            this.userId = userId;
            this.role = role;
        }

        @Override
        public Long getUserId() {
            return userId;
        }

        @Override
        public String getRole() {
            return role;
        }

        @Override
        public String getName() {
            return "test-user@example.test";
        }
    }
}
