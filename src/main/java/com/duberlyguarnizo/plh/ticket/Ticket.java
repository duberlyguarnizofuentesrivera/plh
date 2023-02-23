package com.duberlyguarnizo.plh.ticket;

import com.duberlyguarnizo.plh.auditing.AuditableEntity;
import com.duberlyguarnizo.plh.client.Client;
import com.duberlyguarnizo.plh.enums.TicketPaymentStatus;
import com.duberlyguarnizo.plh.enums.TicketStatus;
import com.duberlyguarnizo.plh.shipment.Shipment;
import com.duberlyguarnizo.plh.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Ticket extends AuditableEntity {
    @Id
    @GeneratedValue
    private Long id;
    private String code;
    @OneToMany
    @ToString.Exclude
    private List<Shipment> shipments = new ArrayList<>();
    private double totalCost;
    @ToString.Exclude
    @ManyToOne
    private Client client;
    @ToString.Exclude
    @ManyToOne
    @CreatedBy
    private User user;
    @Enumerated(EnumType.STRING)
    private TicketStatus status;
    @Enumerated(EnumType.STRING)
    private TicketPaymentStatus paymentStatus;
}
