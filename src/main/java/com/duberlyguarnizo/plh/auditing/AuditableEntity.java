package com.duberlyguarnizo.plh.auditing;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntity {
    protected boolean deleted = false;
    @CreatedBy
    @Column(updatable = false)
    protected Long createdBy;

    @CreationTimestamp
    @Column(updatable = false)
    protected LocalDateTime createdDate;

    @LastModifiedBy
    @Column(columnDefinition = "bigint default 1")
    protected Long lastModifiedBy;

    @UpdateTimestamp
    protected LocalDateTime lastModifiedDate;
    private String notes;
}
