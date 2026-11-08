package co.edu.corhuila.attbook.identity.users;

import jakarta.validation.Valid;

import co.edu.corhuila.attbook.identity.users.IdentityDtos.AuthResponse;
import co.edu.corhuila.attbook.identity.users.IdentityDtos.LoginRequest;
import co.edu.corhuila.attbook.identity.users.IdentityDtos.RegisterRequest;
import co.edu.corhuila.attbook.identity.users.IdentityDtos.UserResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final IdentityService service;

    public AuthController(IdentityService service) {
        this.service = service;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return service.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(
            @Valid @RequestBody LoginRequest request
    ) {
        return service.login(request);
    }
}