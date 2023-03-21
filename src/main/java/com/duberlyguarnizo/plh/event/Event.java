package com.duberlyguarnizo.plh.event;

import com.duberlyguarnizo.plh.auditing.AuditableEntity;
import com.duberlyguarnizo.plh.enums.EntityType;
import com.duberlyguarnizo.plh.enums.EventType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event extends AuditableEntity {
    @Id
    @GeneratedValue
    private Long id;
    private Long entityId; //The id of the affected entity... e.g.: if you create a client, the id of the client
    @Enumerated(value = EnumType.STRING)
    private EntityType entityType; //the type (class) of the entity affected
    @Enumerated(value = EnumType.STRING)
    private EventType eventType;
    private String referer; //Aditional data (i.e.: names of deleted client) to pass to event queue
}
