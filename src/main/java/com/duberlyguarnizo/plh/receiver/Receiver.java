package com.duberlyguarnizo.plh.receiver;

import com.duberlyguarnizo.plh.address.Address;
import com.duberlyguarnizo.plh.auditing.AuditableEntity;
import com.duberlyguarnizo.plh.enums.PersonType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@SQLDelete(sql = "UPDATE receiver SET deleted = true WHERE id = ?")
@Where(clause = "deleted=false")
public class Receiver extends AuditableEntity {
    @Id
    @GeneratedValue
    private Long id;
    @Enumerated(EnumType.STRING)
    private PersonType type;
    @NotBlank
    private String names;
    @Column(unique = true)
    private String idNumber;
    private String phone;
    @Email
    private String email;
    private String contactNames;
    @ToString.Exclude
    @OneToMany
    private Set<Address> addresses = new HashSet<>();
}
