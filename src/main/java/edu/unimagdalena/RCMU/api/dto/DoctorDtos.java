package edu.unimagdalena.RCMU.api.dto;

import java.io.Serializable;

public class DoctorDtos {
    public record CreateDoctorRequest(String firstName, String lastName, String email, Long specialityId) implements Serializable {}
    public record UpdateDoctorRequest(String firstName, String lastName, Boolean isActive) implements Serializable {}
    public record DoctorResponse(Long id, String firstName, String lastName, String email, String specialityName, Boolean isActive) implements Serializable {}
}