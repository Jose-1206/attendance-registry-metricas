
package co.edu.corhuila.attbook.academic.enrollments;

import java.security.Principal;
import java.util.List;

import co.edu.corhuila.attbook.academic.courses.CoursePrincipal;

import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/enrollments")
@Validated
public class EnrollmentController {

    private final EnrollmentService service;

    public EnrollmentController(EnrollmentService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public List<Enrollment> listMine(Principal principal) {
        return service.listMine(actorFrom(principal));
    }

    @PostMapping("/courses/{courseId}")
    public ResponseEntity<Enrollment> enroll(
            @PathVariable("courseId") @Positive Long courseId,
            Principal principal
    ) {
        Enrollment enrollment = service.enroll(
                courseId,
                actorFrom(principal)
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(enrollment);
    }

    private EnrollmentActor actorFrom(Principal principal) {
        if (!(principal instanceof CoursePrincipal authenticated)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authentication is required"
            );
        }

        return new EnrollmentActor(
                authenticated.getUserId(),
                authenticated.getRole()
        );
    }
}
