
package co.edu.corhuila.attbook.academic.sessions.qr;

import java.time.Instant;

public record QrResponse(
        String qr,
        Instant expiresAt
) {

    public static QrResponse from(QrCode qrCode) {
        return new QrResponse(
                qrCode.getToken(),
                qrCode.getExpiresAt()
        );
    }
}
