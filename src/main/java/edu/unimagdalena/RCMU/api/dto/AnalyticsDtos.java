package edu.unimagdalena.RCMU.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

public class AnalyticsDtos {
    public record AvailabilitySlotResponse(LocalDateTime start, LocalDateTime end, Boolean isAvailable) implements Serializable {}
    public record DoctorProductivityResponse(String doctorName, Long attendedPatients, Double efficiencyRate) implements Serializable {}
    public record NoShowPatientResponse(String patientName, String documentId, Long missedAppointments) implements Serializable {}
}