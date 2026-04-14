package edu.unimagdalena.RCMU.services.mapper;

import edu.unimagdalena.RCMU.api.dto.AppointmentTypeDtos.*;
import edu.unimagdalena.RCMU.domine.entity.AppointmentType;

public class AppointmentTypeMapper {

    public static AppointmentType toEntity(CreateAppointmentTypeRequest req) {
        return AppointmentType.builder()
                .name(req.name())
                .duration(req.duration())
                .build();
    }

    public static AppointmentTypeResponse toResponse(AppointmentType type) {
        if (type == null) return null;
        return new AppointmentTypeResponse(
                type.getId(),
                type.getName(),
                type.getDuration()
        );
    }

    public static void patch(AppointmentType entity, CreateAppointmentTypeRequest req) {
        if (req.name() != null) entity.setName(req.name());
        if (req.duration() != null) entity.setDuration(req.duration());
    }
}