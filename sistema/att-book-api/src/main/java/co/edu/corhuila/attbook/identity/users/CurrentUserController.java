package co.edu.corhuila.attbook.identity.users;

import java.security.Principal;

import co.edu.corhuila.attbook.identity.security.AttendanceAuthentication;
import co.edu.corhuila.attbook.identity.users.IdentityDtos.UserResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/usuarios")
public class CurrentUserController {

    private final IdentityService service;

    public CurrentUserController(IdentityService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public UserResponse currentUser(Principal principal) {
        if (!(principal instanceof AttendanceAuthentication authenticated)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication is required"
            );
        }

        return service.currentUser(authenticated.getUserId());
    }
}