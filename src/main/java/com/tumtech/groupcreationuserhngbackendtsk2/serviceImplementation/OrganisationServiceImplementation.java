package com.tumtech.groupcreationuserhngbackendtsk2.serviceImplementation;

import com.tumtech.groupcreationuserhngbackendtsk2.apiResponse.APiResponses;
import com.tumtech.groupcreationuserhngbackendtsk2.dto.OrganisationCreationRequest;
import com.tumtech.groupcreationuserhngbackendtsk2.entity.Organisations;
import com.tumtech.groupcreationuserhngbackendtsk2.entity.Users;
import com.tumtech.groupcreationuserhngbackendtsk2.repostory.OrganisationRepository;
import com.tumtech.groupcreationuserhngbackendtsk2.repostory.UserRepository;
import com.tumtech.groupcreationuserhngbackendtsk2.util.UUIDValidator;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OrganisationServiceImplementation {
    private OrganisationRepository organisationRepository;
    private UserRepository userRepository;
@Autowired
    public OrganisationServiceImplementation(OrganisationRepository organisationRepositoty, UserRepository userRepository){
        this.organisationRepository= organisationRepositoty;
        this.userRepository= userRepository;
    }
public Organisations createOrganisation (Users users){
    Organisations organisations = new Organisations();
    organisations.setName(users.getFirstName()+"'s"+ " "+"Organisation");
    Set<Users> usersSet = new HashSet<>();
    usersSet.add(users);
    organisations.setUsers(usersSet);
    organisations.setDescription(users.getFirstName()+"'s"+ " "+"Organisation");
    return organisationRepository.save(organisations);
}

public List<Organisations> organisationsList(Users users){
    return organisationRepository.findByUsers(users);
}
public APiResponses getOrganisationsOfUsers () {
    try {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users users = (Users) authentication.getPrincipal();
        List<Organisations> organisations = organisationsList(users);
        Map<String, Object> data = new HashMap<>();
        data.put("organisations", organisations);
        return new APiResponses("success", "<message>", data, 200);
    }catch (Exception e){
        e.printStackTrace();
        return new APiResponses("Bad Request", "Client error",400);
    }
}
public APiResponses getOneOrganisation (String id){
try{
   Optional< Organisations >organisations = organisationRepository.findById(UUID.fromString(id));
   if(organisations.isEmpty()){
       return new APiResponses("NOT_FOUND", "Organisation not found", 404);
   }
   Map<String, Object> data = new HashMap<>();
   data.put("orgId", organisations.get().getOrgId().toString());
   data.put("name", organisations.get().getName());
   data.put("description",organisations.get().getDescription());
   return new APiResponses("success","<message>",data,200);
}catch (Exception e){
    e.printStackTrace();
    return new APiResponses("INTERNAL_SERVER_ERROR", e.getMessage(),500);
}
}
public APiResponses createOrganisationForLoggedin (OrganisationCreationRequest organisationCreationRequest) {
    try {
        if (organisationCreationRequest == null || organisationCreationRequest.getName() == null) {
            return new APiResponses("UNPROCESSABLE_ENTITY", "Organisation Name Required", 422);
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users users = (Users) authentication.getPrincipal();
        Organisations organisations = new Organisations();
        organisations.setName(organisationCreationRequest.getName());
        organisations.setDescription(organisationCreationRequest.getDescription());
        Set<Users> usersSet = new HashSet<>();
        usersSet.add(users);
        organisations.setUsers(usersSet);
        Organisations savedOrganisation = organisationRepository.save(organisations);
        Map<String, Object> data = new HashMap<>();
        data.put("orgId", savedOrganisation.getOrgId());
        data.put("name", savedOrganisation.getName());
        data.put("description", savedOrganisation.getDescription());
        return new APiResponses("success", "Organisation created successfully", data, 201);
    } catch (Exception e) {
        return new APiResponses("Bad Request", "Client error", 400);
    }
}}





//
//    public APiResponses addUserToOrganisation(String userId, String orgId) {
//        try {
//            if (!UUIDValidator.isValidUUID(userId) || !UUIDValidator.isValidUUID(orgId)) {
//                return new APiResponses("Bad Request", "Invalid UUID format", 400);
//            }
//
//            UUID userUUID = UUID.fromString(userId);
//            UUID orgUUID = UUID.fromString(orgId);
//
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            Users loggedInUser = (Users) authentication.getPrincipal();
//
//            Optional<Organisations> organisations = organisationRepository.findById(orgUUID);
//            Users users = userRepository.findById(userUUID).orElse(null);
//
//            if (organisations.isPresent()) {
//                if (organisations.get().getUsers().contains(loggedInUser)) {
//                    Set<Users> addUsers = organisations.get().getUsers();
//                    addUsers.add(users);
//                    organisationRepository.save(organisations.get());
//                    return new APiResponses("success", "User added to organisation successfully", 200);
//                } else {
//                    return new APiResponses("Forbidden", "Logged in user does not belong to the organisation", 403);
//                }
//            } else {
//                return new APiResponses("Not Found", "Organisation not found", 404);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return new APiResponses("Bad Request", e.getMessage(), 400);
//        }
//    }
//
//[POST] /api/organisations/:orgId/users : adds a user to a particular organisation
//        Request body:
//        {
//        "userId": "string"
//        }
//        Successful response: Return the payload below with a 200 success status code.
//        {
//        "status": "success",
//        "message": "User added to organisation successfully",
//        }
