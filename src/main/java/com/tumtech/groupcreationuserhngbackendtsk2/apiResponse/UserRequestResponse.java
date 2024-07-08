package com.tumtech.groupcreationuserhngbackendtsk2.apiResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRequestResponse {
    private String userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
}
