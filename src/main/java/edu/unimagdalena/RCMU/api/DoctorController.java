package edu.unimagdalena.RCMU.api;

import edu.unimagdalena.RCMU.api.dto.DoctorDtos.*;
import edu.unimagdalena.RCMU.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
@Validated
public class DoctorController {

    private final DoctorService service;

    @PostMapping
    public ResponseEntity<DoctorResponse> create(@Valid @RequestBody CreateDoctorRequest req,
                                                 UriComponentsBuilder uriBuilder) {
        var created = service.create(req);
        var location = uriBuilder.path("/api/doctors/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DoctorResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    public ResponseEntity<Page<DoctorResponse>> list(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.getAll(PageRequest.of(page, size, Sort.by("id").ascending())));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DoctorResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody UpdateDoctorRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }
}