package edu.unimagdalena.RCMU.api;

import edu.unimagdalena.RCMU.api.dto.SpecialityDtos.*;
import edu.unimagdalena.RCMU.service.SpecialityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/specialties")
@RequiredArgsConstructor
@Validated
public class SpecialityController {

    private final SpecialityService service;

    @PostMapping
    public ResponseEntity<SpecialityResponse> create(@Valid @RequestBody CreateSpecialityRequest req,
                                                    UriComponentsBuilder uriBuilder) {
        var created = service.create(req);
        var location = uriBuilder.path("/api/specialties/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<SpecialityResponse>> list() {
        return ResponseEntity.ok(service.findAll());
    }
}