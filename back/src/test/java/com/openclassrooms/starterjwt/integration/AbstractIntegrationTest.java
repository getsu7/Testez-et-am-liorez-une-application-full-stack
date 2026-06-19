package com.openclassrooms.starterjwt.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.models.User;
import com.openclassrooms.starterjwt.payload.request.LoginRequest;
import com.openclassrooms.starterjwt.payload.response.JwtResponse;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Classe de base pour les tests d'intégration.
 * Utilise H2 en mémoire via le profil "test" et effectue un rollback de chaque test.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractIntegrationTest {

    protected static final String TEST_USER_EMAIL = "testuser@example.com";
    protected static final String TEST_USER_PASSWORD = "Test1234!";
    protected static final String TEST_ADMIN_EMAIL = "admin@example.com";
    protected static final String TEST_ADMIN_PASSWORD = "Admin1234!";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected TeacherRepository teacherRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    protected User testUser;
    protected User adminUser;
    protected Teacher testTeacher;

    @BeforeEach
    void setUpBaseData() {
        testUser = createTestUser(TEST_USER_EMAIL, TEST_USER_PASSWORD, false);
        adminUser = createTestUser(TEST_ADMIN_EMAIL, TEST_ADMIN_PASSWORD, true);
        testTeacher = createTestTeacher("Jean", "Dupont");
    }

    /**
     * Crée et persiste un utilisateur de test.
     */
    protected User createTestUser(String email, String password, boolean admin) {
        User user = User.builder()
                .email(email)
                .firstName("Test")
                .lastName("User")
                .password(passwordEncoder.encode(password))
                .admin(admin)
                .build();
        return userRepository.saveAndFlush(user);
    }

    /**
     * Crée et persiste un professeur de test.
     */
    protected Teacher createTestTeacher(String firstName, String lastName) {
        Teacher teacher = Teacher.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();
        return teacherRepository.saveAndFlush(teacher);
    }

    /**
     * Effectue un login et retourne le token JWT correspondant.
     */
    protected String obtainJwtToken(String email, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        JwtResponse jwtResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), JwtResponse.class);
        return jwtResponse.getToken();
    }

    /**
     * Retourne le token JWT de l'utilisateur de test standard.
     */
    protected String getUserToken() throws Exception {
        return obtainJwtToken(TEST_USER_EMAIL, TEST_USER_PASSWORD);
    }

    /**
     * Retourne le token JWT de l'administrateur de test.
     */
    protected String getAdminToken() throws Exception {
        return obtainJwtToken(TEST_ADMIN_EMAIL, TEST_ADMIN_PASSWORD);
    }
}

