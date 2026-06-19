package com.openclassrooms.starterjwt.integration;

import com.openclassrooms.starterjwt.models.Session;
import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests d'intégration pour SessionController.
 * Teste les opérations CRUD sur les sessions ainsi que la participation des utilisateurs.
 */
@DisplayName("SessionController - Tests d'intégration")
class SessionControllerIT extends AbstractIntegrationTest {

    @Autowired
    private SessionRepository sessionRepository;

    // ─── Helpers ────────────────────────────────────────────────────────────

    /** Crée une session avec un professeur dédié pour éviter la contrainte unique OneToOne. */
    private Session createAndSaveSession(String name) {
        String shortName = name.replaceAll("\\s+", "").substring(0, Math.min(name.replaceAll("\\s+", "").length(), 15));
        Teacher dedicatedTeacher = createTestTeacher("Prof", shortName);
        Session session = Session.builder()
                .name(name)
                .date(new Date())
                .description("Description de " + name)
                .teacher(dedicatedTeacher)
                .users(new ArrayList<>())
                .build();
        return sessionRepository.saveAndFlush(session);
    }

    private Map<String, Object> buildSessionPayload(String name) {
        String shortName = name.replaceAll("\\s+", "").substring(0, Math.min(name.replaceAll("\\s+", "").length(), 15));
        Teacher t = createTestTeacher("Build", shortName);
        return Map.of(
                "name", name,
                "date", new Date().getTime(),
                "teacher_id", t.getId(),
                "description", "Description de " + name,
                "users", List.of()
        );
    }

    // ─── GET /api/session ───────────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/session")
    class GetAllSessions {

        @Test
        @DisplayName("Doit retourner la liste de toutes les sessions")
        void shouldGetAllSessions() throws Exception {
            createAndSaveSession("Yoga Matinal");
            createAndSaveSession("Méditation");
            String token = getUserToken();

            mockMvc.perform(get("/api/session")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
        }

        @Test
        @DisplayName("Doit retourner une liste vide s'il n'y a pas de sessions")
        void shouldReturnEmptyListWhenNoSessions() throws Exception {
            String token = getUserToken();

            mockMvc.perform(get("/api/session")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isArray());
        }

        @Test
        @DisplayName("Doit retourner 401 sans authentification")
        void shouldFailWithoutAuthentication() throws Exception {
            mockMvc.perform(get("/api/session"))
                    .andExpect(status().isUnauthorized());
        }
    }

    // ─── GET /api/session/{id} ──────────────────────────────────────────────

    @Nested
    @DisplayName("GET /api/session/{id}")
    class GetSessionById {

        @Test
        @DisplayName("Doit retourner une session existante par son ID")
        void shouldGetSessionById() throws Exception {
            Session session = createAndSaveSession("Yoga Flow");
            String token = getUserToken();

            mockMvc.perform(get("/api/session/" + session.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(session.getId()))
                    .andExpect(jsonPath("$.name").value("Yoga Flow"));
        }

        @Test
        @DisplayName("Doit retourner 404 pour une session inexistante")
        void shouldFailToGetNonExistentSession() throws Exception {
            String token = getUserToken();

            mockMvc.perform(get("/api/session/99999")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Doit retourner 400 si l'ID n'est pas un nombre")
        void shouldFailWithInvalidSessionIdFormat() throws Exception {
            String token = getUserToken();

            mockMvc.perform(get("/api/session/invalid-id")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isBadRequest());
        }
    }

    // ─── POST /api/session ──────────────────────────────────────────────────

    @Nested
    @DisplayName("POST /api/session")
    class CreateSession {

        @Test
        @DisplayName("Doit créer une nouvelle session avec succès")
        void shouldCreateNewSession() throws Exception {
            String token = getUserToken();
            Map<String, Object> payload = buildSessionPayload("Nouvelle Session Yoga");

            mockMvc.perform(post("/api/session")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payload)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Nouvelle Session Yoga"))
                    .andExpect(jsonPath("$.id").isNumber());
        }

        @Test
        @DisplayName("Doit retourner 400 si le nom de la session est absent")
        void shouldFailToCreateSessionWithMissingName() throws Exception {
            String token = getUserToken();
            // Payload sans 'name'
            var payload = Map.of(
                    "date", new Date().getTime(),
                    "teacher_id", testTeacher.getId(),
                    "description", "Description valide"
            );

            mockMvc.perform(post("/api/session")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payload)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Doit retourner 400 si la description est absente")
        void shouldFailToCreateSessionWithMissingDescription() throws Exception {
            String token = getUserToken();
            Teacher t = createTestTeacher("NoDesc", "Teacher2");
            var payload = Map.of(
                    "name", "Session Sans Desc",
                    "date", new Date().getTime(),
                    "teacher_id", t.getId()
            );

            mockMvc.perform(post("/api/session")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payload)))
                    .andExpect(status().isBadRequest());
        }
    }

    // ─── PUT /api/session/{id} ──────────────────────────────────────────────

    @Nested
    @DisplayName("PUT /api/session/{id}")
    class UpdateSession {

        @Test
        @DisplayName("Doit mettre à jour une session existante")
        void shouldUpdateSession() throws Exception {
            Session session = createAndSaveSession("Session Originale");
            String token = getUserToken();

            Map<String, Object> payload = Map.of(
                    "name", "Session Modifiée",
                    "date", new Date().getTime(),
                    "teacher_id", testTeacher.getId(),
                    "description", "Description mise à jour",
                    "users", List.of()
            );

            mockMvc.perform(put("/api/session/" + session.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payload)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("Session Modifiée"))
                    .andExpect(jsonPath("$.description").value("Description mise à jour"));
        }

        @Test
        @DisplayName("Doit retourner 400 si l'ID est invalide")
        void shouldFailWithInvalidIdFormat() throws Exception {
            String token = getUserToken();
            Map<String, Object> payload = buildSessionPayload("Test");

            mockMvc.perform(put("/api/session/abc")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(payload)))
                    .andExpect(status().isBadRequest());
        }
    }

    // ─── DELETE /api/session/{id} ───────────────────────────────────────────

    @Nested
    @DisplayName("DELETE /api/session/{id}")
    class DeleteSession {

        @Test
        @DisplayName("Doit supprimer une session existante")
        void shouldDeleteSession() throws Exception {
            Session session = createAndSaveSession("Session à supprimer");
            String token = getUserToken();

            mockMvc.perform(delete("/api/session/" + session.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Doit retourner 400 si l'ID est invalide")
        void shouldFailDeleteWithInvalidIdFormat() throws Exception {
            String token = getUserToken();

            mockMvc.perform(delete("/api/session/not-a-number")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isBadRequest());
        }
    }

    // ─── POST /api/session/{id}/participate/{userId} ────────────────────────

    @Nested
    @DisplayName("POST /api/session/{id}/participate/{userId}")
    class ParticipateInSession {

        @Test
        @DisplayName("Doit ajouter un utilisateur à une session")
        void shouldAddUserToSession() throws Exception {
            Session session = createAndSaveSession("Session Participative");
            String token = getUserToken();

            mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + testUser.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Doit retourner 400 si l'utilisateur participe déjà")
        void shouldFailToAddUserAlreadyParticipating() throws Exception {
            Session session = createAndSaveSession("Session Doublons");
            String token = getUserToken();

            // Première participation
            mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + testUser.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk());

            // Deuxième participation (doit échouer)
            mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + testUser.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Doit retourner 404 si la session est inexistante")
        void shouldFailToAddUserToNonExistentSession() throws Exception {
            String token = getUserToken();

            mockMvc.perform(post("/api/session/99999/participate/" + testUser.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Doit retourner 404 si l'utilisateur est inexistant")
        void shouldFailToAddNonExistentUserToSession() throws Exception {
            Session session = createAndSaveSession("Session Utilisateur Inconnu");
            String token = getUserToken();

            mockMvc.perform(post("/api/session/" + session.getId() + "/participate/99999")
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Doit gérer plusieurs utilisateurs participant à la même session")
        void shouldHandleMultipleUsersParticipating() throws Exception {
            Session session = createAndSaveSession("Session Collective");
            String userToken = getUserToken();
            String adminToken = getAdminToken();

            // Ajout du premier utilisateur
            mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + testUser.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                    .andExpect(status().isOk());

            // Ajout du second utilisateur
            mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + adminUser.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                    .andExpect(status().isOk());

            // Vérification : la session a bien 2 participants
            mockMvc.perform(get("/api/session/" + session.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + userToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.users", hasSize(2)));
        }
    }

    // ─── DELETE /api/session/{id}/participate/{userId} ──────────────────────

    @Nested
    @DisplayName("DELETE /api/session/{id}/participate/{userId}")
    class NoLongerParticipate {

        @Test
        @DisplayName("Doit retirer un utilisateur d'une session")
        void shouldRemoveUserFromSession() throws Exception {
            Session session = createAndSaveSession("Session Départ");
            String token = getUserToken();

            // Inscription préalable
            mockMvc.perform(post("/api/session/" + session.getId() + "/participate/" + testUser.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk());

            // Désinscription
            mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/" + testUser.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Doit retourner 400 si l'utilisateur ne participe pas")
        void shouldFailToRemoveNonParticipatingUser() throws Exception {
            Session session = createAndSaveSession("Session Non Inscrit");
            String token = getUserToken();

            mockMvc.perform(delete("/api/session/" + session.getId() + "/participate/" + testUser.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Doit retourner 404 si la session est inexistante")
        void shouldFailToRemoveFromNonExistentSession() throws Exception {
            String token = getUserToken();

            mockMvc.perform(delete("/api/session/99999/participate/" + testUser.getId())
                            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                    .andExpect(status().isNotFound());
        }
    }
}

