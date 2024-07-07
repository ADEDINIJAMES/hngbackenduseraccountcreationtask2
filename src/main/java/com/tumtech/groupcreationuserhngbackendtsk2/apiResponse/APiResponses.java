package com.tumtech.groupcreationuserhngbackendtsk2.apiResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class APiResponses {
    private String status;
    private String message;
    private String data;
    private int statusCode;
}
