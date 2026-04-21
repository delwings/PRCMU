package edu.unimagdalena.RCMU.service;

import edu.unimagdalena.RCMU.api.dto.AppointmentDtos.*;
import java.util.List;

public interface AppointmentService {
    AppointmentResponse schedule(CreateAppointmentRequest req);
    AppointmentResponse confirm(Long id);
    AppointmentResponse complete(Long id, String observations);
    AppointmentResponse markAsNoShow(Long id);
    void cancel(Long id, CancelAppointmentRequest req);
    List<AppointmentResponse> getAll();
    List<AppointmentResponse> findByPatientId(Long patientId);
}