package edu.unimagdalena.RCMU.services;

import edu.unimagdalena.RCMU.api.dto.AnalyticsDtos.AvailabilitySlotResponse;
import java.time.LocalDate;
import java.util.List;

public interface AvailabilityService {
    List<AvailabilitySlotResponse> checkDoctorAvailability(Long doctorId, LocalDate date);
    List<AvailabilitySlotResponse> checkOfficeAvailability(Long officeId, LocalDate date);
}