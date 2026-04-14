package edu.unimagdalena.RCMU.services;

import edu.unimagdalena.RCMU.api.dto.AppointmentDtos.*;
import java.util.List;

public interface AppointmentService {
    AppointmentResponse schedule(CreateAppointmentRequest req);
    void cancel(Long id, CancelAppointmentRequest req);
    List<AppointmentResponse> findByPatientId(Long patientId);
}