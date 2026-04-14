package edu.unimagdalena.RCMU.api.dto;

import java.io.Serializable;

public class SpecialityDtos {
    public record CreateSpecialityRequest(String name) implements Serializable {}
    public record SpecialityResponse(Long id, String name) implements Serializable {}
}