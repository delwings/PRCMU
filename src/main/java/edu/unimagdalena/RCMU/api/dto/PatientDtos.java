package edu.unimagdalena.RCMU.api.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import edu.unimagdalena.RCMU.domine.enums.PatientStatus;

public class PatientDtos {
    public record CreatePatientRequest(
            @NotBlank String documentId,
            @NotBlank String firstName,
            @NotBlank String lastName,
            @Email @NotBlank String email
    ) implements Serializable {}

    public record UpdatePatientRequest(
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotNull PatientStatus status
    ) implements Serializable {}

    public record PatientResponse(
            Long id,
            String documentId,
            String firstName,
            String lastName,
            String email,
            PatientStatus status
    ) implements Serializable {}
}