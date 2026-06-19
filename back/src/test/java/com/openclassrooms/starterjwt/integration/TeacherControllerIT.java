package com.openclassrooms.starterjwt.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour TeacherController.
 * Teste la récupération des professeurs (accès en lecture seule, authentification requise).
 */
@DisplayName("TeacherController - Tests d'intégration")
class TeacherControllerIT extends AbstractIntegrationTest {

    // ─── GET /api/teacher ───────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/teacher")
    class GetAllTeachers {

        @Test
        @DisplayName("Doit retourner la liste de tous les professeurs")
        void shouldGetAllTeachers() throws Exception {
            // Le @BeforeEach crée déjà un professeur (testTeacher)
            createTestTeacher("Marie", "Curie");
            String token = getUserToken();

            mockMvc.perform(get("/api/teacher")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                    .andExpect(jsonPath("$[*].firstName", hasItems("Jean", "Marie")));
        }

        @Test
        @DisplayName("Doit retourner une liste vide s'il n'y a aucun professeur")
        void shouldReturnTeachersList() throws Exception {
            String token = getUserToken();

            mockMvc.perform(get("/api/teacher")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("Doit retourner 401 sans authentification")
        void shouldFailWithoutAuthentication() throws Exception {
            mockMvc.perform(get("/api/teacher"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ─── GET /api/teacher/{id} ──────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/teacher/{id}")
    class GetTeacherById {

        @Test
        @DisplayName("Doit retourner un professeur existant par son ID")
        void shouldGetTeacherById() throws Exception {
            String token = getUserToken();

            mockMvc.perform(get("/api/teacher/" + testTeacher.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(testTeacher.getId()))
                    .andExpect(jsonPath("$.firstName").value("Jean"))
                    .andExpect(jsonPath("$.lastName").value("Dupont"));
        }

        @Test
        @DisplayName("Doit retourner 404 pour un professeur inexistant")
        void shouldFailToGetNonExistentTeacher() throws Exception {
            String token = getUserToken();

            mockMvc.perform(get("/api/teacher/99999")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Doit retourner 400 si l'ID n'est pas un nombre")
        void shouldFailWithInvalidTeacherIdFormat() throws Exception {
            String token = getUserToken();

            mockMvc.perform(get("/api/teacher/not-a-number")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Doit retourner 401 sans authentification")
        void shouldFailWithoutAuthentication() throws Exception {
            mockMvc.perform(get("/api/teacher/" + testTeacher.getId()))
                    .andExpect(status().isUnauthorized());
        }
    }
}

