package edu.unimagdalena.RCMU.service.impl;

import edu.unimagdalena.RCMU.api.dto.AnalyticsDtos.AvailabilitySlotResponse;
import edu.unimagdalena.RCMU.domine.entity.DoctorSchedule;
import edu.unimagdalena.RCMU.domine.enums.AppointmentStatus;
import edu.unimagdalena.RCMU.domine.enums.DayOfWeek;
import edu.unimagdalena.RCMU.domine.repository.AppointmentRepository;
import edu.unimagdalena.RCMU.domine.repository.DoctorScheduleRepository;
import edu.unimagdalena.RCMU.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AvailabilityServiceImpl implements AvailabilityService {

    private final AppointmentRepository appointmentRepo;
    private final DoctorScheduleRepository scheduleRepo;

    @Override
    public List<AvailabilitySlotResponse> checkDoctorAvailability(Long doctorId, LocalDate date) {
        // 1. Obtener el día de la semana
        DayOfWeek day = DayOfWeek.valueOf(date.getDayOfWeek().name());

        // 2. Obtener horarios laborales del doctor
        var schedules = scheduleRepo.findByDoctorIdAndDayOfWeek(doctorId, day);

        // 3. Obtener citas agendadas (No canceladas) para ese día
        var appointments = appointmentRepo.findByDateTimeBetween(
                        date.atStartOfDay(),
                        date.atTime(LocalTime.MAX)
                ).stream()
                .filter(a -> a.getDoctor().getId().equals(doctorId))
                .filter(a -> a.getStatus() != AppointmentStatus.CANCELLED)
                .toList();

        List<AvailabilitySlotResponse> availableSlots = new ArrayList<>();

        // 4. Lógica de generación de slots (Regla 6.6: Bloques completos)
        // Nota: Aquí asumimos una duración estándar (ej. 30 min) o podría recibirse por parámetro
        int durationMinutes = 30;

        for (DoctorSchedule schedule : schedules) {
            LocalTime current = schedule.getStartTime();

            while (!current.plusMinutes(durationMinutes).isAfter(schedule.getEndTime())) {
                LocalTime slotEnd = current.plusMinutes(durationMinutes);
                LocalDateTime startDT = date.atTime(current);
                LocalDateTime endDT = date.atTime(slotEnd);

                // Verificar si el bloque está ocupado por alguna cita
                boolean isOccupied = appointments.stream().anyMatch(a ->
                        (startDT.isBefore(a.getEndAt()) && endDT.isAfter(a.getDateTime()))
                );

                if (!isOccupied) {
                    availableSlots.add(new AvailabilitySlotResponse(startDT, endDT, true));
                }

                current = slotEnd; // Siguiente bloque
            }
        }

        return availableSlots;
    }

    @Override
    public List<AvailabilitySlotResponse> checkOfficeAvailability(Long officeId, LocalDate date) {
        // 1. Buscamos todas las citas que ocupan ese consultorio en el día
        var appointments = appointmentRepo.findByDateTimeBetween(
                        date.atStartOfDay(),
                        date.atTime(LocalTime.MAX)
                ).stream()
                .filter(a -> a.getOffice().getId().equals(officeId))
                // Solo cuentan las que sí van a ocurrir o ya ocurrieron
                .filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED ||
                        a.getStatus() == AppointmentStatus.CONFIRMED ||
                        a.getStatus() == AppointmentStatus.COMPLETED)
                .toList();

        // 2. Mapeamos a AvailabilitySlotResponse
        // El booleano 'available' (el tercer parámetro) va en FALSE
        // porque estos son los bloques que OCUPAN el consultorio.
        return appointments.stream()
                .map(a -> new AvailabilitySlotResponse(
                        a.getDateTime(),
                        a.getEndAt(),
                        false // false = ocupado
                ))
                .toList();
    }
}