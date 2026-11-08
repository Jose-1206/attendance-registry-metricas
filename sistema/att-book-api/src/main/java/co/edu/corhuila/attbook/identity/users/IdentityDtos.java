package co.edu.corhuila.attbook.identity.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class IdentityDtos {

    private IdentityDtos() {
    }

    public record RegisterRequest(
            @NotBlank
            @Size(max = 150)
            String nombre,

            @NotBlank
            @Email
            @Size(max = 254)
            String email,

            @NotBlank
            @Size(min = 8, max = 72)
            String password,

            AppUser.Role rol
    ) {
    }

    public record LoginRequest(
            @NotBlank
            @Email
            @Size(max = 254)
            String email,

            @NotBlank
            @Size(max = 72)
            String password
    ) {
    }

    public record UserResponse(
            Long id,
            String nombre,
            String email,
            String rol
    ) {
        public static UserResponse from(AppUser user) {
            return new UserResponse(
                    user.getId(),
                    user.getName(),
                    user.getEmail(),
                    user.getRole().name()
            );
        }
    }

    public record AuthResponse(
            String token,
            UserResponse user
    ) {
    }
}