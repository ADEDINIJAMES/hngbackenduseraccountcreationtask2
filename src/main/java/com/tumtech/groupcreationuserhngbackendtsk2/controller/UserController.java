package com.tumtech.groupcreationuserhngbackendtsk2.controller;

import com.tumtech.groupcreationuserhngbackendtsk2.apiResponse.APiResponses;
import com.tumtech.groupcreationuserhngbackendtsk2.serviceImplementation.UserServiceImplementation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private UserServiceImplementation userServiceImplementation;
    public UserController (UserServiceImplementation userServiceImplementation){
        this.userServiceImplementation=userServiceImplementation;
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getUser (@PathVariable String id){
        try {

            APiResponses response = userServiceImplementation.getUserInfo(id);
            if(response.getStatusCode()==201) {
                return ResponseEntity.status(response.getStatusCode()).body(response);
            }else if(response.getStatusCode()==422){
                return ResponseEntity.status(response.getStatusCode()).body(response);
            }
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APiResponses("Internal_SERVER_ERROR", "An unexpected error occurred",500));
        }
    }
}
