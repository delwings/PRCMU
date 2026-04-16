package edu.unimagdalena.RCMU.service.mappers;

import edu.unimagdalena.RCMU.api.dto.SpecialityDtos.*;
import edu.unimagdalena.RCMU.domine.entity.Speciality;

public class SpecialityMapper {

    public static Speciality toEntity(CreateSpecialityRequest req) {
        return Speciality.builder()
                .name(req.name())
                .build();
    }

    public static SpecialityResponse toResponse(Speciality s) {
        if (s == null) return null;
        return new SpecialityResponse(
                s.getId(),
                s.getName()
        );
    }

    public static void patch(Speciality entity, CreateSpecialityRequest req) {
        if (req.name() != null) entity.setName(req.name());
    }
}