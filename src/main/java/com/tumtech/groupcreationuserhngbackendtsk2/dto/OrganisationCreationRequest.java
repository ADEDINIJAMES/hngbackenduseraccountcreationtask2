package com.tumtech.groupcreationuserhngbackendtsk2.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganisationCreationRequest {
    @NotNull(message = "organisation name required")
    private String name;
    private String description;
}
