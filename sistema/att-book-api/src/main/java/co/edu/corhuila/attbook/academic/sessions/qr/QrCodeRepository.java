
package co.edu.corhuila.attbook.academic.sessions.qr;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QrCodeRepository extends JpaRepository<QrCode, Long> {

    // Buscar un código QR mediante su token
    Optional<QrCode> findByToken(String token);

    // Comprobar si un token ya existe
    boolean existsByToken(String token);
}
