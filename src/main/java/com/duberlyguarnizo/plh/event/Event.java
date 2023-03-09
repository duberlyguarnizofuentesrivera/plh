package com.duberlyguarnizo.plh.event;

import com.duberlyguarnizo.plh.auditing.AuditableEntity;
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
    private Long userId;
    private String referer;
    @Enumerated(value = EnumType.STRING)
    private EventType eventType;
}
