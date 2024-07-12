package com.tumtech.groupcreationuserhngbackendtsk2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tumtech.groupcreationuserhngbackendtsk2.util.IdGenerator;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Users implements UserDetails{
    @Id
    @Column(unique = true)
    private String userid;

    @NotNull(message = "firstName is required")
    private String firstName;
    @NotNull(message = "last name is required")
    private String lastName;
    @Column(unique = true)
    @Email(message = "email must be valid")
    @NotNull(message = "email is required")
    private String email;
    @NotNull(message = "password is required")
    private String password;
    private String phone;
    @JsonIgnore
    @ManyToMany(mappedBy = "users", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
   private Set<Organisations> organisations = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>();
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @PrePersist
    public void prePersist() {
        if (userid == null) {
            userid = IdGenerator.generateNanoId(10); // Specify the length of the NanoId here
        }
    }
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }

}
