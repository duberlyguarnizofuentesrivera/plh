package com.duberlyguarnizo.plh.receiver;

import com.duberlyguarnizo.plh.address.Address;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
public class Receiver {
    @Id
    @GeneratedValue
    private Long id;
    private boolean isCompany;
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
    @CreationTimestamp
    private LocalDateTime creationDate;
    @UpdateTimestamp
    private LocalDateTime modificationDate;
}
