package edu.unimagdalena.RCMU.api.dto;

import java.io.Serializable;
import edu.unimagdalena.RCMU.domine.enums.OfficeStatus;

public class OfficeDtos {
    public record CreateOfficeRequest(String roomNumber, Integer floor) implements Serializable {}
    public record UpdateOfficeRequest(OfficeStatus status) implements Serializable {}
    public record OfficeResponse(Long id, String roomNumber, Integer floor, OfficeStatus status) implements Serializable {}
    public record OfficeOccupancyResponse(String roomNumber, Long totalAppointments, Double occupancyPercentage) implements Serializable {}
}