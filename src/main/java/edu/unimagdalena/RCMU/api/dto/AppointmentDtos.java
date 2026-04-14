package edu.unimagdalena.RCMU.api.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import edu.unimagdalena.RCMU.domine.enums.AppointmentStatus;

public class AppointmentDtos {
    public record CreateAppointmentRequest(
            @NotNull @Future LocalDateTime dateTime, // No permite citas en el pasado
            @NotNull Long patientId,
            @NotNull Long doctorId,
            @NotNull Long officeId,
            @NotNull Long typeId
    ) implements Serializable {}

    public record CancelAppointmentRequest(
            @NotBlank @Size(min = 10, max = 255) String reason
    ) implements Serializable {}

    public record AppointmentResponse(
            Long id,
            LocalDateTime dateTime,
            String patientName,
            String doctorName,
            AppointmentStatus status
    ) implements Serializable {}
}