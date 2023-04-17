package com.duberlyguarnizo.plh.shipment;

import com.duberlyguarnizo.plh.address.Address;
import com.duberlyguarnizo.plh.auditing.AuditableEntity;
import com.duberlyguarnizo.plh.client.Client;
import com.duberlyguarnizo.plh.enums.ShipmentProblem;
import com.duberlyguarnizo.plh.enums.ShipmentStatus;
import com.duberlyguarnizo.plh.receiver.Receiver;
import com.duberlyguarnizo.plh.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE shipment SET deleted = true WHERE id = ?")
@Where(clause = "deleted=false")
public class Shipment extends AuditableEntity {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    private String code;
    @Min(1)
    private int numberOfPackages;
    private LocalDateTime receptionDate;
    private LocalDateTime onRouteDate;
    private LocalDateTime onReturnDate;
    private LocalDateTime returnDate;
    private LocalDateTime deliveryDate;
    @ToString.Exclude
    @ManyToOne
    private Client client;
    @ToString.Exclude
    @ManyToOne
    private Address address;
    @ToString.Exclude
    @ManyToOne
    private Receiver receiver;
    @ToString.Exclude
    @ManyToOne
    private User deliveryTransporter;
    @ToString.Exclude
    @ManyToOne
    private User pickUpTransporter;
    @ToString.Exclude
    @ManyToOne
    private User receiverUser;
    @Positive
    private double cost;
    @ElementCollection
    private @NotNull Set<String> photoUrls = new HashSet<>();
    private String observations;
    @Enumerated(EnumType.STRING)
    private ShipmentProblem problems;
    @Enumerated(EnumType.STRING)
    private ShipmentStatus status;
}
