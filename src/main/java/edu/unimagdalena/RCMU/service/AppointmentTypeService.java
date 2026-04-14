package edu.unimagdalena.RCMU.services;

import edu.unimagdalena.RCMU.api.dto.AppointmentTypeDtos.*;
import java.util.List;

public interface AppointmentTypeService {
    AppointmentTypeResponse create(CreateAppointmentTypeRequest req);
    List<AppointmentTypeResponse> findAll();
}