package edu.unimagdalena.RCMU.api;

import edu.unimagdalena.RCMU.api.dto.AnalyticsDtos.AvailabilitySlotResponse;
import edu.unimagdalena.RCMU.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/availability")
@RequiredArgsConstructor
public class AvailabilityController {

    private final AvailabilityService service;

    @GetMapping("/doctors/{doctorId}")
    public ResponseEntity<List<AvailabilitySlotResponse>> getDoctorAvailability(
            @PathVariable Long doctorId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        return ResponseEntity.ok(service.checkDoctorAvailability(doctorId, date));
    }
}