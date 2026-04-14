package edu.unimagdalena.RCMU.services.mapper;

import edu.unimagdalena.RCMU.api.dto.DoctorDtos.*;
import edu.unimagdalena.RCMU.domine.entity.Doctor;

public class DoctorMapper {
    public static Doctor toEntity(CreateDoctorRequest req) {
        return Doctor.builder()
                .firstName(req.firstName())
                .lastName(req.lastName())
                .email(req.email())
                .isActive(true)
                .build();
    }

    public static DoctorResponse toResponse(Doctor d) {
        var specName = d.getSpeciality() != null ? d.getSpeciality().getName() : null;
        return new DoctorResponse(d.getId(), d.getFirstName(), d.getLastName(),
                d.getEmail(), specName, d.getIsActive());
    }

    public static void patch(Doctor entity, UpdateDoctorRequest req) {
        if (req.firstName() != null) entity.setFirstName(req.firstName());
        if (req.lastName() != null) entity.setLastName(req.lastName());
        if (req.isActive() != null) entity.setIsActive(req.isActive());
    }
}