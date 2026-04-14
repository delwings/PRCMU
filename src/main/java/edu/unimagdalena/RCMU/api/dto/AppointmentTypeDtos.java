package edu.unimagdalena.RCMU.api.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;

public class AppointmentTypeDtos {
    public record CreateAppointmentTypeRequest(
            @NotBlank String name,
            @NotNull @Min(15) @Max(120) Integer duration
    ) implements Serializable {}

    public record AppointmentTypeResponse(Long id, String name, Integer duration) implements Serializable {}
}