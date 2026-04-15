package edu.unimagdalena.RCMU.domine.entity;

import edu.unimagdalena.RCMU.domine.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateTime; // Hora de inicio

    @Column(nullable = false)
    private LocalDateTime endAt;    // NUEVO: Hora de fin (calculada por el service)

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private String cancelReason;    // NUEVO: Motivo de cancelación obligatorio
    private String observations;    // NUEVO: Observaciones al finalizar

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;

    @ManyToOne
    @JoinColumn(name = "appointment_type_id", nullable = false)
    private AppointmentType appointmentType;
}