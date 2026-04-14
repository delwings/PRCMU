package edu.unimagdalena.RCMU.services.mapper;

import edu.unimagdalena.RCMU.api.dto.DoctorScheduleDtos.*;
import edu.unimagdalena.RCMU.domine.entity.DoctorSchedule;

public class DoctorScheduleMapper {
    public static DoctorSchedule toEntity(CreateDoctorScheduleRequest req) {
        return DoctorSchedule.builder()
                .dayOfWeek(req.dayOfWeek())
                .startTime(req.startTime())
                .endTime(req.endTime())
                .active(true)
                .build();
    }

    public static DoctorScheduleResponse toResponse(DoctorSchedule s) {
        return new DoctorScheduleResponse(s.getId(), s.getDayOfWeek(),
                s.getStartTime(), s.getEndTime(), s.getActive());
    }
}