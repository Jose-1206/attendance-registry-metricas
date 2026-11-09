
package co.edu.corhuila.attbook.academic.sessions.qr;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.corhuila.attbook.academic.sessions.SessionActor;
import co.edu.corhuila.attbook.academic.sessions.SessionService;

@Service
public class QrService {

    private final QrCodeRepository qrCodeRepository;
    private final SessionService sessionService;
    private final SecureRandom secureRandom = new SecureRandom();

    public QrService(
            QrCodeRepository qrCodeRepository,
            SessionService sessionService
    ) {
        this.qrCodeRepository = qrCodeRepository;
        this.sessionService = sessionService;
    }

    @Transactional
    public QrCode generateQr(
            Long sessionId,
            SessionActor actor
    ) {
        // Verificar que la sesión exista y pertenezca al docente
        sessionService.validateTeacherForQr(sessionId, actor);

        // Generar un token aleatorio seguro
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        // Establecer duración de 15 minutos
        Instant now = Instant.now();
        Instant expiresAt = now.plus(15, ChronoUnit.MINUTES);

        // Crear el registro del código QR
        QrCode qrCode = new QrCode(
                sessionId,
                token,
                now,
                expiresAt
        );

        // Guardar el código QR en PostgreSQL
        return qrCodeRepository.save(qrCode);
    }

    @Transactional(readOnly = true)
    public boolean isQrValid(String token) {

        if (token == null || token.isBlank()) {
            return false;
        }

        return qrCodeRepository.findByToken(token)
                .map(qrCode ->
                        qrCode.isActive()
                        && Instant.now().isBefore(
                                qrCode.getExpiresAt()
                        )
                )
                .orElse(false);
    }
}
