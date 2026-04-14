package edu.unimagdalena.RCMU.services.mapper;

import edu.unimagdalena.RCMU.api.dto.AppointmentDtos.*;
import edu.unimagdalena.RCMU.domine.entity.Appointment;

public class AppointmentMapper {
    public static AppointmentResponse toResponse(Appointment a) {
        var patientName = a.getPatient() != null ?
                a.getPatient().getFirstName() + " " + a.getPatient().getLastName() : null;
        var doctorName = a.getDoctor() != null ?
                "Dr. " + a.getDoctor().getLastName() : null;

        return new AppointmentResponse(a.getId(), a.getDateTime(), patientName,
                doctorName, a.getStatus());
    }
}