package edu.unimagdalena.RCMU.api;

import edu.unimagdalena.RCMU.api.dto.AppointmentDtos.*;
import edu.unimagdalena.RCMU.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List; // <--- Import agregado

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;

    @PostMapping
    public ResponseEntity<AppointmentResponse> create(@Valid @RequestBody CreateAppointmentRequest req) {
        return ResponseEntity.status(201).body(service.schedule(req));
    }

    @GetMapping
    public ResponseEntity<List<AppointmentResponse>> list() {
        return ResponseEntity.ok(service.getAll());
    }

    @PutMapping("/{id}/confirm")
    public ResponseEntity<AppointmentResponse> confirm(@PathVariable Long id) {
        return ResponseEntity.ok(service.confirm(id));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<AppointmentResponse> complete(@PathVariable Long id, @RequestParam String observations) {
        return ResponseEntity.ok(service.complete(id, observations));
    }

    @PutMapping("/{id}/no-show")
    public ResponseEntity<AppointmentResponse> noShow(@PathVariable Long id) {
        return ResponseEntity.ok(service.markAsNoShow(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id, @Valid @RequestBody CancelAppointmentRequest req) {
        service.cancel(id, req);
        return ResponseEntity.noContent().build();
    }
}