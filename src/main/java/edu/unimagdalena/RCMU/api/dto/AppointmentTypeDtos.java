package edu.unimagdalena.RCMU.api.dto;

import java.io.Serializable;

public class AppointmentTypeDtos {
    public record CreateAppointmentTypeRequest(String name, Integer duration) implements Serializable {}
    public record AppointmentTypeResponse(Long id, String name, Integer duration) implements Serializable {}
}