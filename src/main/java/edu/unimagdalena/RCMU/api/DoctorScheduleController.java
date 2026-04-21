package edu.unimagdalena.RCMU.api;

import edu.unimagdalena.RCMU.api.dto.DoctorScheduleDtos.*;
import edu.unimagdalena.RCMU.service.DoctorScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/doctors/{doctorId}/schedules")
@RequiredArgsConstructor
public class DoctorScheduleController {

    private final DoctorScheduleService service;

    @PostMapping
    public ResponseEntity<DoctorScheduleResponse> create(@PathVariable Long doctorId,
                                                         @Valid @RequestBody CreateDoctorScheduleRequest req) {
        return ResponseEntity.ok(service.create(doctorId, req));
    }

    @GetMapping
    public ResponseEntity<List<DoctorScheduleResponse>> listByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(service.getByDoctor(doctorId));
    }
}