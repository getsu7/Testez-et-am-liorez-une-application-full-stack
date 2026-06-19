package com.openclassrooms.starterjwt.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour UserController.
 * Teste la récupération et la suppression de comptes utilisateurs.
 */
@DisplayName("UserController - Tests d'intégration")
class UserControllerIT extends AbstractIntegrationTest {

    // ─── GET /api/user/{id} ─────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/user/{id}")
    class GetUserById {

        @Test
        @DisplayName("Doit retourner les informations d'un utilisateur existant")
        void shouldGetUserById() throws Exception {
            String token = getUserToken();

            mockMvc.perform(get("/api/user/" + testUser.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testUser.getId()))
                    .andExpect(jsonPath("$.email").value(TEST_USER_EMAIL))
                    .andExpect(jsonPath("$.firstName").value("Test"))
                    .andExpect(jsonPath("$.lastName").value("User"))
                    .andExpect(jsonPath("$.admin").value(false));
        }

        @Test
        @DisplayName("Doit retourner les informations d'un administrateur")
        void shouldGetAdminUserById() throws Exception {
            String token = getAdminToken();

            mockMvc.perform(get("/api/user/" + adminUser.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value(TEST_ADMIN_EMAIL))
                    .andExpect(jsonPath("$.admin").value(true));
        }

        @Test
        @DisplayName("Doit retourner 404 pour un utilisateur inexistant")
        void shouldFailToGetNonExistentUser() throws Exception {
            String token = getUserToken();

            mockMvc.perform(get("/api/user/99999")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Doit retourner 400 si l'ID n'est pas un nombre")
        void shouldFailWithInvalidUserIdFormat() throws Exception {
            String token = getUserToken();

            mockMvc.perform(get("/api/user/not-a-number")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Doit retourner 401 sans authentification")
        void shouldFailWithoutAuthentication() throws Exception {
            mockMvc.perform(get("/api/user/" + testUser.getId()))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ─── DELETE /api/user/{id} ──────────────────────────────────────────────

    @Nested
    @DisplayName("DELETE /api/user/{id}")
    class DeleteUserAccount {

        @Test
        @DisplayName("Doit supprimer son propre compte utilisateur")
        void shouldDeleteOwnUserAccount() throws Exception {
            String token = getUserToken();

            mockMvc.perform(delete("/api/user/" + testUser.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Doit retourner 401 si on tente de supprimer le compte d'un autre utilisateur")
        void shouldFailToDeleteAnotherUserAccount() throws Exception {
            // Authentifié en tant que testUser, mais essaie de supprimer adminUser
            String token = getUserToken();

            mockMvc.perform(delete("/api/user/" + adminUser.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("Doit retourner 404 si l'utilisateur à supprimer est inexistant")
        void shouldFailToDeleteNonExistentUser() throws Exception {
            String token = getUserToken();

            mockMvc.perform(delete("/api/user/99999")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Doit retourner 400 si l'ID est invalide")
        void shouldFailDeleteWithInvalidIdFormat() throws Exception {
            String token = getUserToken();

            mockMvc.perform(delete("/api/user/not-a-number")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Doit retourner 401 sans authentification")
        void shouldFailWithoutAuthentication() throws Exception {
            mockMvc.perform(delete("/api/user/" + testUser.getId()))
                    .andExpect(status().isUnauthorized());
        }
    }
}

