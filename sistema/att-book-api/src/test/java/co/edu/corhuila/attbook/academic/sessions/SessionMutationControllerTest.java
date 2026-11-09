package co.edu.corhuila.attbook.academic.sessions;

import java.time.LocalDateTime;

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

class SessionMutationControllerTest {

    private SessionService service;
    private MockMvc mvc;

    private final SessionActor teacherActor =
            new SessionActor(10L, "TEACHER");

    private final CoursePrincipal teacherPrincipal =
            new TestPrincipal(10L, "TEACHER");

    private static final String VALID_JSON = """
            {
              "courseId": 1,
              "title": "Updated session",
              "description": "Updated description",
              "startAt": "2026-11-10T08:00:00",
              "endAt": "2026-11-10T10:00:00"
            }
            """;

    @BeforeEach
    void setUp() {
        service = mock(SessionService.class);

        mvc = MockMvcBuilders
                .standaloneSetup(new SessionController(service))
                .build();
    }

    @Test
    void updatesSessionAndReturnsResponse() throws Exception {
        LocalDateTime start =
                LocalDateTime.of(2026, 11, 10, 8, 0);

        SessionRequest request = new SessionRequest(
                1L,
                "Updated session",
                "Updated description",
                start,
                start.plusHours(2)
        );

        when(service.update(5L, request, teacherActor))
                .thenReturn(new SessionResponse(
                        5L,
                        1L,
                        request.title(),
                        request.description(),
                        request.startAt(),
                        request.endAt()
                ));

        mvc.perform(put("/sessions/5")
                        .principal(teacherPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.courseId").value(1))
                .andExpect(jsonPath("$.title").value("Updated session"));

        verify(service).update(5L, request, teacherActor);
    }

    @Test
    void deletesSessionAndReturnsNoContent() throws Exception {
        mvc.perform(delete("/sessions/5")
                        .principal(teacherPrincipal))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(service).delete(5L, teacherActor);
    }

    @Test
    void rejectsUpdateWithoutAuthentication() throws Exception {
        mvc.perform(put("/sessions/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(service);
    }

    @Test
    void rejectsDeleteWithoutAuthentication() throws Exception {
        mvc.perform(delete("/sessions/5"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(service);
    }

    @Test
    void rejectsUnverifiedPrincipal() throws Exception {
        mvc.perform(delete("/sessions/5")
                        .principal(() -> "unverified-user"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(service);
    }

    @Test
    void rejectsUpdateWithBlankTitle() throws Exception {
        mvc.perform(put("/sessions/5")
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
    void rejectsUpdateWithMissingStartTime() throws Exception {
        mvc.perform(put("/sessions/5")
                        .principal(teacherPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "courseId": 1,
                                  "title": "Updated session",
                                  "endAt": "2026-11-10T10:00:00"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    void returnsForbiddenWhenUpdateIsDenied() throws Exception {
        when(service.update(
                eq(5L),
                any(SessionRequest.class),
                eq(teacherActor)
        )).thenThrow(new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Access denied"
        ));

        mvc.perform(put("/sessions/5")
                        .principal(teacherPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void returnsForbiddenWhenDeleteIsDenied() throws Exception {
        doThrow(new ResponseStatusException(
                HttpStatus.FORBIDDEN,
                "Access denied"
        )).when(service).delete(5L, teacherActor);

        mvc.perform(delete("/sessions/5")
                        .principal(teacherPrincipal))
                .andExpect(status().isForbidden());
    }

    @Test
    void returnsNotFoundForUnknownSession() throws Exception {
        doThrow(new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Session not found"
        )).when(service).delete(999L, teacherActor);

        mvc.perform(delete("/sessions/999")
                        .principal(teacherPrincipal))
                .andExpect(status().isNotFound());
    }

    @Test
    void returnsConflictWhenDeletionIsBlocked() throws Exception {
        doThrow(new ResponseStatusException(
                HttpStatus.CONFLICT,
                "The session has related records"
        )).when(service).delete(5L, teacherActor);

        mvc.perform(delete("/sessions/5")
                        .principal(teacherPrincipal))
                .andExpect(status().isConflict());
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
