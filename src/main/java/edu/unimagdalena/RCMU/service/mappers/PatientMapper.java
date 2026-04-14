package edu.unimagdalena.RCMU.services.mapper;

import edu.unimagdalena.RCMU.api.dto.PatientDtos.*;
import edu.unimagdalena.RCMU.domine.entity.Patient;
import edu.unimagdalena.RCMU.domine.enums.PatientStatus;

public class PatientMapper {
    public static Patient toEntity(CreatePatientRequest req) {
        return Patient.builder()
                .documentId(req.documentId())
                .firstName(req.firstName())
                .lastName(req.lastName())
                .email(req.email())
                .status(PatientStatus.ACTIVE)
                .build();
    }

    public static PatientResponse toResponse(Patient p) {
        return new PatientResponse(p.getId(), p.documentId(), p.getFirstName(),
                p.getLastName(), p.getEmail(), p.getStatus());
    }

    public static void patch(Patient entity, UpdatePatientRequest req) {
        if (req.firstName() != null) entity.setFirstName(req.firstName());
        if (req.lastName() != null) entity.setLastName(req.lastName());
        if (req.status() != null) entity.setStatus(req.status());
    }
}