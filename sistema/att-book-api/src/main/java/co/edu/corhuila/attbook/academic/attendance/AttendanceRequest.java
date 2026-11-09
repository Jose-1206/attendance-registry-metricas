
package co.edu.corhuila.attbook.academic.attendance;

import jakarta.validation.constraints.NotBlank;

public record AttendanceRequest(

        @NotBlank(message = "El codigo QR es obligatorio")
        String qr

) {
}
