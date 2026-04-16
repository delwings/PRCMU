package edu.unimagdalena.RCMU.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public class SpecialityDtos {
    public record CreateSpecialityRequest(
            @NotBlank @Size(min = 3, max = 50) String name
    ) implements Serializable {}

    public record SpecialityResponse(Long id, String name) implements Serializable {}
}