package edu.unimagdalena.RCMU.api.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;

public class DoctorDtos {
    public record CreateDoctorRequest(
            @NotBlank String firstName,
            @NotBlank String lastName,
            @Email @NotBlank String email,
            @NotNull Long specialityId
    ) implements Serializable {}

    public record UpdateDoctorRequest(
            @NotBlank String firstName,
            @NotBlank String lastName,
            @NotNull Boolean isActive
    ) implements Serializable {}

    public record DoctorResponse(
            Long id,
            String firstName,
            String lastName,
            String email,
            String specialityName,
            Boolean isActive
    ) implements Serializable {}
}