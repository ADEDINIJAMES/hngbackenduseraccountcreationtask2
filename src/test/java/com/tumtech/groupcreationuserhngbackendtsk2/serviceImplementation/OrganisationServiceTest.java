package com.tumtech.groupcreationuserhngbackendtsk2.serviceImplementation;

import com.tumtech.groupcreationuserhngbackendtsk2.apiResponse.APiResponses;
import com.tumtech.groupcreationuserhngbackendtsk2.entity.Users;
import com.tumtech.groupcreationuserhngbackendtsk2.repostory.OrganisationRepository;
import com.tumtech.groupcreationuserhngbackendtsk2.repostory.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class OrganisationServiceTest {

    @Autowired
    private UserServiceImplementation userService;

    @Autowired
    private OrganisationServiceImplementation organizationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganisationRepository organisationRepository;

    private Users user1;
    private Users user2;
    private Users savedUser1;
    private Users savedUser2;

    @BeforeEach
    public void setUp() {
        // Initialize users
        user1 = new Users();
        user2 = new Users();

        // Register first user
        user1.setFirstName("john");
        user1.setEmail("johnomo@yahoo.com");
        user1.setLastName("john");
        user1.setPassword("password@123");
        savedUser1 = userRepository.save(user1);

        // Register second user
        user2.setFirstName("Peter");
        user2.setEmail("johnmomo@yahoo.com");
        user2.setLastName("James");
        user2.setPassword("password@123");
        savedUser2 = userRepository.save(user2);
    }
@AfterEach
    public void tearDown() {
    userRepository.deleteAll();
    organisationRepository.deleteAll();

    }

    @Test
    public void testUserCannotAccessOtherUsersOrganization() {
        // Authenticate user1
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(savedUser1, null));

        // Try to access user2's organization
        String user2Id = savedUser2.getUserid(); // Ensure this is the correct method to get the user ID
        APiResponses response = userService.getUserInfo(user2Id);

        System.out.println("User 2 ID: " + user2Id);
        if (response == null) {
            System.out.println("Response is null");
        } else {
            System.out.println("Response status: " + response.getStatus());
            System.out.println("Response message: " + response.getMessage());
        }

        assert response != null;
        assertEquals("UNAUTHORIZED", response.getStatus());
        assertEquals("You are not permitted to view this", response.getMessage());
    }


}
