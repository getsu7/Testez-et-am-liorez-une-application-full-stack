package com.openclassrooms.starterjwt.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour AuthController.
 * Teste l'authentification et l'inscription via l'API REST avec une base H2 en mémoire.
 */
@DisplayName("AuthController - Tests d'intégration")
class AuthControllerIT extends AbstractIntegrationTest {

    // -------------------------------------------------------------------------
    // POST /api/auth/login
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("POST /api/auth/login")
    class Login {

        @Test
        @DisplayName("Doit authentifier un utilisateur avec des identifiants valides")
        void shouldAuthenticateUserWithValidCredentials() throws Exception {
            var loginRequest = Map.of(
                    "email", TEST_USER_EMAIL,
                    "password", TEST_USER_PASSWORD
            );

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty())
                    .andExpect(jsonPath("$.type").value("Bearer"))
                    .andExpect(jsonPath("$.username").value(TEST_USER_EMAIL))
                    .andExpect(jsonPath("$.admin").value(false));
        }

        @Test
        @DisplayName("Doit authentifier un administrateur et retourner admin=true")
        void shouldAuthenticateAdminUser() throws Exception {
            var loginRequest = Map.of(
                    "email", TEST_ADMIN_EMAIL,
                    "password", TEST_ADMIN_PASSWORD
            );

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty())
                    .andExpect(jsonPath("$.admin").value(true));
        }

        @Test
        @DisplayName("Doit retourner 401 avec un mot de passe incorrect")
        void shouldFailAuthenticationWithInvalidCredentials() throws Exception {
            var loginRequest = Map.of(
                    "email", TEST_USER_EMAIL,
                    "password", "wrongpassword"
            );

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Doit retourner 401 avec un email inexistant")
        void shouldFailAuthenticationWithNonExistentUser() throws Exception {
            var loginRequest = Map.of(
                    "email", "nobody@example.com",
                    "password", "anypassword"
            );

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Doit retourner 400 quand l'email est absent")
        void shouldFailLoginWithMissingEmail() throws Exception {
            var loginRequest = Map.of("password", TEST_USER_PASSWORD);

            mockMvc.perform(post("/api/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    // -------------------------------------------------------------------------
    // POST /api/auth/register
    // -------------------------------------------------------------------------

    @Nested
    @DisplayName("POST /api/auth/register")
    class Register {

        @Test
        @DisplayName("Doit inscrire un nouvel utilisateur avec succès")
        void shouldRegisterNewUserSuccessfully() throws Exception {
            var registerRequest = Map.of(
                    "email", "newuser@example.com",
                    "firstName", "Marie",
                    "lastName", "Martin",
                    "password", "Password123!"
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value("User registered successfully!"));
        }

        @Test
        @DisplayName("Doit retourner 400 si l'email est déjà utilisé")
        void shouldFailRegistrationWhenEmailAlreadyExists() throws Exception {
            // testUser a déjà cet email grâce au @BeforeEach
            var registerRequest = Map.of(
                    "email", TEST_USER_EMAIL,
                    "firstName", "John",
                    "lastName", "Doe",
                    "password", "Password123!"
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.message").value("Error: Email is already taken!"));
        }

        @Test
        @DisplayName("Doit retourner 400 si les champs obligatoires sont absents")
        void shouldFailRegistrationWithMissingRequiredFields() throws Exception {
            // Pas d'email, pas de prénom
            var registerRequest = Map.of(
                    "password", "Password123!"
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Doit retourner 400 si le format de l'email est invalide")
        void shouldFailRegistrationWithInvalidEmailFormat() throws Exception {
            var registerRequest = Map.of(
                    "email", "not-an-email",
                    "firstName", "Test",
                    "lastName", "User",
                    "password", "Password123!"
            );

            mockMvc.perform(post("/api/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isBadRequest());
        }
    }
}

