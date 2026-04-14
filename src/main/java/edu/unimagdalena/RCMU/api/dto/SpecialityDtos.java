package edu.unimagdalena.RCMU.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

public class SpecialityDtos {
    public record CreateSpecialtyRequest(
            @NotBlank @Size(min = 3, max = 50) String name
    ) implements Serializable {}

    public record SpecialtyResponse(Long id, String name) implements Serializable {}
}