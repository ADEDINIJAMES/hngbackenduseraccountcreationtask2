package com.tumtech.groupcreationuserhngbackendtsk2.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Table(name = "organisations")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Organisations {
    @Id
    @Column(unique = true)
    @GeneratedValue(strategy = GenerationType.AUTO)    private String orgId;
    @NotNull
    private String name;
    private String description;
    @ManyToMany

    private Set<Users> users = new HashSet<>();

}
