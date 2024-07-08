package com.tumtech.groupcreationuserhngbackendtsk2.controller;

import com.tumtech.groupcreationuserhngbackendtsk2.dto.RegisterRequest;
import com.tumtech.groupcreationuserhngbackendtsk2.serviceImplementation.UserServiceImplementation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class UserController {
    private UserServiceImplementation userServiceImplementation;

    public UserController(UserServiceImplementation userServiceImplementation) {
        this.userServiceImplementation = userServiceImplementation;
    }
@PostMapping("/register")
public ResponseEntity<?> registerUser(@Valid RegisterRequest request) {
        try {
            return ResponseEntity.ok(userServiceImplementation.registerUser(request));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(userServiceImplementation.registerUser(request));
        }
    }
}