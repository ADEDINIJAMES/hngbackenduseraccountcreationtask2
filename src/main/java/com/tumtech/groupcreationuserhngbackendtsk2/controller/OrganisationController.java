package com.tumtech.groupcreationuserhngbackendtsk2.controller;

import com.tumtech.groupcreationuserhngbackendtsk2.apiResponse.APiResponses;
import com.tumtech.groupcreationuserhngbackendtsk2.dto.AddUserToOrganisationRequest;
import com.tumtech.groupcreationuserhngbackendtsk2.dto.OrganisationCreationRequest;
import com.tumtech.groupcreationuserhngbackendtsk2.serviceImplementation.OrganisationServiceImplementation;
import com.tumtech.groupcreationuserhngbackendtsk2.serviceImplementation.UserServiceImplementation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organisations")
public class OrganisationController {
    private UserServiceImplementation userServiceImplementation;
    private OrganisationServiceImplementation organisationServiceImplementation;

    public OrganisationController(UserServiceImplementation userServiceImplementation, OrganisationServiceImplementation organisationServiceImplementation) {
        this.userServiceImplementation = userServiceImplementation;
        this.organisationServiceImplementation = organisationServiceImplementation;
    }

    @GetMapping("/")
    public ResponseEntity<?> getOrganisationOfUser() {
        try {

            APiResponses response = organisationServiceImplementation.getOrganisationsOfUsers();
            if (response.getStatusCode() == 201) {
                return ResponseEntity.status(response.getStatusCode()).body(response);
            } else if (response.getStatusCode() == 422) {
                return ResponseEntity.status(response.getStatusCode()).body(response);
            }
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new APiResponses("Bad Request", "Client error", 400));
        }
    }
@GetMapping("/{id}")
    public ResponseEntity<?> getOneOrganisation(@PathVariable String id) {
        try {

            APiResponses response = organisationServiceImplementation.getOneOrganisation(id);
            if (response.getStatusCode() == 201) {
                return ResponseEntity.status(response.getStatusCode()).body(response);
            } else if (response.getStatusCode() == 422) {
                return ResponseEntity.status(response.getStatusCode()).body(response);
            }
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new APiResponses("Internal_SERVER_ERROR", e.getMessage(), 500));
        }
    }


    @PostMapping("/")
    public ResponseEntity<?> createOrganisation(@Valid @RequestBody OrganisationCreationRequest request) {
        try {

            APiResponses response = organisationServiceImplementation.createOrganisationForLoggedin(request);
            if (response.getStatusCode() == 201) {
                return ResponseEntity.status(response.getStatusCode()).body(response);
            } else if (response.getStatusCode() == 422) {
                return ResponseEntity.status(response.getStatusCode()).body(response);
            }
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            return ResponseEntity.status(400).body(new APiResponses("Bad Request", "Client error", 400));
        }
    }

    @PostMapping("/{orgId}/user")
    public ResponseEntity<?> createOrganisation( @PathVariable  String orgId, @Valid @RequestBody AddUserToOrganisationRequest userId) {
        try {

            APiResponses response = organisationServiceImplementation.addUserToOrganisation(userId,orgId);
            if (response.getStatusCode() == 201) {
                return ResponseEntity.status(response.getStatusCode()).body(response);
            } else if (response.getStatusCode() == 422) {
                return ResponseEntity.status(response.getStatusCode()).body(response);
            }
            return ResponseEntity.status(response.getStatusCode()).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(400).body(new APiResponses("Bad Request", "Client error", 400));
        }
    }
}

