package edu.unimagdalena.RCMU.api.dto;

import java.io.Serializable;
import java.time.LocalTime;
import edu.unimagdalena.RCMU.domine.enums.DayOfWeek;

public class DoctorScheduleDtos {
    public record CreateDoctorScheduleRequest(Long doctorId, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime) implements Serializable {}
    public record DoctorScheduleResponse(Long id, DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, Boolean active) implements Serializable {}
}