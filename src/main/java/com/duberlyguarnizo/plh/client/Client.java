package com.duberlyguarnizo.plh.client;

import com.duberlyguarnizo.plh.address.Address;
import com.duberlyguarnizo.plh.auditing.AuditableEntity;
import com.duberlyguarnizo.plh.enums.PersonType;
import com.duberlyguarnizo.plh.enums.UserStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Builder
@AllArgsConstructor
@SQLDelete(sql = "UPDATE client SET deleted = true WHERE id = ?")
@Where(clause = "deleted=false")
public class Client extends AuditableEntity {
    @Id
    @GeneratedValue
    private Long id;
    @NotBlank
    @Column(unique = true)
    private String idNumber;
    @Enumerated(EnumType.STRING)
    private PersonType clientType;
    @NotBlank
    private String names;
    private String contactNames;
    private String phone;
    @Email
    private String email;
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    @ToString.Exclude
    @OneToMany
    private Set<Address> pickUpAddresses = new HashSet<>();
}
