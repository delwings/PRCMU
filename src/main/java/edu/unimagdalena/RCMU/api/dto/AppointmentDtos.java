package edu.unimagdalena.RCMU.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import edu.unimagdalena.RCMU.domine.enums.AppointmentStatus;

public class AppointmentDtos {
    public record CreateAppointmentRequest(LocalDateTime dateTime, Long patientId, Long doctorId, Long officeId, Long typeId) implements Serializable {}
    public record CancelAppointmentRequest(String reason) implements Serializable {}
    public record AppointmentResponse(Long id, LocalDateTime dateTime, String patientName, String doctorName, AppointmentStatus status) implements Serializable {}
}