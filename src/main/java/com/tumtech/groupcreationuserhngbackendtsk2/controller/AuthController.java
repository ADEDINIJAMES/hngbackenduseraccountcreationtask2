package com.tumtech.groupcreationuserhngbackendtsk2.controller;

import com.tumtech.groupcreationuserhngbackendtsk2.apiResponse.APiResponses;
import com.tumtech.groupcreationuserhngbackendtsk2.dto.LoginRequest;
import com.tumtech.groupcreationuserhngbackendtsk2.dto.RegisterRequest;
import com.tumtech.groupcreationuserhngbackendtsk2.exception.UserNameNotFoundException;
import com.tumtech.groupcreationuserhngbackendtsk2.serviceImplementation.UserServiceImplementation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private UserServiceImplementation userServiceImplementation;

    public AuthController(UserServiceImplementation userServiceImplementation) {
        this.userServiceImplementation = userServiceImplementation;
    }
@PostMapping("/register")
public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest request) {
        try {

                    APiResponses response = userServiceImplementation.registerUser(request);
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

    @PostMapping("/login")
    public ResponseEntity<?> login (@Valid @RequestBody LoginRequest request) throws UserNameNotFoundException {

        APiResponses responses = userServiceImplementation.login(request);
        return ResponseEntity.status(responses.getStatusCode()).body(responses);
    }
}