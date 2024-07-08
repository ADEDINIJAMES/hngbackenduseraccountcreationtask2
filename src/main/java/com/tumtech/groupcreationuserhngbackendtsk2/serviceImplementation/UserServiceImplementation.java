package com.tumtech.groupcreationuserhngbackendtsk2.serviceImplementation;

import com.tumtech.groupcreationuserhngbackendtsk2.apiResponse.APiResponses;
import com.tumtech.groupcreationuserhngbackendtsk2.apiResponse.UserRequestResponse;
import com.tumtech.groupcreationuserhngbackendtsk2.dto.RegisterRequest;
import com.tumtech.groupcreationuserhngbackendtsk2.entity.Organisations;
import com.tumtech.groupcreationuserhngbackendtsk2.entity.Users;
import com.tumtech.groupcreationuserhngbackendtsk2.repostory.UserRepository;
import com.tumtech.groupcreationuserhngbackendtsk2.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserServiceImplementation implements UserDetailsService {
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private JwtUtil jwtUtil;
    private OrganisationServiceImplementation organisationServiceImplementation;
    @Autowired
    public UserServiceImplementation (UserRepository userRepository, OrganisationServiceImplementation organisationServiceImplementation, JwtUtil jwtUtil, PasswordEncoder passwordEncoder){
        this.userRepository= userRepository;
        this.passwordEncoder= passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.organisationServiceImplementation =organisationServiceImplementation;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
return (UserDetails) userRepository.findByEmail(username).orElseThrow(()-> new UsernameNotFoundException("user not found"));
    }
    public APiResponses registerUser ( @Valid RegisterRequest request) {
        try {
            Optional<Users> users = userRepository.findByEmail(request.getEmail());
            if (users.isPresent()) {
                return new APiResponses("Bad Request", "You already Registered", 400);
            }
            Users users1 = new Users();
            users1.setFirstName(request.getFirstName());
            users1.setLastName(request.getLastName());
            users1.setPhone(request.getPhone());
            users1.setEmail(request.getEmail());
            users1.setPassword(request.getPassword());
            Users savedUser = userRepository.save(users1);
            Organisations organisations= organisationServiceImplementation.createOrganisation(savedUser);
            Set<Organisations> organisationsSet = new HashSet<>();
            organisationsSet.add(organisations);
          savedUser.setOrganisations(organisationsSet);
          userRepository.save(savedUser);
           String accessToken = jwtUtil.createJwt.apply((UserDetails) savedUser);
            Map<String, Object> data = generateData(savedUser, accessToken);

            return new APiResponses("success","Registration Successful", data.toString(),201);

        }catch (Exception e){
            e.printStackTrace();
            return new APiResponses("Bad Request","Registration unsuccessful",400);


        }
    }

    private static Map<String, Object> generateData(Users savedUser, String accessToken) {
        UserRequestResponse userRequestResponse = new UserRequestResponse();
        userRequestResponse.setEmail(savedUser.getEmail());
        userRequestResponse.setPhone(savedUser.getPhone());
        userRequestResponse.setLastName(savedUser.getLastName());
        userRequestResponse.setUserId(String.valueOf(savedUser.getUserid()));
        userRequestResponse.setFirstName(savedUser.getFirstName());
        Map<String ,Object> data = new HashMap<>();
        data.put("accessToken", accessToken);
        data.put("user",userRequestResponse);
        return data;
    }


}
