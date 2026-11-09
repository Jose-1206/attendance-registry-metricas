package co.edu.corhuila.attbook.academic.sessions;

import java.net.URI;
import java.security.Principal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import co.edu.corhuila.attbook.academic.courses.CoursePrincipal;

@RestController
@RequestMapping("/sessions")
@Validated
public class SessionController {

    private final SessionService service;

    public SessionController(SessionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SessionResponse> create(
            @Valid @RequestBody SessionRequest request,
            Principal principal
    ) {
        SessionResponse response = service.create(
                request,
                actorFrom(principal)
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{id}")
    public SessionResponse findById(
            @PathVariable("id") @Positive Long id,
            Principal principal
    ) {
        return service.findById(id, actorFrom(principal));
    }

    @GetMapping("/courses/{courseId}")
    public List<SessionResponse> findByCourse(
            @PathVariable("courseId") @Positive Long courseId,
            Principal principal
    ) {
        return service.findByCourse(courseId, actorFrom(principal));
    }

    @PutMapping("/{id}")
    public SessionResponse update(
            @PathVariable("id") @Positive Long id,
            @Valid @RequestBody SessionRequest request,
            Principal principal
    ) {
        return service.update(id, request, actorFrom(principal));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable("id") @Positive Long id,
            Principal principal
    ) {
        service.delete(id, actorFrom(principal));
        return ResponseEntity.noContent().build();
    }

    private SessionActor actorFrom(Principal principal) {
        if (!(principal instanceof CoursePrincipal authenticated)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication is required"
            );
        }

        return new SessionActor(
                authenticated.getUserId(),
                authenticated.getRole()
        );
    }
}