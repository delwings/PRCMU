package edu.unimagdalena.RCMU.api.dto;

import java.io.Serializable;
import edu.unimagdalena.RCMU.domine.enums.PatientStatus;

public class PatientDtos {
    public record CreatePatientRequest(String documentId, String firstName, String lastName, String email) implements Serializable {}
    public record UpdatePatientRequest(String firstName, String lastName, PatientStatus status) implements Serializable {}
    public record PatientResponse(Long id, String documentId, String firstName, String lastName, String email, PatientStatus status) implements Serializable {}
}