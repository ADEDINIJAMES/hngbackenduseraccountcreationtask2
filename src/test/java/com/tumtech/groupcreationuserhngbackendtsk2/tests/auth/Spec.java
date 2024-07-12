package com.tumtech.groupcreationuserhngbackendtsk2.tests.auth;

import com.tumtech.groupcreationuserhngbackendtsk2.entity.Organisations;
import com.tumtech.groupcreationuserhngbackendtsk2.entity.Users;
import com.tumtech.groupcreationuserhngbackendtsk2.repostory.OrganisationRepository;
import com.tumtech.groupcreationuserhngbackendtsk2.repostory.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class Spec {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    @AfterEach
    public void tearDown() {
        organisationRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void shouldRegisterUserSuccessfullyWithDefaultOrganisation() throws Exception {
        String userJson = "{\"firstName\":\"John\", \"lastName\":\"Doe\", \"email\":\"john.doe@example.com\", \"password\":\"Password@123\"}";

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.user.firstName").value("John"))
                .andExpect(jsonPath("$.data.accessToken").exists());

        // Fetch user from database to check organisation details
        Optional<Users> user = userRepository.findByEmail("john.doe@example.com");
        assertNotNull(user);

        // Fetch default organisation
        Organisations organisation = user.get().getOrganisations().stream().findFirst().orElse(null);
        assertNotNull(organisation);
        assertEquals("John's Organisation", organisation.getName());
    }



    @Test
    public void shouldRegisterAndLogTheUserInSuccessfully() throws Exception {
        // Step 1: Register the user
        String registrationJson = "{\"firstName\":\"John\", \"lastName\":\"Doe\", \"email\":\"john.doe@example.com\", \"password\":\"Password@123\"}";

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registrationJson))
                .andExpect(status().isCreated());

        // Step 2: Log in the user
        String loginJson = "{\"email\":\"john.doe@example.com\", \"password\":\"Password@123\"}";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.user.email").value("john.doe@example.com"))
                .andExpect(jsonPath("$.data.accessToken").exists());
    }

    @Test
    public void shouldFailIfRequiredFieldsAreMissing() throws Exception {
        String userJson = "{\"lastName\":\"Doe\", \"email\":\"john.doe@example.com\", \"password\":\"Password@123\"}";

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Invalid input"))
                .andExpect(jsonPath("$.data.errors").isArray())
                .andExpect(jsonPath("$.data.errors[0].field").value("firstName"))
                .andExpect(jsonPath("$.data.errors[0].message").value("First name is required"));
    }
    @Test
    public void shouldFailIfDuplicateEmailOrUserID() throws Exception {
        String userJson = "{\"firstName\":\"John\", \"lastName\":\"Doe\", \"email\":\"john.doe@example.com\", \"password\":\"Password@123\"}";

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userJson))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value("Validation Error"))
                .andExpect(jsonPath("$.message").value("Email already exists"));

    }
}







