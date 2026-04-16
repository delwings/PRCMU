package edu.unimagdalena.RCMU.api.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import edu.unimagdalena.RCMU.domine.enums.OfficeStatus;

public class OfficeDtos {
    public record CreateOfficeRequest(
            @NotBlank String roomNumber,
            @NotNull @Min(1) String location
    ) implements Serializable {}

    public record UpdateOfficeRequest(@NotNull OfficeStatus status) implements Serializable {}

    public record OfficeResponse(Long id, String roomNumber, String location, OfficeStatus status) implements Serializable {}

    public record OfficeOccupancyResponse(String roomNumber, Long totalAppointments, Double occupancyPercentage) implements Serializable {}
}