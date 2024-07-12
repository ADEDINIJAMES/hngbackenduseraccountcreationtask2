package com.tumtech.groupcreationuserhngbackendtsk2.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tumtech.groupcreationuserhngbackendtsk2.apiResponse.APiResponses;
import com.tumtech.groupcreationuserhngbackendtsk2.controller.AuthController;
import com.tumtech.groupcreationuserhngbackendtsk2.dto.RegisterRequest;
import com.tumtech.groupcreationuserhngbackendtsk2.repostory.OrganisationRepository;
import com.tumtech.groupcreationuserhngbackendtsk2.repostory.UserRepository;
import com.tumtech.groupcreationuserhngbackendtsk2.serviceImplementation.OrganisationServiceImplementation;
import com.tumtech.groupcreationuserhngbackendtsk2.serviceImplementation.UserServiceImplementation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrganisationRepository organisationRepository;

    @Mock
    private UserServiceImplementation userServiceImplementation;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        userRepository.deleteAll(); // Clean up the database before each test
    }

    @AfterEach
    public void cleanUp() {
        userRepository.deleteAll();
        organisationRepository.deleteAll();

        // Clean up the database after each test
    }

    @Test
    public void shouldRegisterUserSuccessfully() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.unique@example.com");  // Ensure unique email
        request.setPassword("Password@123");

        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", "uniqueId123");
        userData.put("firstName", "John");
        userData.put("lastName", "Doe");
        userData.put("email", "john.unique@example.com");
        userData.put("organizationName", "John's Organisation");

        Map<String, Object> data = new HashMap<>();
        data.put("accessToken", "dummyAccessToken");
        data.put("user", userData);

        APiResponses response = new APiResponses("success", "Registration Successful", data, 201);

        when(userServiceImplementation.registerUser(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.user.firstName").value("John"))
//                .andExpect(jsonPath("$.data.user.organizationName").value("John's Organisation"))
                .andExpect(jsonPath("$.data.accessToken").exists());
    }

    @Test
    public void shouldFailIfRequiredFieldsAreMissing() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setPassword("Password123");

        APiResponses response = new APiResponses("Validation Error", "Invalid input", new HashMap<>() {{
            put("errors", new Object[]{
                    new HashMap<String, String>() {{
                        put("field", "firstName");
                        put("message", "First name is required");
                    }}
            });
        }}, 422);

        when(userServiceImplementation.registerUser(any(RegisterRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.data.errors[0].field").value("firstName"))
                .andExpect(jsonPath("$.data.errors[0].message").value("First name is required"));
    }

    @Test
    public void shouldFailIfDuplicateEmail() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.duplicate@example.com");  // Ensure unique email
        request.setPassword("Password@123");

        APiResponses response = new APiResponses("Validation Error", "Email already exists", null, 422);

        when(userServiceImplementation.registerUser(any(RegisterRequest.class)))
                .thenReturn(new APiResponses("success", "Registration Successful", new HashMap<>(), 201))
                .thenReturn(response);  // Mock duplicate email behavior

        // First registration
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Second registration with the same email
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Email already exists"));

    }
}
