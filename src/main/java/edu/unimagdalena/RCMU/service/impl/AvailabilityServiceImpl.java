package edu.unimagdalena.RCMU.services.impl;

import edu.unimagdalena.RCMU.api.dto.AnalyticsDtos.AvailabilitySlotResponse;
import edu.unimagdalena.RCMU.domine.repositories.AppointmentRepository;
import edu.unimagdalena.RCMU.domine.repositories.DoctorScheduleRepository;
import edu.unimagdalena.RCMU.services.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AppointmentRepository appointmentRepo;
    private final DoctorScheduleRepository scheduleRepo;

    @Override
    public List<AvailabilitySlotResponse> checkDoctorAvailability(Long doctorId, LocalDate date) {

        var dayOfWeek = java.time.format.DateTimeFormatter.ofPattern("EEEE", java.util.Locale.ENGLISH)
                .format(date).toUpperCase();

        var appointments = appointmentRepo.findByDoctorId(doctorId);

        return schedules.stream()
                .map(s -> new AvailabilitySlotResponse(
                        date.atTime(s.getStartTime()),
                        date.atTime(s.getEndTime()),
                        true))
                .toList();
    }

    @Override
    public List<AvailabilitySlotResponse> checkOfficeAvailability(Long officeId, LocalDate date) {
        return List.of();
    }
}