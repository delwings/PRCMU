package edu.unimagdalena.RCMU.api.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalTime;
import edu.unimagdalena.RCMU.domine.enums.DayOfWeek;

public class DoctorScheduleDtos {
    public record CreateDoctorScheduleRequest(
            @NotNull Long doctorId,
            @NotNull DayOfWeek dayOfWeek,
            @NotNull LocalTime startTime,
            @NotNull LocalTime endTime
    ) implements Serializable {}

    public record DoctorScheduleResponse(Long id, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, Boolean active) implements Serializable {}
}