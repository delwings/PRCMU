package edu.unimagdalena.RCMU.api;

import edu.unimagdalena.RCMU.api.dto.OfficeDtos.*;
import edu.unimagdalena.RCMU.service.OfficeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/offices")
@RequiredArgsConstructor
@Validated
public class OfficeController {

    private final OfficeService service;

    @PostMapping
    public ResponseEntity<OfficeResponse> create(@Valid @RequestBody CreateOfficeRequest req,
                                                 UriComponentsBuilder uriBuilder) {
        var created = service.create(req);
        var location = uriBuilder.path("/api/offices/{id}").buildAndExpand(created.id()).toUri();
        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public ResponseEntity<List<OfficeResponse>> list() {
        return ResponseEntity.ok(service.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<OfficeResponse> update(@PathVariable Long id,
                                                 @Valid @RequestBody UpdateOfficeRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }
}