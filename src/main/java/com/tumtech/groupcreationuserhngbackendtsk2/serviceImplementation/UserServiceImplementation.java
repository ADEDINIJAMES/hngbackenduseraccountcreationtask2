package com.tumtech.groupcreationuserhngbackendtsk2.serviceImplementation;

import com.tumtech.groupcreationuserhngbackendtsk2.apiResponse.APiResponses;
import com.tumtech.groupcreationuserhngbackendtsk2.apiResponse.UserRequestResponse;
import com.tumtech.groupcreationuserhngbackendtsk2.dto.LoginRequest;
import com.tumtech.groupcreationuserhngbackendtsk2.dto.RegisterRequest;
import com.tumtech.groupcreationuserhngbackendtsk2.entity.Organisations;
import com.tumtech.groupcreationuserhngbackendtsk2.entity.Users;
import com.tumtech.groupcreationuserhngbackendtsk2.exception.UserNameNotFoundException;
import com.tumtech.groupcreationuserhngbackendtsk2.repostory.UserRepository;
import com.tumtech.groupcreationuserhngbackendtsk2.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public UserServiceImplementation() {

    }

    @Transactional
    public void deleteUser(String userId) {
        Users user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        // Remove the associations from the join table
        for (Organisations organisation : user.getOrganisations()) {
            organisation.getUsers().remove(user);
        }
        user.getOrganisations().clear();

        // Now delete the user
        userRepository.delete(user);
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
return userRepository.findByEmail(username).orElseThrow(()-> new UserNameNotFoundException("user not found"));
    }
    public APiResponses registerUser (RegisterRequest request) {
        try {
        List<Map<String, String>> errors = validateRequest(request);
        if (!errors.isEmpty()) {
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("errors", errors);
            return new APiResponses("Validation Error", "Invalid input", errorData, 422);
        }

                Optional<Users> exists = userRepository.findByEmail(request.getEmail());
                if (exists.isPresent()) {
                    return new APiResponses("Validation Error", "Email already exists", 422);
                }
                Users users1 = new Users();
                users1.setFirstName(request.getFirstName());
                users1.setLastName(request.getLastName());
                users1.setPhone(request.getPhone());
                users1.setEmail(request.getEmail());
                users1.setPassword(passwordEncoder.encode(request.getPassword()));
                Users savedUser = userRepository.save(users1);
            Organisations organisations= organisationServiceImplementation.createOrganisation(savedUser);
            Set<Organisations> organisationsSet = new HashSet<>();
            organisationsSet.add(organisations);
          savedUser.setOrganisations(organisationsSet);
          userRepository.save(savedUser);
                String accessToken = jwtUtil.createJwt.apply(savedUser);
                Map<String, Object> data = generateData(savedUser, accessToken);

                return new APiResponses("success", "Registration Successful", data, 201);

            } catch (Exception e) {
                e.printStackTrace();
                return new APiResponses("Bad Request", "Registration unsuccessful", 400);
            }

    }

    private List<Map<String, String>> validateRequest(RegisterRequest request) {
        List<Map<String, String>> errors = new ArrayList<>();

        // Validate first name
        if (request.getFirstName() == null || request.getFirstName().isEmpty()) {
            errors.add(createError("firstName", "First name is required"));
        } else if (request.getFirstName().length() < 2 || request.getFirstName().length() > 50) {
            errors.add(createError("firstName", "First name must be between 2 and 50 characters"));
        }

        // Validate last name
        if (request.getLastName() == null || request.getLastName().isEmpty()) {
            errors.add(createError("lastName", "Last name is required"));
        } else if (request.getLastName().length() < 2 || request.getLastName().length() > 50) {
            errors.add(createError("lastName", "Last name must be between 2 and 50 characters"));
        }

        // Validate email
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            errors.add(createError("email", "Email is required"));
        } else if (!isValidEmail(request.getEmail())) {
            errors.add(createError("email", "Email format is invalid"));
        }

        // Validate password
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            errors.add(createError("password", "Password is required"));
        } else if (!isStrongPassword(request.getPassword())) {
            errors.add(createError("password", "Password must be at least 8 characters, include at least one uppercase letter, one lowercase letter, one number, and one special character"));
        }

        // Validate phone (optional)
        if (request.getPhone() != null && !request.getPhone().isEmpty() && !isValidPhoneNumber(request.getPhone())) {
            errors.add(createError("phone", "Phone number format is invalid"));
        }

        return errors;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isStrongPassword(String password) {
        // Password must be at least 8 characters, include at least one uppercase letter, one lowercase letter, one number, and one special character
        String passwordRegex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    private boolean isValidPhoneNumber(String phone) {
        // Validate phone number format (simple example, adjust as needed)
        String phoneRegex = "^[0-9]{10,15}$";
        Pattern pattern = Pattern.compile(phoneRegex);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    private Map<String, String> createError(String field, String message) {
        Map<String, String> error = new HashMap<>();
        error.put("field", field);
        error.put("message", message);
        return error;
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

    public APiResponses login (LoginRequest request) throws UserNameNotFoundException {
        try {
            if (request != null) {
                Users users = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new UserNameNotFoundException("User not found"));
                if (users != null) {
                    if (!passwordEncoder.matches(request.getPassword(), users.getPassword())) {
                        return new APiResponses("Bad Request", "Authentication failed", 401);
                    }
                    String accessToken = jwtUtil.createJwt.apply(users);

                    return new APiResponses("success", "Login successful", generateData(users, accessToken), 200);

                }

            }

            return new APiResponses("Bad Request", "Authentication failed",401);


        }catch (Exception e){
            e.printStackTrace();
            return new APiResponses("Bad Request", "Authentication failed", 401);
        }
    }


/*
* get user
* requirement: get their own detail with their own id. This means they have to be first logged in. Then their info cn now be gotten.
* steps
* check if the security Auth; for the logged-in user information
* */
public APiResponses getUserInfo(String id) {
    try {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return new APiResponses("UNAUTHORIZED", "You are not logged in", 401);
        }

        Users requestedUser = userRepository.findById(id).orElse(null);
        if (requestedUser == null) {
            return new APiResponses("NOT_FOUND", "User not found", 404);
        }

        Users authenticatedUser = (Users) authentication.getPrincipal();
        Set<Organisations> authenticatedUserOrgs = organisationServiceImplementation.organisationsList(authenticatedUser);
        Set<Organisations> requestedUserOrgs = organisationServiceImplementation.organisationsList(requestedUser);

        System.out.println("Authenticated user ID: " + authenticatedUser.getUserid());
        System.out.println("Requested user ID: " + requestedUser.getUserid());

        for (Organisations org : authenticatedUserOrgs) {
            System.out.println("Authenticated user belongs to org ID: " + org.getOrgId());
            if (requestedUserOrgs.contains(org) || id.equals(authenticatedUser.getUserid())) {
                Map<String, Object> data = generateDataForUser(requestedUser);
                return new APiResponses("SUCCESS", "User info retrieved successfully", data, 200);
            }
        }

        return new APiResponses("UNAUTHORIZED", "You are not permitted to view this", 401);
    } catch (Exception e) {
        e.printStackTrace();
        return new APiResponses("INTERNAL_SERVER_ERROR", e.getMessage(), 500);
    }
}

    private static Map<String, Object> generateDataForUser(Users users1) {
        Map<String, Object> data = new HashMap<>();
        UserRequestResponse userRequestResponse = new UserRequestResponse();
        userRequestResponse.setUserId(String.valueOf(users1.getUserid()));
        userRequestResponse.setEmail(users1.getEmail());
        userRequestResponse.setPhone(users1.getPhone());
        userRequestResponse.setFirstName(users1.getFirstName());
        userRequestResponse.setLastName(users1.getLastName());
        data.put("userId", userRequestResponse.getUserId());
        data.put("firstName", userRequestResponse.getFirstName());
        data.put("lastName", userRequestResponse.getLastName());
        data.put("email", userRequestResponse.getEmail());
        data.put("phone", userRequestResponse.getPhone());
        return data;
    }
}
