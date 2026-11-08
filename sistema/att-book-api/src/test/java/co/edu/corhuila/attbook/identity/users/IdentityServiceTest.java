package co.edu.corhuila.attbook.identity.users;

import java.util.Optional;

import co.edu.corhuila.attbook.identity.security.JwtService;
import co.edu.corhuila.attbook.identity.users.IdentityDtos.LoginRequest;
import co.edu.corhuila.attbook.identity.users.IdentityDtos.RegisterRequest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class IdentityServiceTest {

    private UserRepository repository;
    private JwtService jwtService;
    private PasswordEncoder passwordEncoder;
    private IdentityService service;

    @BeforeEach
    void setUp() {
        repository = mock(UserRepository.class);
        jwtService = mock(JwtService.class);
        passwordEncoder = new BCryptPasswordEncoder(4);

        service = new IdentityService(
                repository,
                passwordEncoder,
                jwtService
        );
    }

    @Test
    void registersStudentWithNormalizedEmailAndHashedPassword() {
        when(repository.saveAndFlush(any(AppUser.class)))
                .thenAnswer(invocation -> {
                    AppUser user = invocation.getArgument(0);
                    ReflectionTestUtils.setField(user, "id", 7L);
                    return user;
                });

        var response = service.register(new RegisterRequest(
                " Jose Miguel ",
                "JOSE@example.test",
                "StudentPass123!",
                AppUser.Role.STUDENT
        ));

        ArgumentCaptor<AppUser> captor =
                ArgumentCaptor.forClass(AppUser.class);

        verify(repository).saveAndFlush(captor.capture());

        AppUser saved = captor.getValue();

        assertAll(
                () -> assertEquals(7L, response.id()),
                () -> assertEquals("Jose Miguel", response.nombre()),
                () -> assertEquals(
                        "jose@example.test",
                        response.email()
                ),
                () -> assertEquals("STUDENT", response.rol()),
                () -> assertNotEquals(
                        "StudentPass123!",
                        saved.getPasswordHash()
                ),
                () -> assertTrue(passwordEncoder.matches(
                        "StudentPass123!",
                        saved.getPasswordHash()
                ))
        );
    }

    @Test
    void rejectsPublicTeacherRegistration() {
        var exception = assertThrows(
                ResponseStatusException.class,
                () -> service.register(new RegisterRequest(
                        "Teacher",
                        "teacher@example.test",
                        "TeacherPass123!",
                        AppUser.Role.TEACHER
                ))
        );

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        verifyNoInteractions(repository, jwtService);
    }

    @Test
    void rejectsExistingEmail() {
        when(repository.existsByEmail("jose@example.test"))
                .thenReturn(true);

        var exception = assertThrows(
                ResponseStatusException.class,
                () -> service.register(new RegisterRequest(
                        "Jose",
                        "JOSE@example.test",
                        "StudentPass123!",
                        AppUser.Role.STUDENT
                ))
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(repository, never()).saveAndFlush(any());
    }

    @Test
    void handlesConcurrentDuplicateRegistration() {
        when(repository.saveAndFlush(any(AppUser.class)))
                .thenThrow(new DataIntegrityViolationException(
                        "Unique email constraint"
                ));

        var exception = assertThrows(
                ResponseStatusException.class,
                () -> service.register(new RegisterRequest(
                        "Jose",
                        "jose@example.test",
                        "StudentPass123!",
                        AppUser.Role.STUDENT
                ))
        );

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
    }

    @Test
    void validCredentialsReturnTokenAndSafeUserResponse() {
        AppUser user = existingTeacher();

        when(repository.findByEmail("teacher@example.test"))
                .thenReturn(Optional.of(user));

        when(jwtService.issueToken(
                10L,
                "teacher@example.test",
                "TEACHER"
        )).thenReturn("signed-token");

        var response = service.login(new LoginRequest(
                "TEACHER@example.test",
                "TeacherPass123!"
        ));

        assertAll(
                () -> assertEquals("signed-token", response.token()),
                () -> assertEquals(10L, response.user().id()),
                () -> assertEquals("TEACHER", response.user().rol())
        );
    }

    @Test
    void wrongPasswordDoesNotIssueToken() {
        when(repository.findByEmail("teacher@example.test"))
                .thenReturn(Optional.of(existingTeacher()));

        var exception = assertThrows(
                ResponseStatusException.class,
                () -> service.login(new LoginRequest(
                        "teacher@example.test",
                        "WrongPass123!"
                ))
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verifyNoInteractions(jwtService);
    }

    @Test
    void unknownEmailDoesNotIssueToken() {
        when(repository.findByEmail("unknown@example.test"))
                .thenReturn(Optional.empty());

        var exception = assertThrows(
                ResponseStatusException.class,
                () -> service.login(new LoginRequest(
                        "unknown@example.test",
                        "SomePassword123!"
                ))
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        verifyNoInteractions(jwtService);
    }

    @Test
    void returnsCurrentUser() {
        when(repository.findById(10L))
                .thenReturn(Optional.of(existingTeacher()));

        var response = service.currentUser(10L);

        assertEquals(10L, response.id());
        assertEquals("teacher@example.test", response.email());
    }

    @Test
    void rejectsPasswordExceedingBcryptByteLimit() {
        String password = "é".repeat(40);

        var exception = assertThrows(
                ResponseStatusException.class,
                () -> service.register(new RegisterRequest(
                        "Jose",
                        "jose@example.test",
                        password,
                        AppUser.Role.STUDENT
                ))
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verifyNoInteractions(repository, jwtService);
    }

    private AppUser existingTeacher() {
        AppUser user = new AppUser(
                "Teacher",
                "teacher@example.test",
                passwordEncoder.encode("TeacherPass123!"),
                AppUser.Role.TEACHER
        );

        ReflectionTestUtils.setField(user, "id", 10L);

        return user;
    }
}