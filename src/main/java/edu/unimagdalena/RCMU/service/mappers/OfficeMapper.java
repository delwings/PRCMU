package edu.unimagdalena.RCMU.service.mappers;

import edu.unimagdalena.RCMU.api.dto.OfficeDtos.*;
import edu.unimagdalena.RCMU.domine.entity.Office;
import edu.unimagdalena.RCMU.domine.enums.OfficeStatus;

public class OfficeMapper {
    public static Office toEntity(CreateOfficeRequest req) {
        return Office.builder()
                .roomNumber(req.roomNumber())
                .location(req.location())
                .status(OfficeStatus.AVAILABLE)
                .build();
    }

    public static OfficeResponse toResponse(Office o) {
        return new OfficeResponse(o.getId(), o.getRoomNumber(), o.getLocation(), o.getStatus());
    }

    public static void patch(Office entity, UpdateOfficeRequest req) {
        if (req.status() != null) entity.setStatus(req.status());
    }
}