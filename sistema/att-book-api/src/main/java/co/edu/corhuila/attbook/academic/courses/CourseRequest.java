package co.edu.corhuila.attbook.academic.courses;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CourseRequest(

        @NotBlank(message = "Course name is required")
        @Size(max = 150, message = "Course name must not exceed 150 characters")
        String nombre,

        @NotBlank(message = "Course code is required")
        @Size(max = 30, message = "Course code must not exceed 30 characters")
        String codigo

) {
}