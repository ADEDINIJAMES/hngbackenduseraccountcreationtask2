package com.tumtech.groupcreationuserhngbackendtsk2.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tumtech.groupcreationuserhngbackendtsk2.util.IdGenerator;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Table(name = "organisations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Organisations {
    @Id
    @Column(unique = true)
    private String orgId;
    @NotNull
    private String name;
    private String description;
    @JsonIgnore
    @ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(
            name = "organisations_users",
            joinColumns = @JoinColumn(name = "org_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<Users> users = new HashSet<>();
    @PrePersist
    public void prePersist() {
        if (orgId == null) {
            orgId = IdGenerator.generateNanoId(10);
        }
    }

}
