package co.edu.corhuila.attbook.identity.users;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import co.edu.corhuila.attbook.identity.security.JwtService;
import co.edu.corhuila.attbook.identity.users.IdentityDtos.AuthResponse;
import co.edu.corhuila.attbook.identity.users.IdentityDtos.LoginRequest;
import co.edu.corhuila.attbook.identity.users.IdentityDtos.RegisterRequest;
import co.edu.corhuila.attbook.identity.users.IdentityDtos.UserResponse;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class IdentityService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final String dummyPasswordHash;

    public IdentityService(
            UserRepository repository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.dummyPasswordHash = passwordEncoder.encode(
                "dummy-password-for-unknown-accounts"
        );
    }

    @Transactional
    public UserResponse register(RegisterRequest request) {
        if (request.rol() != null
                && request.rol() != AppUser.Role.STUDENT) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Public registration only supports student accounts"
            );
        }

        requireSupportedPasswordLength(request.password());

        String email = normalizeEmail(request.email());

        if (repository.existsByEmail(email)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "An account with this email already exists"
            );
        }

        AppUser user = new AppUser(
                request.nombre().strip(),
                email,
                passwordEncoder.encode(request.password()),
                AppUser.Role.STUDENT
        );

        try {
            return UserResponse.from(repository.saveAndFlush(user));
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "The account conflicts with existing data"
            );
        }
    }

    public AuthResponse login(LoginRequest request) {
        requireSupportedPasswordLength(request.password());

        AppUser user = repository
                .findByEmail(normalizeEmail(request.email()))
                .orElse(null);

        String storedHash = user == null
                ? dummyPasswordHash
                : user.getPasswordHash();

        boolean passwordMatches = passwordEncoder.matches(
                request.password(),
                storedHash
        );

        if (user == null || !passwordMatches) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid credentials"
            );
        }

        String token = jwtService.issueToken(
                user.getId(),
                user.getEmail(),
                user.getRole().name()
        );

        return new AuthResponse(token, UserResponse.from(user));
    }

    public UserResponse currentUser(Long userId) {
        AppUser user = repository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));

        return UserResponse.from(user);
    }

    private String normalizeEmail(String email) {
        return email.strip().toLowerCase(Locale.ROOT);
    }

    private void requireSupportedPasswordLength(String password) {
        if (password.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Password must not exceed 72 UTF-8 bytes"
            );
        }
    }
}