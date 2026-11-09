
package co.edu.corhuila.attbook.academic.sessions;

import java.time.LocalDateTime;
import java.util.List;

import co.edu.corhuila.attbook.identity.security.JwtConfiguration;
import co.edu.corhuila.attbook.identity.security.JwtService;
import co.edu.corhuila.attbook.identity.security.SecurityConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringJUnitWebConfig(SessionHttpSecurityTest.TestConfiguration.class)
@TestPropertySource(properties = {
        "app.jwt.secret=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
        "app.jwt.issuer=att-book-api",
        "app.jwt.ttl-seconds=3600"
})
class SessionHttpSecurityTest {

    @Configuration(proxyBeanMethods = false)
    @EnableWebMvc
    @EnableWebSecurity
    @Import({
            SecurityConfig.class,
            JwtConfiguration.class,
            JwtService.class,
            SessionController.class
    })
    static class TestConfiguration {

        @Bean
        SessionService sessionService() {
            return mock(SessionService.class);
        }
    }

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private SessionService sessionService;

    @Autowired
    private JwtService jwtService;

    private MockMvc mvc;

    @BeforeEach
    void setUp() {
        reset(sessionService);

        mvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void rejectsSessionCreationWithoutToken() throws Exception {
        mvc.perform(post("/api/v1/sessions")
                        .contextPath("/api/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validSessionJson()))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(sessionService);
    }

    @Test
    void rejectsSessionCreationWithStudentToken() throws Exception {
        mvc.perform(post("/api/v1/sessions")
                        .contextPath("/api/v1")
                        .header("Authorization", "Bearer " + studentToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validSessionJson()))
                .andExpect(status().isForbidden());

        verifyNoInteractions(sessionService);
    }

    @Test
    void allowsTeacherToCreateSession() throws Exception {
        LocalDateTime start =
                LocalDateTime.of(2026, 11, 10, 8, 0);

        SessionRequest request = new SessionRequest(
                1L,
                "Mobile Programming",
                "Practical class",
                start,
                start.plusHours(2)
        );

        SessionActor actor = new SessionActor(10L, "TEACHER");

        when(sessionService.create(request, actor))
                .thenReturn(new SessionResponse(
                        5L,
                        1L,
                        "Mobile Programming",
                        "Practical class",
                        start,
                        start.plusHours(2)
                ));

        mvc.perform(post("/api/v1/sessions")
                        .contextPath("/api/v1")
                        .header("Authorization", "Bearer " + teacherToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validSessionJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));

        verify(sessionService).create(request, actor);
    }

    @Test
    void rejectsSessionListingWithoutToken() throws Exception {
        mvc.perform(get("/api/v1/sessions/courses/1")
                        .contextPath("/api/v1"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(sessionService);
    }

    @Test
    void allowsStudentToListCourseSessions() throws Exception {
        SessionActor actor = new SessionActor(20L, "STUDENT");

        when(sessionService.findByCourse(1L, actor))
                .thenReturn(List.of());

        mvc.perform(get("/api/v1/sessions/courses/1")
                        .contextPath("/api/v1")
                        .header("Authorization", "Bearer " + studentToken()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(sessionService).findByCourse(1L, actor);
    }

    @Test
    void rejectsMalformedToken() throws Exception {
        mvc.perform(get("/api/v1/sessions/courses/1")
                        .contextPath("/api/v1")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(sessionService);
    }

    @Test
    void rejectsSessionLookupWithoutToken() throws Exception {
        mvc.perform(get("/api/v1/sessions/5")
                        .contextPath("/api/v1"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(sessionService);
    }

    @Test
    void allowsTeacherToGetSessionWithValidToken() throws Exception {
        SessionActor actor = new SessionActor(10L, "TEACHER");

        when(sessionService.findById(5L, actor))
                .thenReturn(sessionResponse());

        mvc.perform(get("/api/v1/sessions/5")
                        .contextPath("/api/v1")
                        .header("Authorization", "Bearer " + teacherToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.courseId").value(1));

        verify(sessionService).findById(5L, actor);
    }

    @Test
    void allowsStudentToGetSessionWithValidToken() throws Exception {
        SessionActor actor = new SessionActor(20L, "STUDENT");

        when(sessionService.findById(5L, actor))
                .thenReturn(sessionResponse());

        mvc.perform(get("/api/v1/sessions/5")
                        .contextPath("/api/v1")
                        .header("Authorization", "Bearer " + studentToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5));

        verify(sessionService).findById(5L, actor);
    }

    private SessionResponse sessionResponse() {
        LocalDateTime start =
                LocalDateTime.of(2026, 11, 10, 8, 0);

        return new SessionResponse(
                5L,
                1L,
                "Mobile Programming",
                "Practical class",
                start,
                start.plusHours(2)
        );
    }

    private String validSessionJson() {
        return """
                {
                  "courseId": 1,
                  "title": "Mobile Programming",
                  "description": "Practical class",
                  "startAt": "2026-11-10T08:00:00",
                  "endAt": "2026-11-10T10:00:00"
                }
                """;
    }

    private String teacherToken() {
        return jwtService.issueToken(
                10L,
                "teacher@example.test",
                "TEACHER"
        );
    }

    private String studentToken() {
        return jwtService.issueToken(
                20L,
                "student@example.test",
                "STUDENT"
        );
    }
}
