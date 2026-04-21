package edu.unimagdalena.RCMU.api;

import edu.unimagdalena.RCMU.api.dto.AppointmentTypeDtos.*;
import edu.unimagdalena.RCMU.service.AppointmentTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/appointment-types")
@RequiredArgsConstructor
@Validated
public class AppointmentTypeController {

    private final AppointmentTypeService service;

    @PostMapping
    public ResponseEntity<AppointmentTypeResponse> create(@Valid @RequestBody CreateAppointmentTypeRequest req,
                                                          UriComponentsBuilder uriBuilder) {
        var created = service.create(req);
        var location = uriBuilder.path("/api/appointment-types/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentTypeResponse>> list() {
        return ResponseEntity.ok(service.getAll());
    }
}