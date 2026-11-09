
package co.edu.corhuila.attbook.academic.sessions;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SessionRequest(

        @NotNull(message = "Course ID is required")
        Long courseId,

        @NotBlank(message = "Session title is required")
        @Size(max = 150, message = "Session title must not exceed 150 characters")
        String title,

        @Size(max = 500, message = "Description must not exceed 500 characters")
        String description,

        @NotNull(message = "Start date and time are required")
        LocalDateTime startAt,

        @NotNull(message = "End date and time are required")
        LocalDateTime endAt

) {
}
