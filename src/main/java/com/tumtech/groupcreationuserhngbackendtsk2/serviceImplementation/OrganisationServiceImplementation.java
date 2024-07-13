package com.tumtech.groupcreationuserhngbackendtsk2.serviceImplementation;

import com.tumtech.groupcreationuserhngbackendtsk2.apiResponse.APiResponses;
import com.tumtech.groupcreationuserhngbackendtsk2.dto.AddUserToOrganisationRequest;
import com.tumtech.groupcreationuserhngbackendtsk2.dto.OrganisationCreationRequest;
import com.tumtech.groupcreationuserhngbackendtsk2.entity.Organisations;
import com.tumtech.groupcreationuserhngbackendtsk2.entity.Users;
import com.tumtech.groupcreationuserhngbackendtsk2.exception.UserNameNotFoundException;
import com.tumtech.groupcreationuserhngbackendtsk2.repostory.OrganisationRepository;
import com.tumtech.groupcreationuserhngbackendtsk2.repostory.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

public Set<Organisations> organisationsList(Users users){
    return organisationRepository.findByUsers(users);
}
public APiResponses getOrganisationsOfUsers () {
    try {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users users = (Users) authentication.getPrincipal();
        Set<Organisations> organisations = organisationsList(users);
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
   Optional< Organisations >organisations = organisationRepository.findById(id);
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
    public APiResponses createOrganisationForLoggedInUser(OrganisationCreationRequest organisationCreationRequest) {
        try {
            if (organisationCreationRequest == null || organisationCreationRequest.getName() == null) {
                return new APiResponses("UNPROCESSABLE_ENTITY", "Organisation Name Required", 422);
            }

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            Users user = (Users) authentication.getPrincipal();

            Organisations organisation = new Organisations();
            organisation.setName(organisationCreationRequest.getName());
            organisation.setDescription(organisationCreationRequest.getDescription());

            Organisations savedOrganisation = organisationRepository.save(organisation);
Set<Users> usersSet  = savedOrganisation.getUsers();
usersSet.add(user);

Set<Organisations> organisationsSet = user.getOrganisations();
organisationsSet.add(organisation);
userRepository.save(user);
organisationRepository.save(savedOrganisation);

            Map<String, Object> data = new HashMap<>();
            data.put("orgId", savedOrganisation.getOrgId());
            data.put("name", savedOrganisation.getName());
            data.put("description", savedOrganisation.getDescription());

            return new APiResponses("success", "Organisation created successfully", data, 201);
        } catch (Exception e) {
            e.printStackTrace();
            return new APiResponses("Bad Request", "Client error", 400);
        }
    }



public APiResponses addUserToOrganisation (AddUserToOrganisationRequest userId, String orgId) {
    try {


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Users loggedInUser = (Users) authentication.getPrincipal();

        Organisations organisations = organisationRepository.findById(orgId).orElseThrow(()-> new UserNameNotFoundException("Organisation not found"));
        Users users = userRepository.findById(userId.getUserId()).orElseThrow(()-> new UserNameNotFoundException("user not found"));
        Set<Users> addUsers = organisations.getUsers();

        if (addUsers.contains(loggedInUser)) {
            return new APiResponses("bad request", "Logged in user is not part of the organisation",400);

            }
        addUsers.add(users);
      Organisations saved = organisationRepository.save(organisations);

      Set<Organisations> organisationsSet= users.getOrganisations();
      organisationsSet.add(saved);
      userRepository.save(users);
      return new APiResponses("success", "user added to organisation successfully", 200);

    }catch (Exception e){
        e.printStackTrace();
        return new APiResponses ("Bad Request", e.getMessage(),400);
    }

}


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

}
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
