
package co.edu.corhuila.attbook.academic.sessions.qr;

import java.security.Principal;

import jakarta.validation.constraints.Positive;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import co.edu.corhuila.attbook.academic.courses.CoursePrincipal;
import co.edu.corhuila.attbook.academic.sessions.SessionActor;

@RestController
@RequestMapping("/clases")
@Validated
public class QrController {

    private final QrService qrService;

    public QrController(QrService qrService) {
        this.qrService = qrService;
    }

    @PostMapping("/{claseId}/qr")
    public QrResponse generateQr(
            @PathVariable("claseId") @Positive Long claseId,
            Principal principal
    ) {
        SessionActor actor = actorFrom(principal);

        QrCode qrCode = qrService.generateQr(
                claseId,
                actor
        );

        return QrResponse.from(qrCode);
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
