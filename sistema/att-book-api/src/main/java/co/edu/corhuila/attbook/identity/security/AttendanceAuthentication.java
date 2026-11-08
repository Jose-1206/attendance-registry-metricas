package co.edu.corhuila.attbook.identity.security;

import java.util.List;

import co.edu.corhuila.attbook.academic.courses.CoursePrincipal;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

/**
 * Authenticated JWT identity compatible with the existing course module.
 */
public final class AttendanceAuthentication
        extends JwtAuthenticationToken
        implements CoursePrincipal {

    private final Long userId;
    private final String role;

    public AttendanceAuthentication(
            Jwt jwt,
            Long userId,
            String email,
            String role
    ) {
        super(
                jwt,
                List.of(new SimpleGrantedAuthority("ROLE_" + role)),
                email
        );

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
}