package com.tumtech.groupcreationuserhngbackendtsk2.dto;

import jdk.jfr.Name;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddUserToOrganisationRequest {
    private String userId;
}
