package edu.unimagdalena.RCMU.service.mappers;

import edu.unimagdalena.RCMU.api.dto.AppointmentTypeDtos.*;
import edu.unimagdalena.RCMU.domine.entity.AppointmentType;

public class AppointmentTypeMapper {

    public static AppointmentType toEntity(CreateAppointmentTypeRequest req) {
        return AppointmentType.builder()
                .name(req.name())
                .durationInMinutes(req.duration())
                .build();
    }

    public static AppointmentTypeResponse toResponse(AppointmentType type) {
        if (type == null) return null;
        return new AppointmentTypeResponse(
                type.getId(),
                type.getName(),
                type.getDurationInMinutes()
        );
    }

    public static void patch(AppointmentType entity, CreateAppointmentTypeRequest req) {
        if (req.name() != null) entity.setName(req.name());
        if (req.duration() != null) entity.setDurationInMinutes(req.duration());
    }
}