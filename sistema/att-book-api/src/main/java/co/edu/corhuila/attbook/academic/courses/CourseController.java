package co.edu.corhuila.attbook.academic.courses;

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

@RestController
@RequestMapping("/cursos")
@Validated
public class CourseController {

    private final CourseService service;

    public CourseController(CourseService service) {
        this.service = service;
    }

    @GetMapping
    public List<CourseResponse> list(Principal principal) {
        return service.list(actorFrom(principal));
    }

    @GetMapping("/{id}")
    public CourseResponse findById(
            @PathVariable("id") @Positive Long id,
            Principal principal
    ) {
        return service.findById(id, actorFrom(principal));
    }

    @PostMapping
    public ResponseEntity<CourseResponse> create(
            @Valid @RequestBody CourseRequest request,
            Principal principal
    ) {
        CourseResponse response = service.create(
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

    @PutMapping("/{id}")
    public CourseResponse update(
            @PathVariable("id") @Positive Long id,
            @Valid @RequestBody CourseRequest request,
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

    private CourseActor actorFrom(Principal principal) {
        if (!(principal instanceof CoursePrincipal authenticated)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication is required"
            );
        }

        return new CourseActor(
                authenticated.getUserId(),
                authenticated.getRole()
        );
    }
}