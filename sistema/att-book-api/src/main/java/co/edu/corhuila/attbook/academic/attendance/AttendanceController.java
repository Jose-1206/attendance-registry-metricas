
package co.edu.corhuila.attbook.academic.attendance;

import java.security.Principal;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import co.edu.corhuila.attbook.academic.courses.CoursePrincipal;
import co.edu.corhuila.attbook.academic.sessions.SessionActor;

@RestController
@Validated
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(
            AttendanceService attendanceService
    ) {
        this.attendanceService = attendanceService;
    }

    // CONSULTAR ASISTENCIAS DE UNA CLASE - DOCENTE
    @GetMapping("/clases/{claseId}/asistencias")
    public List<AttendanceResponse> findByClass(
            @PathVariable("claseId") @Positive Long claseId,
            Principal principal
    ) {
        SessionActor actor = actorFrom(principal);

        return attendanceService.findByClass(
                claseId,
                actor
        );
    }

    // REGISTRAR ASISTENCIA MEDIANTE QR - ESTUDIANTE
    @PostMapping("/asistencias")
    public ResponseEntity<AttendanceResponse> registerAttendance(
            @Valid @RequestBody AttendanceRequest request,
            Principal principal
    ) {
        SessionActor actor = actorFrom(principal);

        AttendanceResponse response =
                attendanceService.registerAttendance(
                        request,
                        actor
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // CONSULTAR HISTORIAL DEL ESTUDIANTE
    @GetMapping("/asistencias/historial")
    public List<AttendanceResponse> findMyHistory(
            Principal principal
    ) {
        SessionActor actor = actorFrom(principal);

        return attendanceService.findMyHistory(actor);
    }

    // OBTENER USUARIO AUTENTICADO
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
