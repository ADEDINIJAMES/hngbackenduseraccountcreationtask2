package com.tumtech.groupcreationuserhngbackendtsk2.apiResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class APiResponses {
    private String status;
    private String message;
    private Map<String, Object> data;
    private int statusCode;

    public APiResponses(String status, String message, int statusCode) {
        this.status = status;
        this.message = message;
        this.statusCode = statusCode;
    }
}
