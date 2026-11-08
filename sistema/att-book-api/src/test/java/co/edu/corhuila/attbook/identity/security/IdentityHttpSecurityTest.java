package co.edu.corhuila.attbook.identity.security;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import co.edu.corhuila.attbook.academic.courses.CourseActor;
import co.edu.corhuila.attbook.academic.courses.CourseController;
import co.edu.corhuila.attbook.academic.courses.CourseService;
import co.edu.corhuila.attbook.identity.users.AppUser;
import co.edu.corhuila.attbook.identity.users.AuthController;
import co.edu.corhuila.attbook.identity.users.CurrentUserController;
import co.edu.corhuila.attbook.identity.users.IdentityService;
import co.edu.corhuila.attbook.identity.users.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.web.SpringJUnitWebConfig;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringJUnitWebConfig(IdentityHttpSecurityTest.TestConfiguration.class)
@TestPropertySource(properties = {
        "app.jwt.secret=AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
        "app.jwt.issuer=att-book-api",
        "app.jwt.ttl-seconds=3600"
})
class IdentityHttpSecurityTest {

    @Configuration(proxyBeanMethods = false)
    @EnableWebMvc
    @EnableWebSecurity
    @Import({
            SecurityConfig.class,
            JwtConfiguration.class,
            JwtService.class,
            IdentityService.class,
            AuthController.class,
            CurrentUserController.class,
            CourseController.class
    })
    static class TestConfiguration {

        @Bean
        UserRepository userRepository() {
            return mock(UserRepository.class);
        }

        @Bean
        CourseService courseService() {
            return mock(CourseService.class);
        }
    }

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository repository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private JwtEncoder jwtEncoder;

    private MockMvc mvc;
    private AppUser teacher;

    @BeforeEach
    void setUp() {
        reset(repository, courseService);

        mvc = webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        teacher = new AppUser(
                "Demo Teacher",
                "teacher@example.test",
                passwordEncoder.encode("TeacherPass123!"),
                AppUser.Role.TEACHER
        );

        ReflectionTestUtils.setField(teacher, "id", 10L);

        when(repository.findByEmail("teacher@example.test"))
                .thenReturn(Optional.of(teacher));

        when(repository.findById(10L))
                .thenReturn(Optional.of(teacher));
    }

    @Test
    void loginAcceptsValidCredentialsWithoutAnExistingToken()
            throws Exception {
        mvc.perform(post("/api/v1/auth/login")
                        .contextPath("/api/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "teacher@example.test",
                                  "password": "TeacherPass123!"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.user.id").value(10))
                .andExpect(jsonPath("$.user.rol").value("TEACHER"))
                .andExpect(jsonPath("$.user.password").doesNotExist())
                .andExpect(jsonPath("$.user.passwordHash").doesNotExist());
    }

    @Test
    void loginRejectsWrongPassword() throws Exception {
        mvc.perform(post("/api/v1/auth/login")
                        .contextPath("/api/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "teacher@example.test",
                                  "password": "WrongPass123!"
                                }
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void publicRegistrationCreatesStudentWithoutExposingPassword()
            throws Exception {
        when(repository.saveAndFlush(any(AppUser.class)))
                .thenAnswer(invocation -> {
                    AppUser user = invocation.getArgument(0);
                    ReflectionTestUtils.setField(user, "id", 20L);
                    return user;
                });

        mvc.perform(post("/api/v1/auth/register")
                        .contextPath("/api/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Demo Student",
                                  "email": "student@example.test",
                                  "password": "StudentPass123!",
                                  "rol": "STUDENT"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(20))
                .andExpect(jsonPath("$.rol").value("STUDENT"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        verify(repository).saveAndFlush(any(AppUser.class));
    }

    @Test
    void publicRegistrationRejectsTeacherRole() throws Exception {
        mvc.perform(post("/api/v1/auth/register")
                        .contextPath("/api/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "Teacher",
                                  "email": "another@example.test",
                                  "password": "TeacherPass123!",
                                  "rol": "TEACHER"
                                }
                                """))
                .andExpect(status().isForbidden());

        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void registrationRejectsInvalidFields() throws Exception {
        mvc.perform(post("/api/v1/auth/register")
                        .contextPath("/api/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nombre": "",
                                  "email": "invalid-email",
                                  "password": "short",
                                  "rol": "STUDENT"
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void profileRejectsRequestWithoutToken() throws Exception {
        mvc.perform(get("/api/v1/usuarios/me")
                        .contextPath("/api/v1"))
                .andExpect(status().isUnauthorized());

        verify(repository, never()).findById(anyLong());
    }

    @Test
    void profileAcceptsValidSignedToken() throws Exception {
        mvc.perform(get("/api/v1/usuarios/me")
                        .contextPath("/api/v1")
                        .header("Authorization", "Bearer " + teacherToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.email")
                        .value("teacher@example.test"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());

        verify(repository).findById(10L);
    }

    @Test
    void profileRejectsMalformedToken() throws Exception {
        mvc.perform(get("/api/v1/usuarios/me")
                        .contextPath("/api/v1")
                        .header("Authorization", "Bearer invalid-token"))
                .andExpect(status().isUnauthorized());

        verify(repository, never()).findById(anyLong());
    }

    @Test
    void profileRejectsExpiredToken() throws Exception {
        JwtService expiredTokenService = new JwtService(
                jwtEncoder,
                Clock.fixed(
                        Instant.now().minusSeconds(7200),
                        ZoneOffset.UTC
                ),
                "att-book-api",
                3600
        );

        String token = expiredTokenService.issueToken(
                10L,
                "teacher@example.test",
                "TEACHER"
        );

        mvc.perform(get("/api/v1/usuarios/me")
                        .contextPath("/api/v1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());

        verify(repository, never()).findById(anyLong());
    }

    @Test
    void coursesRejectRequestWithoutToken() throws Exception {
        mvc.perform(get("/api/v1/cursos")
                        .contextPath("/api/v1"))
                .andExpect(status().isUnauthorized());

        verifyNoInteractions(courseService);
    }

    @Test
    void coursesRejectStudentRole() throws Exception {
        String token = jwtService.issueToken(
                20L,
                "student@example.test",
                "STUDENT"
        );

        mvc.perform(get("/api/v1/cursos")
                        .contextPath("/api/v1")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        verifyNoInteractions(courseService);
    }

    @Test
    void coursesReceiveVerifiedTeacherIdentity() throws Exception {
        CourseActor expectedActor = new CourseActor(10L, "TEACHER");

        when(courseService.list(expectedActor))
                .thenReturn(List.of());

        mvc.perform(get("/api/v1/cursos")
                        .contextPath("/api/v1")
                        .header("Authorization", "Bearer " + teacherToken()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        verify(courseService).list(expectedActor);
    }

    private String teacherToken() {
        return jwtService.issueToken(
                10L,
                "teacher@example.test",
                "TEACHER"
        );
    }
}